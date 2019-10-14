package cn.edu.buaa.act.management.marathon;

import cn.edu.buaa.act.management.common.*;
import cn.edu.buaa.act.management.common.Properties;
import cn.edu.buaa.act.management.model.DeploymentRequest;
import cn.edu.buaa.act.management.service.ServiceDeploy;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.MarathonException;
import mesosphere.marathon.client.model.v2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.buaa.act.management.common.Properties.*;


/**
 * MesosAppDeploy
 *
 * @author wsj
 * @date 2018/9/21
 */
@Service
public class MesosServiceDeploy implements ServiceDeploy {
    private static final Logger logger = LoggerFactory.getLogger(MesosServiceDeploy.class);

    private MarathonDeployProperties properties = new MarathonDeployProperties();


    @Autowired
    Marathon marathon;

    private String getAppId(DeploymentRequest request) {
        //暂时不用grout
        String groupId = request.getDeploymentProperties().get(Properties.GROUP_PROPERTY);

        String name = request.getDefinition().getName();
        if (groupId != null) {
            return  groupId + "/" + name;
        }
        else {
            return name;
        }
    }
    private Container createContainer(DeploymentRequest request) {
        Container container = new Container();
        Docker docker = new Docker();
        String image = null;
        try {
            image = request.getResource().getURI().getSchemeSpecificPart();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to get URI for " + request.getResource(), e);
        }
        logger.info("Using Docker image: " + image);
        docker.setImage(image);

        String portMap= request.getDeploymentProperties().get(PORTMAP_PROPERTY);
        List<Port> portList = new ArrayList<>();
//        Port portx = new Port();
//        portx.setContainerPort(0);
//        portx.setHostPort(8080);
//        portList.add(portx);
        if(StringUtils.hasText(portMap)){
            System.out.println(portMap);
            JSONArray jsonArray = JSONArray.parseArray(portMap);
            for (Object aJsonArray : jsonArray) {
                String[] strings = ((String) aJsonArray).split(":", 2);
                Assert.isTrue(strings.length == 2, "Invalid environment variable declared: " + (String) aJsonArray);
                Port port = new Port();
                port.setContainerPort(Integer.parseInt(strings[0]));
                port.setHostPort(Integer.parseInt(strings[1]));
                portList.add(port);
            }
        }
        docker.setPortMappings(portList);
        docker.setNetwork("HOST");
        container.setDocker(docker);
        container.setType("DOCKER");
        return container;
    }


    private String prefix(String property) {
        // return MarathonDeployProperties.PREFIX + "." + property;
        return property;
    }

    private Collection<String> deduceUris(DeploymentRequest request) {
        Set<String> additional = StringUtils.commaDelimitedListToSet(request.getDeploymentProperties().get(Properties.URIS_PROPERTY));
        HashSet<String> result = new HashSet<>(additional);
        result.addAll(properties.getUris());
        return result;
    }
    private Collection<Constraint> deduceConstraints(DeploymentRequest request) {
        Set<Constraint> requestSpecific = StringUtils.commaDelimitedListToSet(request.getDeploymentProperties().get(prefix("constraints")))
                .stream().map(Constraint::new).collect(Collectors.toSet());
        Set<Constraint> result = new HashSet<>(properties.getConstraints());
        result.addAll(requestSpecific);
        return result;
    }

    private Double deduceCpus(DeploymentRequest request) {
        String override = request.getDeploymentProperties().get(CPU_PROPERTY);
        return override != null ? Double.valueOf(override) : properties.getCpu();
    }
    private Double deduceMemory(DeploymentRequest request) {
        String override = request.getDeploymentProperties().get(MEMORY_PROPERTY);
        return override != null ? Double.valueOf(override) : properties.getMemory();
    }
    private Integer deduceInstances(DeploymentRequest request) {
        String value = request.getDeploymentProperties().get(COUNT_PROPERTY);
        return value != null ? Integer.valueOf(value) : Integer.valueOf("1");
    }

