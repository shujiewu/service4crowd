package cn.edu.buaa.act.management.controller;

import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.management.common.DeploymentState;
import cn.edu.buaa.act.management.common.Properties;
import cn.edu.buaa.act.management.common.ServiceInstanceStatus;
import cn.edu.buaa.act.management.common.ServiceStatus;
import cn.edu.buaa.act.management.docker.DockerResource;
import cn.edu.buaa.act.management.entity.ServiceDefinition;
import cn.edu.buaa.act.management.entity.ServiceDeployment;
import cn.edu.buaa.act.management.entity.ServiceRegistration;
import cn.edu.buaa.act.management.exception.NoServiceDefinitionException;
import cn.edu.buaa.act.management.exception.NoServiceDeploymentException;
import cn.edu.buaa.act.management.exception.NoServiceRegistrationException;
import cn.edu.buaa.act.management.model.DeploymentRequest;
import cn.edu.buaa.act.management.repository.DefinitionRepository;
import cn.edu.buaa.act.management.repository.DeploymentRepository;
import cn.edu.buaa.act.management.service.ServiceDefine;
import cn.edu.buaa.act.management.service.ServiceDeploy;
import cn.edu.buaa.act.management.service.ServiceRegistry;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * ServiceDeployController
 * 服务部署的相关操作
 * @author wsj
 * @date 2018/10/3
 */

@RestController
@RequestMapping("/service/deployment")
public class ServiceDeployController {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDeployController.class);

    @Autowired
    private DefinitionRepository repository;

    @Autowired
    private ServiceDeploy serviceDeploy;

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private DeploymentRepository deploymentRepository;

    @Autowired
    private ServiceDefine serviceDefine;


    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> unDeploy(@PathVariable("name") String name) {
        ServiceDefinition definition = this.repository.findServiceDefinitionByName(name);
        if (definition == null) {
            throw new NoServiceDefinitionException(name);
        }
        this.serviceDeploy.unDeploy(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Void> unDeployAll() {
        for (ServiceDefinition definition : this.repository.findAll()) {
            this.serviceDeploy.unDeploy(definition.getName());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "",method = RequestMethod.GET)
    public TableResultResponse<ServiceDeployment> list(Pageable pageable) {
        Page<ServiceDeployment> serviceDeploymentPage = deploymentRepository.findAll(pageable);
        List<ServiceDeployment> serviceDeploymentList =serviceDeploymentPage.getContent();
        Map<String,ServiceStatus> serviceStatusMap = new HashMap<>();
        serviceDeploymentList.stream().forEach(serviceDeployment -> {
            ServiceStatus serviceStatus;
            if(serviceStatusMap.containsKey(serviceDeployment.getAppId())){
               serviceStatus = serviceStatusMap.get(serviceDeployment.getAppId());
            }else {
                serviceStatus = serviceDeploy.getStatus(serviceDeployment.getAppId());
                serviceStatusMap.put(serviceDeployment.getAppId(),serviceStatus);
            }
            if(serviceStatus!=null){
                serviceDeployment.setStatus(serviceStatus.getState());
//                System.out.println(serviceStatus.getInstances());
//                System.out.println(serviceDeployment.getTaskId());
                //System.out.println(serviceStatus.getInstances().get(serviceDeployment.getTaskId()));
                //serviceDeployment.setStatus(serviceStatus.getInstances().get(serviceDeployment.getTaskId()).getState());
            }else{
                serviceDeployment.setStatus(DeploymentState.unknown);
            }
        });
        deploymentRepository.saveAll(serviceDeploymentList);
        return new TableResultResponse<ServiceDeployment>(serviceDeploymentPage.getTotalElements(),serviceDeploymentList);
    }


    @RequestMapping(value = "/{name}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ObjectRestResponse<Map> createDeployment(@PathVariable("name") String name, @RequestBody Map<String,String> properties) {
        Map result = new HashMap();
        ServiceDefinition serviceDefinition = serviceDefine.createServiceDefinition(name,properties);
//        int instanceCount;
//        if((instanceCount=Integer.valueOf(properties.get(INSTANCE_COUNT)))>0){
//            for(int i = 0 ;i<instanceCount;i++){
//                DeploymentRequest deploymentRequest = new DeploymentRequest();
//
//                serviceDeploy.deploy()serviceDeploy.deploy()
//            }
//
//        }
//        List<ServiceDeployment> serviceDeployments = this.deploymentRepository.findAllByDefinitionName(name);
//
//        for(ServiceDeployment serviceDeployment: serviceDeployments){
//            serviceDeployment.setStatus(serviceDeploy.getStatus(serviceDeployment.getDeploymentId()).getState());
//            this.deploymentRepository.save(serviceDeployment);
//        }
        result.put("success",true);
        return new ObjectRestResponse<>().data(result);
    }



//    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.CREATED)
//    public TableResultResponse<ServiceDeployment> info(@PathVariable("name") String name) {
//        ServiceDefinition definition = this.repository.findServiceDefinitionByName(name);
//        if (definition == null) {
//            throw new NoServiceDefinitionException(name);
//        }
//        List<ServiceDeployment> serviceDeployments = this.deploymentRepository.findAllByDefinitionName(name);
//
//        for(ServiceDeployment serviceDeployment: serviceDeployments){
//            serviceDeployment.setStatus(serviceDeploy.getStatus(serviceDeployment.getDeploymentId()).getState());
//            this.deploymentRepository.save(serviceDeployment);
//        }
//        return new TableResultResponse<>(serviceDeployments.size(),serviceDeployments);
//    }

//    @RequestMapping(value = "/{name}", method = RequestMethod.POST)
//    public ResponseEntity<Void> deploy(@PathVariable("name") String name,
//                                       @RequestBody(required = false) Map<String, String> deploymentProperties) {
//        ServiceDefinition definition = this.repository.findServiceDefinitionByName(name);
//        if (definition == null) {
//            throw new NoServiceDefinitionException(name);
//        }
//        Resource resource = serviceRegistry.getServiceResource(definition.getName());
//        DeploymentRequest deploymentRequest = new DeploymentRequest(definition,resource, deploymentProperties);
//        String deployId  = this.serviceDeploy.deploy(deploymentRequest);
//        ServiceDeployment deployment = new ServiceDeployment(deployId,definition.getName(),deploymentProperties);
//        this.deploymentRepository.save(deployment);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
}
