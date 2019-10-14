package cn.edu.buaa.act.data.service;


import cn.buaa.act.datacore.common.ReadJsonFile;
import cn.buaa.act.datacore.entity.JudgeMent;
import cn.buaa.act.datacore.entity.ResultEntity;
import cn.buaa.act.datacore.entity.WorkersDataStruct;
import cn.buaa.act.datacore.model.ResultRepresentation;
import cn.buaa.act.datacore.model.WorkerRepresentation;
import cn.buaa.act.datacore.service.api.IResultService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ResultServiceImpl implements IResultService {


    private static class parseTypeWR{
        private static String workerId = null;
        private static String question = null;
        private static String response = null;
    }
    Map<String,WorkersDataStruct<String,String>> workersMap = new HashMap<String, WorkersDataStruct<String,String>>(); //多个question，这里用list
    Map<String,Map<String,Integer>> workerCountry = new HashMap<>();
    Map<String,Integer> workerCountryStat = new HashMap<>();
    ResultRepresentation resultRepresentation = new ResultRepresentation();
    Map<String,Long> workerCopTime;
    private void parseWorkerLabels()
    {
        resultRepresentation = new ResultRepresentation();
        workersMap = new HashMap<String, WorkersDataStruct<String,String>>(); //多个question，这里用list
        workerCountry = new HashMap<>();
        workerCountryStat = new HashMap<>();
        workerCopTime = new HashMap<>();

        List<JSONObject> data = ReadJsonFile.ReadFile("d:/job_839968.json");

        for(JSONObject jsonObject:data)
        {
            ResultEntity resultEntity = jsonObject.toJavaObject(ResultEntity.class);
            JSONObject results= resultEntity.getResults();
            JSONArray array = results.getJSONArray("judgments");
            resultEntity.setJudgeMentList(JSON.parseArray(array.toString(),JudgeMent.class));

            double totalInteval = 0;
            List<Double> workerTrust= new ArrayList<>();
            for(JudgeMent judgeMent: resultEntity.getJudgeMentList())
            {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                try {
                    Date date1 =df.parse(judgeMent.getStarted_at());
                    Date date2 =df.parse(judgeMent.getCreated_at());
                    judgeMent.setInterval((date2.getTime()-date1.getTime())/1000);
                    totalInteval +=judgeMent.getInterval();
                    // System.out.println((date1.getTime()-date2.getTime())/1000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                parseTypeWR.workerId = judgeMent.getWorker_id();
                judgeMent.getData().forEach((key, value) -> {
                    parseTypeWR.question=judgeMent.getUnit_id()+"-"+key;
                    parseTypeWR.response=value;
                });
                if(workersMap.containsKey(parseTypeWR.workerId)){
                    WorkersDataStruct<String,String> currentWorkerStruct = workersMap.get(parseTypeWR.workerId);
                    currentWorkerStruct.insertWorkerResponse(parseTypeWR.question, parseTypeWR.response);

                    if(currentWorkerStruct.getTrust()!=judgeMent.getWorker_trust())
                    {
                        System.out.println("trust not equal");
                        currentWorkerStruct.setTrust(judgeMent.getWorker_trust());
                    }
                    workersMap.put(parseTypeWR.workerId, currentWorkerStruct);
                    workerCopTime.put(parseTypeWR.workerId,judgeMent.getInterval()+workerCopTime.get(parseTypeWR.workerId));
                }
                else
                {
                    WorkersDataStruct<String,String> newWorker = new WorkersDataStruct<String,String>();
                    newWorker.insertWorkerResponse(parseTypeWR.question, parseTypeWR.response);

                    newWorker.setTrust(judgeMent.getWorker_trust());

                    workersMap.put(parseTypeWR.workerId, newWorker);
                    workerCopTime.put(parseTypeWR.workerId,judgeMent.getInterval());

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

//                parseTypeWR.question = judgeMent.g;
//                parseTypeWR.response = lineScan.next();

            }
            resultRepresentation.getWorkerTrust().add(workerTrust);
            resultRepresentation.getInterval().add(totalInteval/resultEntity.getJudgeMentList().size());
            resultRepresentation.getAgreement().add(resultEntity.getAgreement());
            resultRepresentation.getJudgementCount().add(resultEntity.getJudgments_count());
            resultRepresentation.getUnitId().add(resultEntity.getId());
        }
        resultRepresentation.setUnitTotal(data.size());

        int judgeTotal=0;
        for(int i=0;i<resultRepresentation.getJudgementCount().size();i++)
        {
            judgeTotal+=resultRepresentation.getJudgementCount().get(i);
        }
        resultRepresentation.setJudgementTotal(judgeTotal);

        //resultRepresentation.setJudgementTotal();
        System.out.println(workersMap.size());
    }

    public ResultRepresentation getResultRepresentation() {
        return resultRepresentation;
    }

    public WorkerRepresentation getWorkerStatistical()
    {
        parseWorkerLabels();
        WorkerRepresentation workerRepresentation= new WorkerRepresentation();
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
        return  workerRepresentation;
    }


}

