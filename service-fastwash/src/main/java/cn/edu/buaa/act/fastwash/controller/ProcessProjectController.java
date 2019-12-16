package cn.edu.buaa.act.fastwash.controller;

import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.fastwash.data.DataItemEntity;
import cn.edu.buaa.act.fastwash.data.TrainingItem;
import cn.edu.buaa.act.fastwash.delay.DelayService;
import cn.edu.buaa.act.fastwash.delay.Message;
import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import cn.edu.buaa.act.fastwash.entity.RepublishRequest;
import cn.edu.buaa.act.fastwash.entity.TrainingSetAddRequest;
import cn.edu.buaa.act.fastwash.feign.IDataCoreService;
import cn.edu.buaa.act.fastwash.service.AnnotationService;
import cn.edu.buaa.act.fastwash.service.api.IProjectService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/project")
public class ProcessProjectController {
    @Autowired
    IProjectService projectService;

    @Autowired
    private IDataCoreService iDataCoreService;
    private static Logger logger = LoggerFactory.getLogger(ProcessProjectController.class);
    @RequestMapping(value = "/createAndPublish", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> createProject(@RequestBody Map<String,Object> request) throws Exception {

        JSONObject json = (JSONObject) JSON.toJSON(request.get("taskInfo"));
        String taskId = json.getString("taskId");
        logger.info(JSONObject.toJSONString(request.get("parameter")));
        ProjectEntity projectEntity = JSONObject.parseObject(JSONObject.toJSONString(request.get("parameter")),ProjectEntity.class);

        Map<String, Object> result = new HashMap<>();
        projectEntity = projectService.insertProcessProject(taskId,projectEntity);
        if(projectEntity.getId()!=null){
            result.put("projectInfo",projectEntity);
            result.put("projectName",projectEntity.getName());
            result.put("success",true);
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }else{
            result.put("success",false);
            result.put("message","创建项目失败");
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
    }
    @Autowired
    AnnotationService annotationService;

    @RequestMapping(value = "/complete/{projectName}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map> completeProject(@PathVariable String projectName) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("success",true);
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/republish/{projectName}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> republishProject(@PathVariable String projectName, @RequestBody Map<String,Object> request) throws Exception {
        /// 延时任务
        JSONObject json = (JSONObject) JSON.toJSON(request.get("taskInfo"));
        String taskId = json.getString("taskId");
        logger.info(JSONObject.toJSONString(request.get("parameter")));
        RepublishRequest republishRequest = JSONArray.parseObject(JSONObject.toJSONString(request.get("parameter")),RepublishRequest.class);
        Map<String, Object> result = new HashMap<>();
        try {
            annotationService.addToRuntimeProject(projectName,republishRequest.getCrowdTaskIdList());
            result.put("success",true);
            result.put("message","republish success");
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }catch (Exception e){
            result.put("success",false);
            result.put("message",e.toString());
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/trainingSet/add/{projectName}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> addToTrainingSet(@PathVariable String projectName, @RequestBody Map<String,Object> request) throws Exception {
        /// 延时任务
        JSONObject json = (JSONObject) JSON.toJSON(request.get("taskInfo"));
        String taskId = json.getString("taskId");
        logger.info(JSONObject.toJSONString(request.get("parameter")));
        TrainingSetAddRequest trainingSetAddRequest = JSONArray.parseObject(JSONObject.toJSONString(request.get("parameter")),TrainingSetAddRequest.class);
        Map<String, Object> result = new HashMap<>();
        try {
            long remainTaskNum = annotationService.addToTrainingSet(projectName,trainingSetAddRequest.getCrowdTaskIdList());
            result.put("success",true);
            result.put("message","addToTrainingSet success");
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }catch (Exception e){
            result.put("success",false);
            result.put("message",e.toString());
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "/trainingSet/get/{projectName}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map> getTrainingSet(@PathVariable String projectName) throws Exception {
        Set<String> imageIdList = new HashSet<>();
        List<TrainingItem> trainingItemList = new ArrayList<>();
        MongoCollection<Document> groundTruthCollection = mongoTemplate.getCollection(projectName+"_result");

        MongoCursor<Document> cursor = groundTruthCollection.find().iterator();

        Map<String,TrainingItem> trainingItemMap = new HashMap<>();
        try {
            while (cursor.hasNext()) {
                Document origin =  cursor.next();
                String str = origin.toJson();
                TrainingItem trainingItem = JSONObject.parseObject(str, TrainingItem.class);
                imageIdList.add(trainingItem.getImageId());
                if(!trainingItemMap.containsKey(trainingItem.getImageId())){
                    TrainingItem item = new TrainingItem();
                    item.setDataSetName(trainingItem.getDataSetName());
                    item.setImageId(trainingItem.getImageId());
                    item.setTagList(new ArrayList<>());
                    trainingItemMap.put(trainingItem.getImageId(),item);
                }
                TrainingItem originItem = trainingItemMap.get(trainingItem.getImageId());
                originItem.getTagList().addAll(trainingItem.getTagList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success",true);
            result.put("message","getTrainingSet success");
            result.put("imageIdList",imageIdList);
            result.put("trainingItemList",trainingItemMap.values());
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }catch (Exception e){
            result.put("success",false);
            result.put("message",e.toString());
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/tasks/remain/{projectName}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map> getRemainingTask(@PathVariable String projectName) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            long remainTaskNum = annotationService.getRemainingTask(projectName);
            result.put("success",true);
            result.put("remainTaskNum",remainTaskNum);
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }catch (Exception e){
            result.put("success",false);
            result.put("message",e.toString());
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
    }

    @Autowired
    private DelayService delayService;

    @RequestMapping(value = "/recentResult/{projectName}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> recentTask(@PathVariable String projectName, @RequestBody Map<String,Object> request) throws Exception {
        /// 延时任务
        JSONObject json = (JSONObject) JSON.toJSON(request.get("taskInfo"));
        String taskId = json.getString("taskId");
        logger.info(JSONObject.toJSONString(request.get("parameter")));

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(request.get("parameter")));
        int delaySeconds = jsonObject.getInteger("delaySeconds");
        Message m1 = new Message(taskId, projectName, request,delaySeconds*1000);
        delayService.addToDelayQueue(m1);

        Map<String, Object> result = new HashMap<>();
        result.put("success",true);
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }
}
