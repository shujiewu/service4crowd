//package cn.edu.buaa.act.data.controller;
//
//
//
//import cn.edu.buaa.act.data.service.IAggregateResultService;
//import cn.edu.buaa.act.data.service.IAnswerService;
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//
//@RestController
//public class AggregateResultController {
//
//
//    @Autowired
//    IAnswerService answerService;
//
//    @Autowired
//    IAggregateResultService mergeResultService;
//
//    @Autowired
//    IResultService resultService;
//
//    @RequestMapping(value = "/getReport", method = RequestMethod.GET, produces = "application/json")
//    public TableResultResponse<ResultEntity> getReport() {
////        Map map=new HashMap<>();
////        map.put("success",true);
////        List<JSONObject> data = ReadJsonFile.ReadFile("d:/job_839968.json");
////        System.out.println(data.size());
////        for(JSONObject jsonObject:data)
////        {
////            ResultEntity resultEntity = jsonObject.toJavaObject(ResultEntity.class);
////            JSONObject results= resultEntity.getResults();
////            JSONArray array = results.getJSONArray("judgments");
////            resultEntity.setJudgeMentList(JSON.parseArray(array.toString(),JudgeMent.class));
////            System.out.println(resultEntity.getJudgeMentList().get(0).getData().keySet());
////        }
//        // resultService.parseWorkerLabels();
//        return null;
//    }
//
//
//    @RequestMapping(value = "/getMergeResult/{mergeResultId}", method = RequestMethod.GET, produces = "application/json")
//    public ResponseEntity<Object> getMergeResult(@PathVariable("mergeResultId")String mergeResultId) {
//        MergeResultEntity mergeResultEntity = mergeResultService.queryMergeResult(mergeResultId);
//        Map map=new HashMap<>();
//        map.put("success",true);
//        map.put("mergeResultEntity",mergeResultEntity);
//
//        List<MergeResultRepresentation> mergeResultRepresentationList = new ArrayList<>();
//        Map<String, String> sortResult = new TreeMap<String, String>(mergeResultEntity.getResultPredict());
//        sortResult.forEach((key,value)->{
//            MergeResultRepresentation mergeResultRepresentation = new MergeResultRepresentation();
//            mergeResultRepresentation.setId(key);
//            mergeResultRepresentation.setPredictResult(value);
//
//            mergeResultRepresentation.setTruth(mergeResultEntity.getResultTruth().getOrDefault(key,"?"));
//            mergeResultRepresentation.setCorrect(mergeResultEntity.getResultTruth().getOrDefault(key,"?").equals(value));
//            mergeResultRepresentationList.add(mergeResultRepresentation);
//        });
//        map.put("mergeResultList",mergeResultRepresentationList);
//        return new ResponseEntity<Object>(map, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/getWorker", method = RequestMethod.GET, produces = "application/json")
//    public ResponseEntity<Object> getWorker() {
//        Map map=new HashMap<>();
//        map.put("success",true);
//        map.put("worker", resultService.getWorkerStatistical());
//        map.put("result", resultService.getResultRepresentation());
//        return new ResponseEntity<Object>(map, HttpStatus.OK);
//    }
//
//    @Autowired
//    ServiceResultImpl serviceResultl;
//
//    @RequestMapping(value = "/getCustomResult/{serviceName}/{id}", method = RequestMethod.GET, produces = "application/json")
//    public ResponseEntity<Object> getCustomResult(@PathVariable("serviceName")String serviceName, @PathVariable("id")String id) {
//        Map map=new HashMap<>();
//        // map.put("success",true);
////        if(serviceName.equals("LoadAnswerData")){
////            map.put("result",answerService.queryById(id));
////        }
////        if(serviceName.equals("AnswerStatistics")){
////            map.put("result",answerService.queryById(id).getAnswerStatRepresentation());
////        }
//        map = serviceResultl.findById(id).getResult();
//        return new ResponseEntity<Object>(map, HttpStatus.OK);
//    }
//
//    @Autowired
//    ServiceResultImpl serviceResult;
//    @IgnoreUserToken
//    @RequestMapping(value = "/insertServiceResult", method = RequestMethod.POST, produces = "application/json")
//    public ResponseEntity<Map> insertServiceResult(@RequestBody Map<String, Object> request){
//        String serviceName= (String) request.get("ServiceName");
//        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult(serviceName,request);
//        Map<String,Object> result = new HashMap<>();
//        result.put("serviceResultId",serviceResultEntity.getId());
//        return new ResponseEntity<Map>(result, HttpStatus.OK);
//    }
//
//
//
//
//    @IgnoreUserToken
//    @RequestMapping(value = "/aggregateCompare", method = RequestMethod.POST, produces = "application/json")
//    public ResponseEntity<Map> aggregateCompare(@RequestBody Map<String, Object> request) {
//        List<MergeResultEntity> aggregateResult = new ArrayList<>();
//        request.forEach((key,value)->{
//            Map map=new HashMap<>();
//            System.out.println("AR"+value);
//            map = serviceResultl.findById((String)value).getResult();
//            // System.out.println(map.get("AggregateStatistics").toString());
//            aggregateResult.add(JSONObject.parseObject(JSONObject.toJSONString(map.get("AggregateStatistics")),MergeResultEntity.class));
//        });
////
////        request.forEach((key,value)->{
////            Map map=new HashMap<>();
////            map = serviceResultl.findById((String)value).getResult();
////            System.out.println(map.get("AggregateStatistics").toString());
////            aggregateResult.add(JSONObject.parseObject(map.get("AggregateStatistics").toString().replaceAll("=",":")));
////        });
////
////        Map result = new HashMap();
////        AggregateCompare aggregateCompare = new AggregateCompare();
////
////        if(aggregateResult.size()>0){
////            Map<String,JSONObject> allPredict= new HashMap<>();
////
////            JSONObject groundTruth = aggregateResult.get(0).getJSONObject("resultTruth");
////            groundTruth.forEach((key,value)->{
////                JSONObject oneLine = new JSONObject();
////                oneLine.put("gold",value);
////                oneLine.put("id",key);
////
////                allPredict.put(key,oneLine);
////            });
////
////            aggregateResult.forEach(jsonObject -> {
////                JSONObject resultIndex= jsonObject.getJSONObject("resultIndex");
////
////                double duration = jsonObject.getDouble("duration");
////                String method =jsonObject.getString("method");
////                aggregateCompare.getAlgorithmName().add(method);
////
////                resultIndex.put("algorithm",method);
////                resultIndex.put("duration",duration);
////                aggregateCompare.getAggregateStat().add(resultIndex);
////
////                JSONObject predict = jsonObject.getJSONObject("resultPredict");
////                predict.forEach((key,value)->{
////                    allPredict.get(key).put(method,value);
////                });
////            });
////            aggregateCompare.setResultPredict(allPredict);
////        }
//        Map result = new HashMap();
//        AggregateCompare aggregateCompare = new AggregateCompare();
//        if (aggregateResult.size() > 0) {
//            Map<String, JSONObject> allPredict = new HashMap<>();
//
//            aggregateResult.get(0).getResultTruth().forEach((key, value) -> {
//                JSONObject oneLine = new JSONObject();
//                oneLine.put("gold", value);
//                oneLine.put("id", key);
//
//                allPredict.put(key, oneLine);
//            });
//
//            aggregateResult.forEach(jsonObject -> {
//                Map<String,Object> resultIndex = jsonObject.getResultIndex();
//
//                double duration = jsonObject.getDuration();
//                String method = jsonObject.getMethod();
//                aggregateCompare.getAlgorithmName().add(method);
//
//                resultIndex.put("algorithm", method);
//                resultIndex.put("duration", duration);
//                aggregateCompare.getAggregateStat().add(resultIndex);
//
//                Map<String,String> predict = jsonObject.getResultPredict();
//                predict.forEach((key, value) -> {
//                    allPredict.get(key).put(method, value);
//                });
//            });
//            aggregateCompare.setResultPredict(allPredict);
//            aggregateCompare.setDataType("decisionmaking");
//        }
//        if(aggregateResult.size()>0){
//            result.put("success",true);
//            result.put("AggregateCompare",aggregateCompare);
//        }
//        else {
//            result.put("success",false);
//        }
//        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("AggregateCompare",result);
//        result.put("serviceResultId",serviceResultEntity.getId());
//        return new ResponseEntity<Map>(result, HttpStatus.OK);
//    }
//}
