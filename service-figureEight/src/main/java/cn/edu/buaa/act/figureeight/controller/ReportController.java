package cn.edu.buaa.act.figureeight.controller;

import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.figureeight.entity.JudgmentItem;
import cn.edu.buaa.act.figureeight.feign.IDataCoreService;
import cn.edu.buaa.act.figureeight.model.*;
import cn.edu.buaa.act.figureeight.service.ExecutePlatformResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ReportController
 *
 * @author wsj
 * @date 2018/10/28
 */
@RequestMapping("/figure-eight")
@RestController
public class ReportController {

    @Autowired
    private IDataCoreService dataCoreService;

    @RequestMapping(value = "/job/{jobId}/status", method = RequestMethod.POST)
    private Map generateResult(@PathVariable String jobId, String answerEntityId){
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
                judgeMent.getData().forEach((key, value) -> {
                    question.add(judgeMent.getUnit_id()+"-"+key);
                    response.add(value);
//                    if(metaDataId.equals("")){
//                        if(key.equals("taxonomyresponse_id")){
//                            if(isInteger(value)){
//                                response.add(value);
//                            }
//                            else {
//                                response.add("7");
//                            }
//                        }
//                    }else {
//                        response.add(value);
//                    }
                });
                Label label =new Label();
                label.setWorker(workerId);
                label.setAnswer(response.get(0));///默认为1个，其实并不是
                label.setItem(unitId);
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
        labelReq.put("answerEntityId",answerEntityId);
        dataCoreService.insertLabelList(labelReq);
        httpResponse = dataCoreService.insertServiceResult(serviceResult).getBody();
        // httpResponse.put("labelList", labelList);
        return httpResponse;
    }
}
