package cn.edu.buaa.act.model.detection.controller;



import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.fastwash.data.Annotation;
import cn.edu.buaa.act.fastwash.data.Box;
import cn.edu.buaa.act.fastwash.data.Classification;
import cn.edu.buaa.act.fastwash.data.TrainingItem;
import cn.edu.buaa.act.model.detection.channel.MachineAnnotationChannel;
import cn.edu.buaa.act.model.detection.channel.ModelTrainingChannel;
import cn.edu.buaa.act.model.detection.channel.SlurmSelectChannel;
import cn.edu.buaa.act.model.detection.channel.SlurmTrainingChannel;
import cn.edu.buaa.act.model.detection.common.Constants;
import cn.edu.buaa.act.model.detection.entity.*;
import cn.edu.buaa.act.model.detection.feign.IDataCoreService;
import cn.edu.buaa.act.model.detection.feign.IWorkflowService;
import cn.edu.buaa.act.model.detection.repository.InferenceTaskRepository;
import cn.edu.buaa.act.model.detection.repository.TrainingTaskRepository;
import cn.edu.buaa.act.model.detection.service.InferenceService;
import cn.edu.buaa.act.model.detection.service.TrainingService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * AnnotationController
 *
 * @author wsj
 * @date 2019/9/8
 */
@RestController
@RequestMapping("/annotation")
@EnableBinding({SlurmTrainingChannel.class,SlurmSelectChannel.class})
public class AnnotationController {

    @Autowired
    private InferenceService inferenceService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private MachineAnnotationChannel machineAnnotationChannel;

    @Autowired
    private ModelTrainingChannel modelTrainingChannel;

    @Autowired
    private SlurmTrainingChannel slurmTrainingChannel;

    @Autowired
    private SlurmSelectChannel slurmSelectChannel;

    private static Logger logger = LoggerFactory.getLogger(AnnotationController.class);

    @RequestMapping(value = "/model/inference", method = RequestMethod.POST, produces = "application/json")
    public ObjectRestResponse inference(@RequestParam String projectName, @RequestParam String dataSetName, @RequestBody List<String> imageIdList) throws Exception {
        InferenceTask inferenceTask = inferenceService.createInferenceTask(projectName,dataSetName,imageIdList);
        if(inferenceTask==null){
            ObjectRestResponse objectRestResponse = new ObjectRestResponse<String>().success(false);
            objectRestResponse.setMessage("正在推断中");
            return objectRestResponse;
        }else {
            if(machineAnnotationChannel.output().send(MessageBuilder.withPayload(inferenceTask).build())){
                return new ObjectRestResponse<String>().success(true);
            }else{
                return new ObjectRestResponse<String>().success(false);
            }
        }
    }

    @RequestMapping(value = "/model/inferenceAll", method = RequestMethod.POST, produces = "application/json")
    public ObjectRestResponse inference(@RequestParam String projectName, @RequestParam String dataSetName) throws Exception {
        InferenceTask inferenceTask = inferenceService.createInferenceTask(projectName,dataSetName);
        if(inferenceTask==null){
            ObjectRestResponse objectRestResponse = new ObjectRestResponse<String>().success(false);
            objectRestResponse.setMessage("正在推断中");
            return objectRestResponse;
        }else {
            if(machineAnnotationChannel.output().send(MessageBuilder.withPayload(inferenceTask).build())){
                return new ObjectRestResponse<String>().success(true);
            }else{
                return new ObjectRestResponse<String>().success(false);
            }
        }
    }

