//package cn.edu.buaa.act.figureeight.service;
//
//import cn.edu.buaa.act.figureeight.common.ReadJsonFile;
//import cn.edu.buaa.act.figureeight.constant.JobAttribute;
//import cn.edu.buaa.act.figureeight.entity.JudgmentItem;
//import cn.edu.buaa.act.figureeight.entity.ResultEntity;
//import cn.edu.buaa.act.figureeight.exception.NullAPIKeyException;
//import cn.edu.buaa.act.figureeight.feign.IDataCoreService;
//import cn.edu.buaa.act.figureeight.model.*;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.Trigger;
//import org.springframework.scheduling.TriggerContext;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.scheduling.support.CronTrigger;
//import org.springframework.stereotype.Service;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ScheduledFuture;
//
//
///**
// * ExecuteSimulationResultService
// * 预留工人库和工人能力
// * @author wsj
// * @date 2018/10/22
// */
//@Service
//@Slf4j
//public class ExecuteSimulationResultService {
//
//    private final Logger logger = LoggerFactory.getLogger(ExecuteSimulationResultService.class);
//
//    @Autowired
//    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
//
//    public static ConcurrentHashMap<String, List<Judgment>> taskJudgement = new ConcurrentHashMap<>();
//    private Map<String, ScheduledFuture> cronTaskScheduledFutureMap = new HashMap<>();
//
//    @Autowired
//    private IDataCoreService dataCoreService;
//
//    @Autowired
//    private JobService jobService;
//
//    @Autowired
//    private JudgementService judgementService;
//
//    private Map<String,List<ResultEntity>> metaToResult = new HashMap<>();
//
//
//    /**
//     * @param taskId 对应于workflow中的task
//     * @param jobId  对应于figureEight的task
//     * @param apiKey
//     * @param metaDataId
//     * @return
//     */
//    public Runnable createGetResultRunnable(String taskId, String jobId,String apiKey,String metaDataId) {
//        Runnable runnable = new Runnable() {
//            private void LoadAllResultEntity(String path,String taskId){
//                List<JSONObject> data = ReadJsonFile.ReadFile(path);
//                if(!metaToResult.containsKey(taskId)){
//                    metaToResult.put(taskId,new ArrayList<>());
//                }
//                for(JSONObject jsonObject:data)
//                {
//                    ResultEntity resultEntity = jsonObject.toJavaObject(ResultEntity.class);
//                    JSONObject results= resultEntity.getResults();
//                    JSONArray array = results.getJSONArray("judgments");
//                    resultEntity.setJudgmentList(JSON.parseArray(array.toString(),JudgmentItem.class));
//                    metaToResult.get(taskId).add(resultEntity);
//                }
//            }
//            Random random = new Random();
//            private Map humanTask(String metaDataId,List<String> unitIdList,int redundancy,String answerEntityId){
//                Map<String,Object> httpResponse = new HashMap<>();
//                Map<String,List<Judgment>> unitToJudgeMent= new HashMap<>();
//                metaToResult.get(metaDataId).forEach(resultEntity -> {
//                    String resultEntityId= resultEntity.getId();
//                    if(unitIdList.contains(resultEntityId)){
//                        if(!unitToJudgeMent.containsKey(resultEntityId)){
//                            unitToJudgeMent.put(resultEntityId,new ArrayList<>());
//                        }
//                        for(int i = 0; i<redundancy;i++){
//                            int size = resultEntity.getJudgeMentList().size();
//                            int pos = random.nextInt(size);
//                            unitToJudgeMent.get(resultEntityId).add(resultEntity.getJudgeMentList().get(pos));
//                            resultEntity.getJudgeMentList().remove(pos);
//                        }
//                    }
//                });
//
//                Map<String,WorkersDataStruct<String,String>> workersMap = new HashMap<String, WorkersDataStruct<String,String>>(); //多个question，这里用list
//                Map<String,Map<String,Integer>> workerCountry = new HashMap<>();
//                Map<String,Integer> workerCountryStat = new HashMap<>();
//                ResultRepresentation resultRepresentation = new ResultRepresentation();
//                WorkerRepresentation workerRepresentation= new WorkerRepresentation();
//                Map<String,Long> workerCopTime= new HashMap<>();
//
//                List<Label> labelList= new ArrayList<>();
//
//                unitToJudgeMent.forEach((unitId,judgeList)->{
//                    double totalInteval = 0;
//                    List<Double> workerTrust= new ArrayList<>();
//
//                    Map<String,Integer> agreement = new HashMap<>();
//
//                    for(Judgment judgeMent: judgeList)
//                    {
//                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//                        try {
//                            Date date1 =df.parse(judgeMent.getStarted_at());
//                            Date date2 =df.parse(judgeMent.getCreated_at());
//                            judgeMent.setInterval((date2.getTime()-date1.getTime())/1000);
//                            totalInteval +=judgeMent.getInterval();
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        String workerId = judgeMent.getWorker_id();
//                        List<String> question = new ArrayList<>();
//                        List<String> response = new ArrayList<>();
//                        judgeMent.getData().forEach((key, value) -> {
//                            question.add(judgeMent.getUnit_id()+"-"+key);
//                            response.add(value);
//                        });
//                        Label label =new Label();
//                        label.setWorker(workerId);
//                        label.setAnswer(response.get(0));///默认为1个，其实并不是
//                        label.setItem(unitId);
//                        labelList.add(label);
//
//                        if(!agreement.containsKey(response.get(0))){
//                            agreement.put(response.get(0),1);
//                        }
//                        else {
//                            int val = agreement.get(response.get(0));
//                            agreement.put(response.get(0),++val);
//                        }
//
//                        if(workersMap.containsKey(workerId)){
//                            WorkersDataStruct<String,String> currentWorkerStruct = workersMap.get(workerId);
//                            for(int q=0;q<question.size();q++){
//                                currentWorkerStruct.insertWorkerResponse(question.get(q), response.get(0));
//                            }
//                            if(currentWorkerStruct.getTrust()!=judgeMent.getWorker_trust())
//                            {
//                                // System.out.println("trust not equal");
//                                currentWorkerStruct.setTrust(judgeMent.getWorker_trust());
//                            }
//                            workersMap.put(workerId, currentWorkerStruct);
//                            workerCopTime.put(workerId,judgeMent.getInterval()+workerCopTime.get(workerId));
//                        }
//                        else
//                        {
//                            WorkersDataStruct<String,String> newWorker = new WorkersDataStruct<String,String>();
//                            for(int q=0;q<question.size();q++){
//                                newWorker.insertWorkerResponse(question.get(q), response.get(0));
//                            }
//                            newWorker.setTrust(judgeMent.getWorker_trust());
//
//                            workersMap.put(workerId, newWorker);
//                            workerCopTime.put(workerId,judgeMent.getInterval());
//
//                            if(workerCountryStat.containsKey(judgeMent.getCountry()))
//                            {
//                                int number = workerCountryStat.get(judgeMent.getCountry());
//                                workerCountryStat.put(judgeMent.getCountry(),++number);
//                            }
//                            else
//                            {
//                                workerCountryStat.put(judgeMent.getCountry(),1);
//                            }
//                            if(workerCountry.containsKey(judgeMent.getCountry()))
//                            {
//                                if (workerCountry.get(judgeMent.getCountry()).containsKey(judgeMent.getCity()))
//                                {
//                                    int number = workerCountry.get(judgeMent.getCountry()).get(judgeMent.getCity());
//                                    workerCountry.get(judgeMent.getCountry()).put(judgeMent.getCity(),++number);
//                                }
//                                else
//                                {
//                                    workerCountry.get(judgeMent.getCountry()).put(judgeMent.getCity(),1);
//                                }
//                            }
//                            else
//                            {
//                                Map<String,Integer> city =new HashMap<>();
//                                city.put(judgeMent.getCity(),1);
//                                workerCountry.put(judgeMent.getCountry(),city);
//                            }
//
//                        }
//                        workerTrust.add(Double.parseDouble(String.format("%.2f",judgeMent.getWorker_trust())));
//
//                    }
//                    resultRepresentation.getWorkerTrust().add(workerTrust);
//                    resultRepresentation.getInterval().add(totalInteval/judgeList.size());
//
//                    resultRepresentation.getAgreement().add((double)Collections.max(agreement.values())/judgeList.size());
//
//                    resultRepresentation.getJudgementCount().add(judgeList.size());
//                    resultRepresentation.getUnitId().add(unitId);
//
//                });
//
//                resultRepresentation.setUnitTotal(unitToJudgeMent.size());
//                int judgeTotal=0;
//                for(int i=0;i<resultRepresentation.getJudgementCount().size();i++)
//                {
//                    judgeTotal+=resultRepresentation.getJudgementCount().get(i);
//                }
//                resultRepresentation.setJudgementTotal(judgeTotal);
//
//
//                workerRepresentation.setWorkerNumber(workersMap.size());
//                workerRepresentation.setWorkerId(new ArrayList<String>(workersMap.keySet()));
//                List<Integer> completeTask = new ArrayList<>();
//                List<Double> workerTrust = new ArrayList<>();
//                List<Long> avgCompTime = new ArrayList<>();
//
//                workersMap.forEach((key, value) -> {
//                    completeTask.add(value.getNumResponses());
//                    workerTrust.add(value.getTrust());
//                    avgCompTime.add(workerCopTime.get(key)/value.getNumResponses());
//                });
//                workerRepresentation.setAvgCompleteTime(avgCompTime);
//                workerRepresentation.setCompleteTaskNumer(completeTask);
//                workerRepresentation.setWorkerTrust(workerTrust);
//                workerRepresentation.setCountry(workerCountry);
//                workerRepresentation.setCountryStat(workerCountryStat);
//
//                Map<String,Object> serviceResult = new HashMap<>();
//                serviceResult.put("success",true);
//                serviceResult.put("worker", workerRepresentation);
//                serviceResult.put("result", resultRepresentation);
//                serviceResult.put("labelList", labelList);
//                serviceResult.put("ServiceName", "FigureEightTask");
//
//                Map<String,Object> labelReq = new HashMap<>();
//                labelReq.put("labelList",labelList);
//                labelReq.put("answerEntityId",answerEntityId);
//                dataCoreService.insertLabelList(labelReq);
//                // httpResponse = dataCoreService.insertServiceResult(serviceResult).getBody();
//                // httpResponse.put("labelList", labelList);
//                return httpResponse;
//            }
//
//            @Override
//            public void run() {
//                logger.info("请求结果"+jobId+apiKey);
//                try {
//                    Job job = jobService.getJob(jobId,apiKey);
//                    if(job.getAttribute(JobAttribute.COMPLETED.toString()).equals("true")||!job.getAttribute(JobAttribute.COMPLETED_AT.toString()).equals("null")){
//
//                        //这里暂时用不到
////                        judgementService.regenerateReport(jobId,apiKey);
////                        List<Judgment> judgmentList = judgementService.getReport(jobId,apiKey);
////                        taskJudgement.put(jobId,judgmentList);
//
//                        Map req = new HashMap();
//                        req.put("answerEntityName",taskId);
//                        req.put("metaDataId",metaDataId);
//
//                        String key = UUID.randomUUID().toString();
//                        req.put("key",job.getAttribute(JobAttribute.COMPLETED_AT));
//                        req = (Map) dataCoreService.insertAnswerEntity(req).getBody();
//                        if((Boolean) req.get("success")){
//                            String answerEntityId = req.get("answerEntityId").toString();
//                            String path ="d:/job_839968.json";
//                            LoadAllResultEntity(path,taskId);
//                        }
//                        else {
//                            logger.info("插入回答实体类失败");
//                        }
//                        cronTaskScheduledFutureMap.get(jobId).cancel(true);
//                    }
//                    else {
//                        List<Judgment> judgments = jobService.getJudgments(jobId,apiKey);
//                        taskJudgement.put(jobId,judgments);
//
//                    }
//                } catch (NullAPIKeyException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        return runnable;
//    }
//
//    public ScheduledFuture<?> threadScheduler(String processInstanceId, Runnable runnable, String cron) {
//        /*动态创建定时任务*/
//        ScheduledFuture future = threadPoolTaskScheduler.schedule(runnable, new Trigger() {
//            @Override
//            public Date nextExecutionTime(TriggerContext triggerContext) {
//                return new CronTrigger(cron).nextExecutionTime(triggerContext);
//            }
//        });
//        cronTaskScheduledFutureMap.put(processInstanceId, future);
//        return future;
//    }
//}
