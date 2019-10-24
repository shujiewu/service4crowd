package cn.edu.buaa.act.model.detection.controller;



import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.fastwash.data.TrainingItem;
import cn.edu.buaa.act.model.detection.channel.MachineAnnotationChannel;
import cn.edu.buaa.act.model.detection.channel.ModelTrainingChannel;
import cn.edu.buaa.act.model.detection.entity.InferenceTask;
import cn.edu.buaa.act.model.detection.entity.TrainingTask;
import cn.edu.buaa.act.model.detection.service.InferenceService;
import cn.edu.buaa.act.model.detection.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AnnotationController
 *
 * @author wsj
 * @date 2019/9/8
 */
@RestController
@RequestMapping("/annotation")
public class AnnotationController {

    @Autowired
    private InferenceService inferenceService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private MachineAnnotationChannel machineAnnotationChannel;

    @Autowired
    private ModelTrainingChannel modelTrainingChannel;

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
}
