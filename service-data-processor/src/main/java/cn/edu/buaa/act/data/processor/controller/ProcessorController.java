package cn.edu.buaa.act.data.processor.controller;

import cn.edu.buaa.act.common.context.BaseContextHandler;
//import cn.edu.buaa.act.data.entity.ServiceResultEntity;
import cn.edu.buaa.act.data.processor.feign.IWorkflowService;
import cn.edu.buaa.act.data.processor.feign.IDataCoreService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/processor")
public class ProcessorController {
    private static Logger logger = LoggerFactory.getLogger(ProcessorController.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final String runtimeOutputExchangeName = "RUNTIME_REQUEST";
    private static final String runtimeInputExchangeName = "RUNTIME_RESPONSE";

    @RequestMapping(value = "/{processorName}/{version}", method = RequestMethod.POST)
    public ResponseEntity<Map> executeProcessor(@PathVariable String processorName, @PathVariable("version") String version, @RequestBody Map<String, Object> body) {
        Map<String, Object> varMap = body;//.toSingleValueMap();
        Map<String, Object> result = new HashMap<>();
        logger.info(processorName + version);
        JSONObject projectInfo = new JSONObject();
        projectInfo.put("projectName",processorName);
        projectInfo.put("version",version);
        projectInfo.put("projectType","PROCESSOR");
        varMap.put("projectInfo",projectInfo);
        String routingKey = "runtime_"+BaseContextHandler.getUserID();
        rabbitTemplate.convertAndSend(runtimeOutputExchangeName, routingKey, varMap);
        result.put("success",true);
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }
    @Autowired
    IWorkflowService iWorkflowService;
    @Autowired
    IDataCoreService iDataCoreService;

    @RabbitListener(queues = "runtime_pro_response")
    public void listerQueueOne(Message message) throws Exception {
        JSONObject result = JSONObject.parseObject(new String(message.getBody(),"UTF-8"));
        logger.info("runtime_pro_response" +result.toJSONString());
        JSONObject taskInfo = result.getJSONObject("taskInfo");
        String taskId = taskInfo.getString("taskId");
        if(taskId==null){
            throw new Exception("no taskId");
        }
        JSONObject output = result.getJSONObject("output");
        ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(output);
        Object serviceResultId = responseEntity.getBody().get("serviceResultId");
        if(result.getBoolean("success")){
            output.put("serviceResultId",serviceResultId);
            output.put("success",true);
            output.put("status","200");
            iWorkflowService.complete(taskId,output);
        }
        else {
            output.put("serviceResultId",serviceResultId);
            output.put("success",false);
            output.put("status","200");
            iWorkflowService.complete(taskId,output);
            logger.info("返回success为false");
        }
    }
}
