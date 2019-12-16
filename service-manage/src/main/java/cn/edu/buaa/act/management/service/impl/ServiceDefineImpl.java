package cn.edu.buaa.act.management.service.impl;

import cn.edu.buaa.act.management.common.Properties;
import cn.edu.buaa.act.management.common.ServiceInstanceStatus;
import cn.edu.buaa.act.management.common.ServiceStatus;
import cn.edu.buaa.act.management.common.ServiceType;
import cn.edu.buaa.act.management.docker.DockerResource;
import cn.edu.buaa.act.management.entity.ServiceDefinition;
import cn.edu.buaa.act.management.entity.ServiceDeployment;
import cn.edu.buaa.act.management.entity.ServiceRegistration;
import cn.edu.buaa.act.management.exception.NoServiceDefinitionException;
import cn.edu.buaa.act.management.exception.NoServiceRegistrationException;
import cn.edu.buaa.act.management.exception.ParamException;
import cn.edu.buaa.act.management.exception.ServiceAlreadyDefinedException;
import cn.edu.buaa.act.management.model.DeploymentRequest;
import cn.edu.buaa.act.management.repository.DefinitionRepository;
import cn.edu.buaa.act.management.repository.DeploymentRepository;
import cn.edu.buaa.act.management.service.ServiceDefine;
import cn.edu.buaa.act.management.service.ServiceDeploy;
import cn.edu.buaa.act.management.service.ServiceRegistry;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * ServiceDefineImpl
 *
 * @author wsj
 * @date 2018/10/3
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ServiceDefineImpl implements ServiceDefine {
    public static final String SECTION_SEPARATOR = "-";

    @Autowired
    ServiceDeploy serviceDeploy;
    @Autowired
    ServiceRegistry serviceRegistry;
    @Autowired
    DefinitionRepository definitionRepository;

    @Autowired
    DeploymentRepository deploymentRepository;

    @Override
    public Page<ServiceDefinition> findDefinitionByNameLike(Pageable pageable, String searchName) {
        Page<ServiceDefinition> serviceDefinitions;
        if (searchName != null) {
            serviceDefinitions = definitionRepository.findServiceDefinitionByNameLike(pageable,searchName);
            serviceDefinitions = new PageImpl<>(serviceDefinitions.getContent(), pageable,
                    serviceDefinitions.getTotalElements());
        } else {
            serviceDefinitions = definitionRepository.findAll(pageable);
        }
        return serviceDefinitions;
    }

    @Override
    public ServiceDefinition createServiceDefinition(String name, Map<String, String> properties) {
        //这里定义需要修改
        ServiceDefinition definition = new ServiceDefinition();
        String registerName = properties.get("registerName");
        String type = properties.get("type");
        String version = properties.get("version");
        if(name==null||registerName==null||type==null||version==null){
            throw new ParamException();
        }
        String definitionName = (type+ SECTION_SEPARATOR+ registerName +SECTION_SEPARATOR+ version + SECTION_SEPARATOR + name).toLowerCase();

        // String definitionName = type+ registerName + version +  name;
        definition.setName(definitionName);
        if(this.definitionRepository.findServiceDefinitionByName(definitionName)!=null){
            throw new ServiceAlreadyDefinedException(definition);
        }
        ServiceRegistration serviceRegistration = serviceRegistry.find(registerName,type,version);
        if (serviceRegistration == null) {
            throw new NoServiceRegistrationException(name,type,version);
        }
        definition.setProperties(JSONObject.toJSONString(properties));
        definition.setRegisteredId(serviceRegistration.getId());
        definition.setRegisteredName(serviceRegistration.getName());

        definition = definitionRepository.save(definition);



        Map<String, String> deploymentProperties =new HashMap<>();
        String property = properties.get("property");
        JSONObject jsonObject = JSONObject.parseObject(property);
        deploymentProperties.put(Properties.CPU_PROPERTY,jsonObject.getString(Properties.CPU_PROPERTY));
        deploymentProperties.put(Properties.MEMORY_PROPERTY,jsonObject.getString(Properties.MEMORY_PROPERTY));

        deploymentProperties.put(Properties.INDEXED_PROPERTY_KEY,jsonObject.getString(Properties.INDEXED_PROPERTY_KEY));
        deploymentProperties.put(Properties.COUNT_PROPERTY,properties.get(Properties.COUNT_PROPERTY));
        deploymentProperties.put(Properties.PORTMAP_PROPERTY,jsonObject.getString(Properties.PORTMAP_PROPERTY));
        deploymentProperties.put(Properties.ENV_PROPERTY,jsonObject.getString(Properties.ENV_PROPERTY));
        deploymentProperties.put(Properties.URIS_PROPERTY,jsonObject.getString(Properties.URIS_PROPERTY));
        //System.out.println(deploymentProperties);
        //resourt

        Resource docker =new DockerResource(serviceRegistration.getUri());
        try {
            System.out.println(docker.getURI().getSchemeSpecificPart());
        } catch (IOException e) {
            e.printStackTrace();
        }
        DeploymentRequest deploymentRequest =new DeploymentRequest(definition,docker,deploymentProperties);
        String appId = serviceDeploy.deploy(deploymentRequest);

//        ServiceStatus serviceStatus = serviceDeploy.getStatus(appId);
//        serviceStatus.getInstances().
//
//        Integer count = Integer.valueOf(deploymentProperties.get(Properties.COUNT_PROPERTY));
//        for(int i = 0; i<count;count++){
//            ServiceDeployment serviceDeployment = new ServiceDeployment();
//            serviceDeployment.setAppId(appId);
//            serviceDeployment.setStatus(serviceDeploy.get);
//        }


        ServiceStatus serviceStatus = serviceDeploy.getStatus(appId);
        ServiceDeployment serviceDeployment = new ServiceDeployment();
        serviceDeployment.setAppId(appId);
        serviceDeployment.setDefinitionId(definition.getId());
        serviceDeployment.setStatus(serviceStatus.getState());
        serviceDeployment.setDeployTime(new Date());
        deploymentRepository.save(serviceDeployment);
//        List<ServiceDeployment> deploymentList = new ArrayList<>();
//        for(Map.Entry <String, ServiceInstanceStatus> statusEntry:serviceStatus.getInstances().entrySet()){
//            ServiceDeployment serviceDeployment = new ServiceDeployment();
//            serviceDeployment.setAppId(appId);
//            serviceDeployment.setDefinitionId(definition.getId());
//            serviceDeployment.setStatus(statusEntry.getValue().getState());
//            serviceDeployment.setTaskId(statusEntry.getKey());
//            serviceDeployment.setDeployTime(new Date());
//            deploymentList.add(serviceDeployment);
//        }
//        deploymentRepository.saveAll(deploymentList);
        return definition;
    }

    @Override
    public ServiceDefinition createServiceApp(String definitionName, String registeredName, boolean deploy) {
        //这里定义需要修改
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        final String serviceType = ServiceType.PROCESSOR;
        if (!serviceRegistry.exist(registeredName, serviceType)) {
            throw new NoServiceRegistrationException(registeredName, serviceType);
        }
        if(this.definitionRepository.findServiceDefinitionByName(definitionName)!=null){
            throw new ServiceAlreadyDefinedException(serviceDefinition);
        }
        if (deploy) {
            // this.serviceDeploy.deploy(serviceDefinition);
        }
        return serviceDefinition;
    }

    @Override
    public void deleteDefinition(String name) {
        if (this.definitionRepository.findServiceDefinitionByName(name) == null) {
            throw new NoServiceDefinitionException(name);
        }
        serviceDeploy.unDeploy(name);
        this.definitionRepository.deleteServiceDefinitionByName(name);
    }

    @Override
    public ServiceDefinition findDefinitionByName(String name) {
        return this.definitionRepository.findServiceDefinitionByName(name);
    }
}
