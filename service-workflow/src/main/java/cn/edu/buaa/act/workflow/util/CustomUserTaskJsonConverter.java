package cn.edu.buaa.act.workflow.util;

import cn.edu.buaa.act.workflow.model.ServiceOutputs;
import cn.edu.buaa.act.workflow.model.ServiceParameters;
import cn.edu.buaa.act.workflow.service.impl.ExecuteProcessService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.activiti.editor.language.json.converter.UserTaskJsonConverter;

import java.util.Map;

/**
 * CustomUserTaskJsonConverter
 *
 * @author wsj
 * @date 2018/10/22
 */
public class CustomUserTaskJsonConverter extends UserTaskJsonConverter {
    public static void fillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put("UserTask", CustomUserTaskJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, CustomUserTaskJsonConverter.class);
    }

    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap){
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap);
        UserTask userTask = (UserTask) flowElement;
        String atomicName = this.getPropertyValueAsString("name",elementNode);
        String serviceName = this.getPropertyValueAsString("servicename",elementNode);
        JsonNode servicePara = this.getProperty("servicepara",elementNode);
        JsonNode serviceOutput = this.getProperty("serviceoutput",elementNode);
        ServiceParameters serviceParameters = JSONObject.parseObject(servicePara.toString(),ServiceParameters.class);
        ServiceOutputs serviceOutputs = JSONObject.parseObject(serviceOutput.toString(),ServiceOutputs.class);
        serviceParameters.setMicroServiceName(serviceName);
        serviceOutputs.setMicroServiceName(serviceName);
        System.out.println(serviceName+11);
        System.out.println(atomicName+12);

        JSONObject jsonProcessProperty = JSONObject.parseObject(modelNode.toString());
        String processId = jsonProcessProperty.getJSONObject("properties").get("process_id").toString();
        String resourceId = this.getValueAsString("resourceId",elementNode);
        // System.out.println(processId+resourceId);
        ExecuteProcessService.serviceParametersMap.put(processId+resourceId,serviceParameters);
        ExecuteProcessService.serviceOutputsMap.put(processId+resourceId,serviceOutputs);
        return flowElement;
    }
}