    private void createAppDeployment(DeploymentRequest request, String deploymentId, Container container, Integer index) {
        App app = new App();
        app.setContainer(container);
        app.setId(deploymentId);

        Map<String, Object> env = new HashMap<>();
        JSONObject jsonEnv = JSONObject.parseObject(request.getDeploymentProperties().get(Properties.ENV_PROPERTY));
        env.putAll(jsonEnv);

        //添加默认的环境变量
        for (String envVar : properties.getEnvironmentVariables()) {
            String[] strings = envVar.split("=", 2);
            Assert.isTrue(strings.length == 2, "Invalid environment variable declared: " + envVar);
            env.put(strings[0], strings[1]);
        }
        if (index != null) {
            env.put(INSTANCE_INDEX_PROPERTY, index.toString());
        }
        app.setEnv(env);

        Collection<String> uris = deduceUris(request);
        // app.setUris(uris);

        Collection<Constraint> constraints = deduceConstraints(request);
        app.setConstraints(constraints.stream().map(Constraint::toStringList).collect(Collectors.toList()));

        Double cpus = deduceCpus(request);
        Double memory = deduceMemory(request);
        Integer instances = index == null ? deduceInstances(request) : 1;
        app.setCpus(cpus);
        app.setMem(memory);
        app.setInstances(instances);
        // app.setDisk(0.0);
        app.setCmd(null);
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setPath("/health");
        healthCheck.setGracePeriodSeconds(300);
        app.setHealthChecks(Arrays.asList(healthCheck));

        logger.debug("Creating app with definition:\n" + app.toString());
        try {
            if(marathon==null){
                System.out.println("null marathon");
            }
        }
        catch (MarathonException e) {
            throw e;
        }
    }

    @Override
    public String deploy(DeploymentRequest request) {

        logger.info(String.format("Deploying app: %s", request.getDefinition().getName()));

        String appId = getAppId(request);

        boolean indexed = Boolean.valueOf(request.getDeploymentProperties().get(INDEXED_PROPERTY_KEY));

        if (indexed) {
            try {
                //如果indexd，意味着不能有group
                Group group = marathon.getGroup(appId);
                throw new IllegalStateException(
                        String.format("App '%s' is already deployed", request.getDefinition().getName()));
            } catch (MarathonException ignore) {}
            Container container = createContainer(request);
            String countProperty = request.getDeploymentProperties().get(Properties.COUNT_PROPERTY);
            int count = (countProperty != null) ? Integer.parseInt(countProperty) : 1;
            for (int i = 0; i < count; i++) {
                String instanceId = appId + "/" + request.getDefinition().getName() + "-" + i;
                createAppDeployment(request, instanceId, container, Integer.valueOf(i));
            }
        }
        else {
            ServiceStatus status = getStatus(appId);
            if (!status.getState().equals(DeploymentState.unknown)) {
                throw new IllegalStateException(
                        String.format("App '%s' is already deployed", request.getDefinition().getName()));
            }
            Container container = createContainer(request);
            createAppDeployment(request, appId, container, null);
        }
        return appId;
    }