    @RequestMapping(value = "/model/training", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse training(@RequestParam String projectName, @RequestParam String dataSetName) throws Exception {
        TrainingTask trainingTask = trainingService.createTrainingTask(projectName,dataSetName);
        if(trainingTask==null){
            ObjectRestResponse objectRestResponse = new ObjectRestResponse<String>().success(false);
            objectRestResponse.setMessage("创建失败");
            return objectRestResponse;
        }else {
            if(modelTrainingChannel.output().send(MessageBuilder.withPayload(trainingTask).build())){
                return new ObjectRestResponse<String>().success(true);
            }else{
                return new ObjectRestResponse<String>().success(false);
            }
        }
    }
    @Autowired
    IDataCoreService iDataCoreService;
    @RequestMapping(value = "/model/train", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> train(@RequestBody Map<String,Object> request) throws Exception {
        JSONObject json = (JSONObject) JSON.toJSON(request.get("taskInfo"));
        String processInstanceId = json.getString("processInstanceId");
        logger.info(JSONObject.toJSONString(request.get("parameter")));
        TrainingRequest trainingRequest = JSONObject.parseObject(JSONObject.toJSONString(request.get("parameter")),TrainingRequest.class);
        TrainingTask trainingTask = trainingService.createProcessTrainingTask(processInstanceId,trainingRequest);

        Map<String, Object> result = new HashMap<>();
        if(trainingTask==null){
            result.put("success",false);
            result.put("message","创建任务失败");
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }else {
            trainingRequest.setAsync(false);
            trainingRequest.setSimulate(true);
            trainingRequest.setTrainingId(trainingTask.getId());
            request.put("parameter",trainingRequest);
            if(slurmTrainingChannel.output().send(MessageBuilder.withPayload(request).build())){
                result.put("success",true);
                result.put("message","创建任务成功");
                return new ResponseEntity<Map>(result, HttpStatus.OK);
            }else{
                result.put("success",false);
                result.put("message","创建任务失败");
                ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
                Object serviceResultId = responseEntity.getBody().get("serviceResultId");
                result.put("serviceResultId",serviceResultId);
                return new ResponseEntity<Map>(result, HttpStatus.OK);
            }
        }
    }

    @RequestMapping(value = "/model/select/active", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> activeSelect(@RequestBody Map<String,Object> request) throws Exception {
        JSONObject json = (JSONObject) JSON.toJSON(request.get("taskInfo"));
        String processInstanceId = json.getString("processInstanceId");
        logger.info(JSONObject.toJSONString(request.get("parameter")));
        SelectRequest selectRequest = JSONObject.parseObject(JSONObject.toJSONString(request.get("parameter")),SelectRequest.class);
        InferenceTask inferenceTask = inferenceService.createProcessInferenceTask(processInstanceId,selectRequest);

        Map<String, Object> result = new HashMap<>();
        if(inferenceTask==null){
            result.put("success",false);
            result.put("message","创建任务失败");
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }else {
            selectRequest.setInferenceId(inferenceTask.getId());
            request.put("parameter",selectRequest);

            if(slurmSelectChannel.output().send(MessageBuilder.withPayload(request).build())){
                result.put("success",true);
                result.put("message","创建任务成功");
                return new ResponseEntity<Map>(result, HttpStatus.OK);
            }else{
                result.put("success",false);
                result.put("message","创建任务失败");
                ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
                Object serviceResultId = responseEntity.getBody().get("serviceResultId");
                result.put("serviceResultId",serviceResultId);
                return new ResponseEntity<Map>(result, HttpStatus.OK);
            }
        }
    }


    @Autowired
    IWorkflowService iWorkflowService;
    @Autowired
    private TrainingTaskRepository trainingTaskRepository;
    @StreamListener(Constants.SLURM_TRAINING_RESPONSE)
    public void receiverTrainingNotify(Message<JSONObject> message) throws Exception {
        JSONObject trainingResult = message.getPayload();
        logger.info("runtime_pro_response" +trainingResult.toJSONString());
        JSONObject taskInfo = trainingResult.getJSONObject("taskInfo");
        String taskId = taskInfo.getString("taskId");
        if(taskId==null){
            throw new Exception("no taskId");
        }
        JSONObject output = trainingResult.getJSONObject("output");

        TrainingTask originTask = trainingTaskRepository.findById(output.getString("trainingId")).orElse(null);
        if(originTask!=null){
            if(Constants.TRAINING_TASK_SUCCESS.equals(trainingResult.getString("status"))){
                originTask.setStatus(Constants.TRAINING_TASK_SUCCESS);
                originTask.setEndTime(new Date());
                trainingTaskRepository.save(originTask);
            }
        }
        ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(output);
        Object serviceResultId = responseEntity.getBody().get("serviceResultId");
        if(trainingResult.getBoolean("success")){
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


    @Autowired
    private InferenceTaskRepository inferenceTaskRepository;
    @StreamListener(Constants.SLURM_SELECT_RESPONSE)
    public void receiverSelectNotify(Message<JSONObject> message) throws Exception {
        JSONObject inferenceResult = message.getPayload();
        logger.info("runtime_pro_response" +inferenceResult.toJSONString());
        JSONObject taskInfo = inferenceResult.getJSONObject("taskInfo");
        String taskId = taskInfo.getString("taskId");
        if(taskId==null){
            throw new Exception("no taskId");
        }
        JSONObject output = inferenceResult.getJSONObject("output");
        if(output.getString("inferenceId")==null){
            throw new Exception("inference Id is null");
        }
        JSONArray annotationList = output.getJSONArray("annotationList");
        List<ImageToAnnotation> imageToAnnotations = new ArrayList<>();
        annotationList.forEach(js->{
            JSONObject imgAnn = JSONObject.parseObject(JSONObject.toJSONString(js));
            ImageToAnnotation imageToAnnotation = new ImageToAnnotation();
            imageToAnnotation.setImageId(imgAnn.getString("imageId"));
            imageToAnnotation.setDataSetName(output.getString("dataSetName"));
            List<Annotation> annotations = new ArrayList<>();

            JSONObject classToAnnResult = imgAnn.getJSONObject("annotation");
            classToAnnResult.forEach((key,value)->{
                JSONArray clsBoxArray = classToAnnResult.getJSONArray(key);
                if(clsBoxArray!=null){
                    for (int i = 0; i < clsBoxArray.size(); i++) {
                        JSONObject bbox = clsBoxArray.getJSONObject(i);
                        Annotation annotation = new Annotation();
                        Box box = new Box();
                        box.setX(bbox.getDouble("x"));
                        box.setY(bbox.getDouble("y"));
                        box.setW(bbox.getDouble("w"));
                        box.setH(bbox.getDouble("h"));
                        box.setScore(bbox.getDouble("score"));
                        annotation.setBox(box);
                        annotation.setModelId("baseModel");
                        annotation.setType("modelInference");
                        Classification classification = new Classification();
                        classification.setId(key);
                        annotation.setClassification(classification);
                        annotations.add(annotation);
                    }
                }
            });
            imageToAnnotation.setAnnotationList(annotations);
            imageToAnnotations.add(imageToAnnotation);
        });
        output.put("annotationList",imageToAnnotations);

        InferenceTask inferenceTask = inferenceTaskRepository.findById(output.getString("inferenceId")).orElse(null);
        if(inferenceTask!=null){
            if(Constants.INFERENCE_TASK_SUCCESS.equals(inferenceResult.getString("status"))){
                inferenceTask.setStatus(Constants.TRAINING_TASK_SUCCESS);
                inferenceTask.setEndTime(new Date());
                inferenceTaskRepository.save(inferenceTask);
            }
        }
        ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(output);
        Object serviceResultId = responseEntity.getBody().get("serviceResultId");
        if(inferenceResult.getBoolean("success")){
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
