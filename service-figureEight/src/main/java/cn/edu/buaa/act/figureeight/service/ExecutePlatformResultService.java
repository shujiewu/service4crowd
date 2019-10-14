package cn.edu.buaa.act.figureeight.service;

import cn.edu.buaa.act.figureeight.common.ReadJsonFile;
import cn.edu.buaa.act.figureeight.constant.JobAttribute;
import cn.edu.buaa.act.figureeight.entity.JudgmentItem;
import cn.edu.buaa.act.figureeight.entity.ResultEntity;
import cn.edu.buaa.act.figureeight.exception.NullAPIKeyException;
import cn.edu.buaa.act.figureeight.feign.IDataCoreService;
import cn.edu.buaa.act.figureeight.feign.IWorkflowService;
import cn.edu.buaa.act.figureeight.model.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


/**
 * ExecuteSimulationResultService
 * @author wsj
 * @date 2018/10/22
 */
@Service
@Slf4j
public class ExecutePlatformResultService {

    private final Logger logger = LoggerFactory.getLogger(ExecutePlatformResultService.class);

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;



    private Map<String, ScheduledFuture> cronTaskScheduledFutureMap = new HashMap<>();

    @Autowired
    private IDataCoreService dataCoreService;

    @Autowired
    private IWorkflowService workflowService;

    @Autowired
    private JobService jobService;

    @Autowired
    private JudgementService judgementService;

    //任务to所有回答的行
    private ConcurrentHashMap<String,List<ResultEntity>> jobToResult = new ConcurrentHashMap<>();

    //答案的id和每一行id的映射,预留
    private ConcurrentHashMap<String,Map<String,String>> jobToUnitIdMap =  new ConcurrentHashMap<>();


    //任务tolist unit
    public static ConcurrentHashMap<String, Map<String,List<JudgmentItem>>> jobJudgement = new ConcurrentHashMap<>();

    // 载入本次任务的所有结果
    // 按理说应该载入多次，但是这里按照processInstanceId载入,而不是taskId
    public void loadAllResultEntity(String path,String processInstanceId){
        List<JSONObject> data = ReadJsonFile.ReadFile(path);
        if(!jobToResult.containsKey(processInstanceId)){
            jobToResult.put(processInstanceId,new ArrayList<>());
            for(JSONObject jsonObject:data) {
                ResultEntity resultEntity = jsonObject.toJavaObject(ResultEntity.class);
                JSONObject unitData = resultEntity.getData();
                // jobToUnitIdMap.get(processInstanceId).put(resultEntity.getId(),unitData.getString("seq"));
                JSONObject results= resultEntity.getResults();
                JSONArray array = results.getJSONArray("judgments");
                resultEntity.setJudgmentList(JSON.parseArray(array.toString(),JudgmentItem.class));
                jobToResult.get(processInstanceId).add(resultEntity);
            }
        }
        if(!jobToUnitIdMap.containsKey(processInstanceId)){
            jobToUnitIdMap.put(processInstanceId,new HashMap<>());
        }
    }