    private void deleteAppsForGroupDeployment(String groupId) throws MarathonException {
        Group group = marathon.getGroup(groupId);
        for (App app : group.getApps()) {
            logger.debug(String.format("Deleting application %s in group %s", app.getId(), groupId));
            marathon.deleteApp(app.getId());
        }
        group = marathon.getGroup(groupId);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Group %s has %d applications and %d groups", group.getId(),
                    group.getApps().size(), group.getGroups().size()));
        }
        if (group.getApps().size() == 0 && group.getGroups().size() == 0) {
            logger.info(String.format("Deleting group: %s", groupId));
            marathon.deleteGroup(groupId,true);
        }
        deleteTopLevelGroupForDeployment(groupId);
    }
    private String extractGroupId(String appId) {
        int index = appId.lastIndexOf('/');
        String groupId = null;
        if (index > 0) {
            groupId = appId.substring(0, index);
        }
        return groupId;
    }

    private void deleteTopLevelGroupForDeployment(String id) throws MarathonException {
        String topLevelGroupId = extractGroupId(id);
        if (topLevelGroupId != null) {
            Group topGroup = marathon.getGroup(topLevelGroupId);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Top level group %s has %d applications and %d groups", topGroup.getId(),
                        topGroup.getApps().size(), topGroup.getGroups().size()));
            }
            if (topGroup.getApps().size() == 0 && topGroup.getGroups().size() == 0) {
                logger.info(String.format("Deleting group: %s", topLevelGroupId));
                marathon.deleteGroup(topLevelGroupId,true);
            }
        }
    }
    @Override
    public Boolean unDeploy(String id) {
        logger.info(String.format("Undeploying app: %s", id));
        Group group = null;
        try {
            group = marathon.getGroup(id);
        } catch (MarathonException ignore) {}
        if (group != null) {
            logger.info(String.format("Undeploying application deployments for group: %s", group.getId()));
            try {
                if (group.getGroups().size() > 0) {
                    for (Group g : group.getGroups()) {
                        deleteAppsForGroupDeployment(g.getId());
                    }
                }
                else {
                    deleteAppsForGroupDeployment(group.getId());
                }
            } catch (MarathonException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            logger.info(String.format("Undeploying application deployment: %s", id));
            try {
                ServiceStatus status = getStatus(id);
                if (status.getState().equals(DeploymentState.unknown)) {
                    throw new IllegalStateException(String.format("App '%s' is not in a deployed state", id));
                }
                App app = marathon.getApp(id).getApp();
                logger.debug(String.format("Deleting application: %s", app.getId()));
                marathon.deleteApp(id);
                deleteTopLevelGroupForDeployment(id);
            } catch (MarathonException e) {
                if (e.getMessage().contains("Not Found")) {
                    logger.debug(String.format("Caught: %s", e.getMessage()));
                    try {
                        deleteAppsForGroupDeployment(id);
                    } catch (MarathonException e2) {
                        throw new RuntimeException(e2);
                    }
                }
                else {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }


    @Override
    public Page<ServiceStatus> getStatus(Pageable pageable) {



        return null;
    }

    private ServiceStatus buildAppStatus(String id, App app) {
        logger.debug("Deployment " + id + " has " + app.getTasksRunning() + "/" + app.getInstances() + " tasks running");
        ServiceStatus.Builder result = ServiceStatus.of(id);
        int requestedInstances = app.getInstances();
        int actualInstances = 0;

        //up实例数量
        if (app.getTasks() != null) {
            for (Task task : app.getTasks()) {
                result.with(MarathonAppInstanceStatus.up(app, task));
                actualInstances++;
            }
        }
        //down实例的数量
        for (int i = actualInstances; i < requestedInstances; i++) {
            result.with(MarathonAppInstanceStatus.down(app));
        }
        return result.build();
    }
    private ServiceInstanceStatus buildInstanceStatus(String id) throws MarathonException {
        App appInstance = marathon.getApp(id).getApp();
        logger.debug("Deployment " + id + " has " + appInstance.getTasksRunning() + "/" + appInstance.getInstances() + " tasks running");
        if (appInstance.getTasks() != null) {
            // there should only be one task for this type of deployment
            MarathonAppInstanceStatus status = null;
            for (Task task : appInstance.getTasks()) {
                if (status == null) {
                    status = MarathonAppInstanceStatus.up(appInstance, task);
                }
            }
            if (status == null) {
                status = MarathonAppInstanceStatus.down(appInstance);
            }
            return status;
        }
        else {
            return MarathonAppInstanceStatus.down(appInstance);
        }
    }

    @Override
    public ServiceStatus getStatus(String id) {
        ServiceStatus status;
        try {
            //只有一个app,多个task
            App app = marathon.getApp(id).getApp();
            logger.debug(String.format("Building status for app: %s", id));
            status = buildAppStatus(id, app);
        } catch (MarathonException e) {
            if (e.getMessage().contains("Not Found")) {
                try {
                    //多个app，每个app只有一个task
                    Group group = marathon.getGroup(id);
                    logger.debug(String.format("Building status for group: %s", id));
                    ServiceStatus.Builder result = ServiceStatus.of(id);
                    for (App app : group.getApps()) {
                        result.with(buildInstanceStatus(app.getId()));
                    }
                    status = result.build();
                } catch (MarathonException e1) {
                    status = ServiceStatus.of(id).build();
                }
            }
            else {
                status = ServiceStatus.of(id).build();
            }
        }
        logger.debug(String.format("Status for app: %s is %s", id, status));
        return status;
    }
}
