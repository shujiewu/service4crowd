package cn.edu.buaa.act.fastwash.controller;

import cn.edu.buaa.act.fastwash.data.TaskItemEntity;
import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import cn.edu.buaa.act.fastwash.estimate.EstimateService;
import cn.edu.buaa.act.fastwash.estimate.ReadJsonFile;
import cn.edu.buaa.act.fastwash.feign.IDataCoreService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/result")
public class ResultController {
    @Autowired
    private IDataCoreService iDataCoreService;

    @Autowired
    private EstimateService estimateService;

    private static Logger logger = LoggerFactory.getLogger(ResultController.class);
    @RequestMapping(value = "/estimate", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> estimateResult(@RequestBody Map<String,Object> request) throws Exception {

        JSONObject json = (JSONObject) JSON.toJSON(request.get("taskInfo"));
        String taskId = json.getString("taskId");
        logger.info(JSONObject.toJSONString(request.get("parameter")));
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(request.get("parameter")));
        List<TaskItemEntity> taskItemEntityList = jsonObject.getJSONArray("taskItemList").toJavaList(TaskItemEntity.class);

        Set<String> workerIds = new HashSet<>();
        taskItemEntityList.forEach(taskItemEntity -> {
            //taskItemEntity.getWorkerList().forEach(w);
            workerIds.addAll(taskItemEntity.getWorkerList());
        });

        JSONObject workerFile = ReadJsonFile.ReadFile("D:/fastwashdata/weight_file_gt_zc.json");
        JSONObject transFile = ReadJsonFile.ReadFile("D:/fastwashdata/parameter.txt");

        JSONObject workerAbility = new JSONObject();
        JSONObject transParameter = new JSONObject();
        workerIds.forEach(workerId->{
            workerAbility.put(workerId,workerFile.get(workerId));
            transParameter.put(workerId,transFile.get(workerId));
        });

        Map<String, Object> result = new HashMap<>();
        JSONArray esResult = estimateService.estimateResult(taskItemEntityList,taskId);
        if(esResult!=null){
            result.put("estimateResult",esResult);
            result.put("workerAbility",workerAbility);
            result.put("transParameter",transParameter);
            result.put("success",true);
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }else{
            result.put("success",false);
            result.put("message","结果评估出错");
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
    }
}
