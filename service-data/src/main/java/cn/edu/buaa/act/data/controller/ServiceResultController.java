package cn.edu.buaa.act.data.controller;


import cn.edu.buaa.act.auth.client.annotation.IgnoreUserToken;
import cn.edu.buaa.act.data.entity.AggregateResultEntity;
import cn.edu.buaa.act.data.entity.ServiceResultEntity;
import cn.edu.buaa.act.data.model.AggregateResultRepresentation;
import cn.edu.buaa.act.data.service.IAggregateResultService;
import cn.edu.buaa.act.data.service.IAnswerService;
import cn.edu.buaa.act.data.service.IServiceResult;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/data")
public class ServiceResultController {
    @Autowired
    IServiceResult resultService;


    @Autowired
    IServiceResult serviceResult;

    @Autowired
    IAnswerService answerService;

    @Autowired
    IAggregateResultService mergeResultService;


    @RequestMapping(value = "/getMergeResult/{mergeResultId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> getMergeResult(@PathVariable("mergeResultId")String mergeResultId) {
        AggregateResultEntity mergeResultEntity = mergeResultService.queryAggregateResult(mergeResultId);
        Map map=new HashMap<>();
        map.put("success",true);
        map.put("mergeResultEntity",mergeResultEntity);

        List<AggregateResultRepresentation> mergeResultRepresentationList = new ArrayList<>();
        Map<String, String> sortResult = new TreeMap<String, String>(mergeResultEntity.getResultPredict());
        sortResult.forEach((key,value)->{
            AggregateResultRepresentation mergeResultRepresentation = new AggregateResultRepresentation();
            mergeResultRepresentation.setId(key);
            mergeResultRepresentation.setPredictResult(value);

            mergeResultRepresentation.setTruth(mergeResultEntity.getResultTruth().getOrDefault(key,"?"));
            mergeResultRepresentation.setCorrect(mergeResultEntity.getResultTruth().getOrDefault(key,"?").equals(value));
            mergeResultRepresentationList.add(mergeResultRepresentation);
        });
        map.put("mergeResultList",mergeResultRepresentationList);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

//    @RequestMapping(value = "/getWorker", method = RequestMethod.GET, produces = "application/json")
//    public ResponseEntity<Object> getWorker() {
//        Map map=new HashMap<>();
//        map.put("success",true);
//        map.put("worker", resultService.getWorkerStatistical());
//        map.put("result", resultService.getResultRepresentation());
//        return new ResponseEntity<Object>(map, HttpStatus.OK);
//    }

    @RequestMapping(value = "/getCustomResult/{serviceName}/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> getCustomResult(@PathVariable("serviceName")String serviceName, @PathVariable("id")String id) {
        Map map=new HashMap<>();
        // map.put("success",true);
//        if(serviceName.equals("LoadAnswerData")){
//            map.put("result",answerService.queryById(id));
//        }
//        if(serviceName.equals("AnswerStatistics")){
//            map.put("result",answerService.queryById(id).getAnswerStatRepresentation());
//        }
        map = serviceResult.findById(id).getResult();
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }


    /**
     * @param id
     * @return
     * 暴露为原子服务,便于从错误恢复
     */
    @RequestMapping(value = "/loadServiceResult/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> loadServiceResult(@PathVariable("id")String id) {
        Map map=new HashMap<>();
        map = serviceResult.findById(id).getResult();
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }


    @IgnoreUserToken
    @RequestMapping(value = "/serviceResult/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> insertServiceResult(@RequestBody Map<String, Object> request){
        String serviceName= (String) request.get("ServiceName");
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult(serviceName,request);
        Map<String,Object> result = new HashMap<>();
        result.put("serviceResultId",serviceResultEntity.getId());
        result.put("success",true);
        return new ResponseEntity<Map>(result,HttpStatus.OK);
    }


    @IgnoreUserToken
    @RequestMapping(value = "/serviceResult/{serviceResultId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map> getServiceResult(@PathVariable(value = "serviceResultId") String serviceResultId){
        Map map=new HashMap<>();
        map = serviceResult.findById(serviceResultId).getResult();
        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }
}