    /**
     * @param taskId 对应于workflow中的task
     * @param apiKey
     * @param metaDataId
     * @return
     * 假设冗余次数和迭代查询的次数是一样的
     * 这里的id默认就是每一个数据集的id
     */
    public Runnable createGetResultRunnable(String taskId, String jobId,String apiKey, String metaDataId, List<String> unitIdList,String processInstanceId) {
        Runnable runnable = new Runnable() {
            int iteration = 0;
            Random random = new Random(Thread.currentThread().getId());

            private Map generateResult(String jobId, String answerEntityId){
                Map<String,Object> httpResponse = new HashMap<>();
                Map<String,WorkersDataStruct<String,String>> workersMap = new HashMap<String, WorkersDataStruct<String,String>>(); //多个question，这里用list
                Map<String,Map<String,Integer>> workerCountry = new HashMap<>();
                Map<String,Integer> workerCountryStat = new HashMap<>();
                ResultRepresentation resultRepresentation = new ResultRepresentation();
                WorkerRepresentation workerRepresentation= new WorkerRepresentation();
                Map<String,Long> workerCopTime= new HashMap<>();
                List<Label> labelList= new ArrayList<>();
                ExecutePlatformResultService.jobJudgement.get(jobId).forEach((unitId, judgeList)->{
                    double totalInteval = 0;
                    List<Double> workerTrust= new ArrayList<>();

                    Map<String,Integer> agreement = new HashMap<>();

                    for(JudgmentItem judgeMent: judgeList)
                    {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                        try {
                            Date date1 =df.parse(judgeMent.getStarted_at());
                            Date date2 =df.parse(judgeMent.getCreated_at());
                            judgeMent.setInterval((date2.getTime()-date1.getTime())/1000);
                            totalInteval +=judgeMent.getInterval();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String workerId = judgeMent.getWorker_id();
                        List<String> question = new ArrayList<>();
                        List<String> response = new ArrayList<>();

                        Map<String,String> responseMap = new HashMap<>();
                        //特殊处理多个问题
                        judgeMent.getData().forEach((key, value) -> {
                            responseMap.put(key,value);
                            question.add(judgeMent.getUnit_id()+"-"+key);
                            if(metaDataId.equals("5bd95a24c5b1fe3338483cf5")){
                                if(key.equals("taxonomyresponse_id")){
                                    response.add(0,value);
                                }
                                else {
                                    response.add(value);
                                }
                            }else {
                                response.add(value);
                            }
                        });

                        Label label =new Label();
                        label.setWorker(workerId);
                        label.setAnswer(response.get(0));///默认为1个，其实并不是
                        label.setItem(unitId);
                        label.setResponse(responseMap);

                        labelList.add(label);
                        if(!agreement.containsKey(response.get(0))){
                            agreement.put(response.get(0),1);
                        }
                        else {
                            int val = agreement.get(response.get(0));
                            agreement.put(response.get(0),++val);
                        }

                        if(workersMap.containsKey(workerId)){
                            WorkersDataStruct<String,String> currentWorkerStruct = workersMap.get(workerId);
                            for(int q=0;q<question.size();q++){
                                currentWorkerStruct.insertWorkerResponse(question.get(q), response.get(0));
                            }
                            if(currentWorkerStruct.getTrust()!=judgeMent.getWorker_trust())
                            {
                                // System.out.println("trust not equal");
                                currentWorkerStruct.setTrust(judgeMent.getWorker_trust());
                            }
                            workersMap.put(workerId, currentWorkerStruct);
                            workerCopTime.put(workerId,judgeMent.getInterval()+workerCopTime.get(workerId));
                        }
                        else
                        {
                            WorkersDataStruct<String,String> newWorker = new WorkersDataStruct<String,String>();
                            for(int q=0;q<question.size();q++){
                                newWorker.insertWorkerResponse(question.get(q), response.get(0));
                            }
                            newWorker.setTrust(judgeMent.getWorker_trust());

                            workersMap.put(workerId, newWorker);
                            workerCopTime.put(workerId,judgeMent.getInterval());

                            if(workerCountryStat.containsKey(judgeMent.getCountry()))
                            {
                                int number = workerCountryStat.get(judgeMent.getCountry());
                                workerCountryStat.put(judgeMent.getCountry(),++number);
                            }
                            else
                            {
                                workerCountryStat.put(judgeMent.getCountry(),1);
                            }
                            if(workerCountry.containsKey(judgeMent.getCountry()))
                            {
                                if (workerCountry.get(judgeMent.getCountry()).containsKey(judgeMent.getCity()))
                                {
                                    int number = workerCountry.get(judgeMent.getCountry()).get(judgeMent.getCity());
                                    workerCountry.get(judgeMent.getCountry()).put(judgeMent.getCity(),++number);
                                }
                                else
                                {
                                    workerCountry.get(judgeMent.getCountry()).put(judgeMent.getCity(),1);
                                }
                            }
                            else
                            {
                                Map<String,Integer> city =new HashMap<>();
                                city.put(judgeMent.getCity(),1);
                                workerCountry.put(judgeMent.getCountry(),city);
                            }

                        }
                        workerTrust.add(Double.parseDouble(String.format("%.2f",judgeMent.getWorker_trust())));

                    }
                    resultRepresentation.getWorkerTrust().add(workerTrust);
                    resultRepresentation.getInterval().add(totalInteval/judgeList.size());

                    resultRepresentation.getAgreement().add((double)Collections.max(agreement.values())/judgeList.size());

                    resultRepresentation.getJudgementCount().add(judgeList.size());
                    resultRepresentation.getUnitId().add(unitId);

                });
                resultRepresentation.setUnitTotal(ExecutePlatformResultService.jobJudgement.get(jobId).size());
                int judgeTotal=0;
                for(int i=0;i<resultRepresentation.getJudgementCount().size();i++)
                {
                    judgeTotal+=resultRepresentation.getJudgementCount().get(i);
                }
                resultRepresentation.setJudgementTotal(judgeTotal);

                workerRepresentation.setWorkerNumber(workersMap.size());
                workerRepresentation.setWorkerId(new ArrayList<String>(workersMap.keySet()));
                List<Integer> completeTask = new ArrayList<>();
                List<Double> workerTrust = new ArrayList<>();
                List<Long> avgCompTime = new ArrayList<>();
                workersMap.forEach((key, value) -> {
                    completeTask.add(value.getNumResponses());
                    workerTrust.add(value.getTrust());
                    avgCompTime.add(workerCopTime.get(key)/value.getNumResponses());
                });
                workerRepresentation.setAvgCompleteTime(avgCompTime);
                workerRepresentation.setCompleteTaskNumer(completeTask);
                workerRepresentation.setWorkerTrust(workerTrust);
                workerRepresentation.setCountry(workerCountry);
                workerRepresentation.setCountryStat(workerCountryStat);
                Map<String,Object> serviceResult = new HashMap<>();
                serviceResult.put("success",true);
                serviceResult.put("worker", workerRepresentation);
                serviceResult.put("result", resultRepresentation);
                serviceResult.put("labelList", labelList);
                serviceResult.put("ServiceName", "FigureEightTask");
                Map<String,Object> labelReq = new HashMap<>();
                labelReq.put("labelList",labelList);
                System.out.println("labesize"+labelList.size());
                labelReq.put("answerEntityId",answerEntityId);
                dataCoreService.insertLabelList(labelReq);
                httpResponse = dataCoreService.insertServiceResult(serviceResult).getBody();
                // httpResponse.put("labelList", labelList);
                return httpResponse;
            }

            @Override
            public void run() {
                logger.info("请求结果"+jobId+apiKey);
                try {
                    Job job = jobService.getJob(jobId,apiKey);
                    //这里先去掉job.getAttribute(JobAttribute.COMPLETED.toString()).equals("true")||!job.getAttribute(JobAttribute.COMPLETED_AT.toString()).equals("null")
                    //迭代5次返回所有结果
                    System.out.println(job.getAttribute(JobAttribute.JUDGMENTS_PER_UNIT)+"www");
                    jobJudgement.computeIfAbsent(jobId, k -> new HashMap<>());
                    int redundancy = Integer.parseInt(job.getAttribute(JobAttribute.JUDGMENTS_PER_UNIT));
                    if(iteration==redundancy){
//                        judgementService.regenerateReport(jobId,apiKey);
//                        List<Judgment> judgmentList = judgementService.getReport(jobId,apiKey);
                        Map req = new HashMap();
                        req.put("answerEntityName",jobId);
                        req.put("metaDataId",metaDataId);
                        String key = UUID.randomUUID().toString();
                        req.put("key",key);//job.getAttribute(JobAttribute.COMPLETED_AT));
                        req = (Map) dataCoreService.insertAnswerEntity(req).getBody();
                        if((Boolean) req.get("success")){
                            String answerEntityId = req.get("answerEntityId").toString();

                            Map result = generateResult(jobId,answerEntityId);
                            if((Boolean) result.get("success")){
                                result.put("status","200");
                                workflowService.complete(taskId,result);
                            }
                            else {
                                logger.info("插入回答label失败");
                            }
                        }
                        else {
                            logger.info("插入回答实体类失败");
                        }
                        cronTaskScheduledFutureMap.get(jobId).cancel(true);
                    }
                    else {
                        List<Integer> test = new ArrayList<>();
                        jobToResult.get(processInstanceId).forEach(resultEntity -> {
                            String resultEntityId= resultEntity.getId();
                            if(unitIdList.contains(resultEntityId)){
                                jobJudgement.get(jobId).computeIfAbsent(resultEntityId, k -> new ArrayList<>());
                                int size = resultEntity.getJudgmentList().size();
                                if(size==0){
                                    //随机给一个答案
                                }
                                else {
                                    int pos = random.nextInt(size);
                                    jobJudgement.get(jobId).get(resultEntityId).add(resultEntity.getJudgmentList().get(pos));
                                    resultEntity.getJudgmentList().remove(pos);
                                    test.add(1);
                                }
                            }
                        });
                        System.out.println("此时的uint"+ unitIdList.size());
                        System.out.println("此时的label"+ test.stream().mapToInt(value->value).sum());
                    }
                    iteration++;
                } catch (NullAPIKeyException e) {
                    e.printStackTrace();
                }
            }
        };
        return runnable;
    }

    public ScheduledFuture<?> threadScheduler(String processInstanceId, Runnable runnable, String cron) {
        /*动态创建定时任务*/
        ScheduledFuture future = threadPoolTaskScheduler.schedule(runnable, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                return new CronTrigger(cron).nextExecutionTime(triggerContext);
            }
        });
        cronTaskScheduledFutureMap.put(processInstanceId, future);
        return future;
    }
}
