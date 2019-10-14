package cn.edu.buaa.act.workflow.controller;

import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.workflow.model.ProcessDefinitionRepresentation;
import cn.edu.buaa.act.workflow.service.impl.ProcessDefinitionService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.edu.buaa.act.workflow.common.Constant.MODEL_STATUS_COMPLETE;

/**
 * ProcessDefinitionController
 *
 * @author wsj
 * @date 2018/10/7
 */
@RestController
@RequestMapping(value = "/workflow")
public class ProcessDefinitionController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    protected ProcessDefinitionService processService;
    @RequestMapping(value = "/models/{modelId}/publish", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> publishProcess(@PathVariable("modelId") String modelId) {
        Map map=new HashMap<>();
        String processInstanceID =processService.publishModel(modelId);
        if(processInstanceID!=null){
            map.put("success",true);
            map.put("processInstanceID",processInstanceID);
        }
        else{
            map.put("success",false);
            map.put("processInstanceID",null);
        }
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

//    @RequestMapping(value = "/processDefinition/{processDefinitionId}/startForm", method = RequestMethod.GET, produces = "application/json")
//    public FormDefinition getProcessDefinitionStartForm(HttpServletRequest request) {
//        return super.getProcessDefinitionStartForm(request);
//    }
    @RequestMapping(value = "/processDefinition", method = RequestMethod.GET)
    public TableResultResponse<ProcessDefinitionRepresentation> getProcessDefinitions(@RequestParam(value="latest", required=false) Boolean latest,
                                                                                      @RequestParam(value="deploymentKey", required=false) String deploymentKey) {
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();

        if (deploymentKey != null) {
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentKey(deploymentKey).latest().singleResult();

            if (deployment != null) {
                definitionQuery.deploymentId(deployment.getId());
            } else {
                return new TableResultResponse<>();
            }

        } else {
            if (latest != null && latest) {
                definitionQuery.latestVersion();
            }
        }
        List<ProcessDefinition> definitions = definitionQuery.list();
        List<ProcessDefinitionRepresentation> result = definitions.stream().map(ProcessDefinitionRepresentation::new).collect(Collectors.toList());
        return new TableResultResponse<>(result.size(),result);
    }


    @RequestMapping(value = "/processInstance/{processInstanceId}/info", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> getProcessInfo(@PathVariable("processInstanceId") String processInstanceId) {
        Map map=new HashMap<>();
        map= processService.getRuntimeProcessInfo(processInstanceId);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }
    @RequestMapping(value = "/processInstance/{processInstanceId}/complete", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> processComplete(@PathVariable("processInstanceId") String processInstanceId) {
        Map map=new HashMap<>();
        processService.changeModelStatus(processInstanceId,MODEL_STATUS_COMPLETE);
        map= processService.getRuntimeProcessInfo(processInstanceId);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }


//    @RequestMapping(value = "/processInstance/{processInstanceId}/task/{taskId}/complete", method = RequestMethod.POST)
//    public ResponseEntity<Object> taskComplete(@PathVariable("processInstanceId") String processInstanceId,@PathVariable("taskId") String taskId) {
//        Map map=new HashMap<>();
//        processService.changeModelStatus(processInstanceId,MODEL_STATUS_COMPLETE);
//        map= processService.getRuntimeProcessInfo(processInstanceId);
//        return new ResponseEntity<Object>(map, HttpStatus.OK);
//    }
}
