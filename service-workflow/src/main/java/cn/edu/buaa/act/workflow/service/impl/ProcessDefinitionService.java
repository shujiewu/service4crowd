package cn.edu.buaa.act.workflow.service.impl;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.workflow.domain.Model;
import cn.edu.buaa.act.workflow.exception.InternalServerErrorException;
import cn.edu.buaa.act.workflow.service.ModelService;
import cn.edu.buaa.act.workflow.util.DeployBpmnJsonConverter;
import cn.edu.buaa.act.workflow.util.SecurityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static cn.edu.buaa.act.workflow.common.Constant.MODEL_STATUS_PUBLISH;


@Service
public class ProcessDefinitionService{

    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ModelService modelService;

    @Autowired
    ProcessEngineConfigurationImpl processEngineConfiguration;

    @Autowired
    ExecuteProcessService executeProcessService;

    protected DeployBpmnJsonConverter bpmnJsonConverter = new DeployBpmnJsonConverter();
    private final Logger log = LoggerFactory.getLogger(ProcessDefinitionService.class);
    public String publishModel(String modelId){
        User user = SecurityUtils.getCurrentUserObject();
        Model model = modelService.getModel(modelId);
        String processInstanceID=null;
        if(model!=null) {
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                    .name(model.getName())
                    .key(model.getKey());
            BpmnModel bpmnModel = null;
            try {
                String editorJson = model.getModelEditorJson();
                JsonNode editorJsonNode = (JsonNode) objectMapper.readTree(editorJson);
                // System.out.println(editorJsonNode.toString());
                bpmnModel= bpmnJsonConverter.convertToBpmnModel(editorJsonNode);

                byte[] modelXML = modelService.getBpmnXML(bpmnModel);
                // System.out.println(new String(modelXML));
                //deploymentBuilder.addBpmnModel("neww",bpmnModel);
                deploymentBuilder.addInputStream(model.getKey().replaceAll(" ", "") + ".bpmn", new ByteArrayInputStream(modelXML));
                Deployment deployment = deploymentBuilder.deploy();
                ProcessDefinition processDefinition= repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();//latestVersion().
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("userId", BaseContextHandler.getUserID());
                variables.put("userToken", BaseContextHandler.getToken());
                variables.put("process_id",model.getKey().replaceAll(" ",""));
                ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);

                model.setStatus(MODEL_STATUS_PUBLISH);
                model.setProcessInstanceID(processInstance.getProcessInstanceId());
                modelService.changeModel(model);
                processInstanceID=processInstance.getProcessInstanceId();
                // asyncRun.startProcess(processInstanceID);

                executeProcessService.threadScheduler(processInstanceID,executeProcessService.createRunnable(processInstanceID), "0/5 * * * * *");
                return processInstanceID;
            } catch (Exception e) {
                log.error("Could not generate BPMN 2.0 model for " + model.getId(), e);
                throw new InternalServerErrorException("Could not generate BPMN 2.0 model");
            }
        }
        else {
            return processInstanceID;
        }
    }

//    @Autowired
//    AsyncRun asyncRun;
//
//    protected DeployBpmnJsonConverter bpmnJsonConverter = new DeployBpmnJsonConverter();
//
//    private final Logger log = LoggerFactory.getLogger(ProcessServiceImpl.class);
//    @Override
//    @Transactional
//    public String publishModel(String modelId) {
//        User user = SecurityUtils.getCurrentUserObject();
//        Model model = modelService.getModel(modelId);
//        String processInstanceID=null;
//        if(model!=null){
//            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
//                    .name(model.getName())
//                    .key(model.getKey());
//            BpmnModel bpmnModel =null;
//            try {
//                String editorJson = model.getModelEditorJson();
//                editorJson= editorJson.replaceAll("simproperties","formproperties");
//                editorJson= editorJson.replaceAll("mergeservicetaskfields","servicetaskfields");
//                // editorJson= editorJson.replaceAll("DataAssociation","SequenceFlow");
//                JsonNode editorJsonNode = (JsonNode) objectMapper.readTree(editorJson);
////                if (editorJsonNode.get("childShapes") != null) {
////                    Iterator var = editorJsonNode.get("childShapes").iterator();
////                    while(var.hasNext()){
////                        JsonNode jsonChildNode = (JsonNode)var.next();
////                        if(jsonChildNode.get("properties")!=null){
////                            JsonNode property = jsonChildNode.get("properties");
////                            property.path("result").
////                        }
////                    }
////                }
//                // log.info("轉化開始"+ bpmnJsonConverter.getwww().get("ServiceTask").getName());
//                bpmnModel= bpmnJsonConverter.convertToBpmnModel(editorJsonNode);
//                //System.out.println((((ServiceTask)bpmnModel.getFlowElement("sid-B3D26EE7-0718-4E67-B61C-833E7E1A9CA2")).getCustomProperties().size()));
//                //System.out.println((((ServiceTask)bpmnModel.getFlowElement("sid-B3D26EE7-0718-4E67-B61C-833E7E1A9CA2")).getFieldExtensions().size()));
//                byte[] modelXML = modelService.getBpmnXML(bpmnModel);
//                System.out.println(new String(modelXML));
//                //deploymentBuilder.addBpmnModel("neww",bpmnModel);
//                deploymentBuilder.addInputStream(model.getKey().replaceAll(" ", "") + ".bpmn", new ByteArrayInputStream(modelXML));
//                Deployment deployment = deploymentBuilder.deploy();
//
//                ProcessDefinition processDefinition= repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();//latestVersion().
//                Map<String, Object> variables = new HashMap<String, Object>();
//                variables.put("userId", BaseContextHandler.getUserID());
//                variables.put("process_id",model.getKey().replaceAll(" ",""));
//                ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);
//             //   asyncRun.getActivity(processInstance.getProcessDefinitionId());
//                runtimeService.suspendProcessInstanceById(processInstance.getProcessInstanceId());
//                model.setStatus(MODEL_STATUS_PUBLISH);
//                model.setProcessInstanceID(processInstance.getProcessInstanceId());
//                modelService.changeModel(model);
//                processInstanceID=processInstance.getProcessInstanceId();
//                asyncRun.startProcess(processInstanceID);
//                return processInstanceID;
//            } catch (Exception e) {
//                log.error("Could not generate BPMN 2.0 model for " + model.getId(), e);
//                throw new InternalServerErrorException("Could not generate BPMN 2.0 model");
//            }
//        }
//        else {
//            return processInstanceID;
//        }
//    }
    public Map<String, String> getRuntimeProcessInfo(String processInstanceID){
        Map result =new HashMap();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceID).singleResult();
        result.put("processName",historicProcessInstance.getProcessDefinitionName());
        result.put("startTime",historicProcessInstance.getStartTime());
        result.put("endTime",historicProcessInstance.getEndTime());
        if(historicProcessInstance.getEndTime()!=null) {
            result.put("processStatus", "Completed");
        } else {
            result.put("processStatus", "Run");
        }
        return result;
    }
    public Boolean changeModelStatus(String processInstanceID,String status){
        Model model = modelService.getModelByProcessInstanceID(processInstanceID);
        model.setStatus(status);
        return modelService.changeModel(model) != null;
    }
}