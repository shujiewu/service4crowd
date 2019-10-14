package cn.edu.buaa.act.figureeight.controller;

import cn.edu.buaa.act.auth.client.annotation.IgnoreUserToken;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.common.util.ServiceProperty;
import cn.edu.buaa.act.figureeight.constant.Constants;
import cn.edu.buaa.act.figureeight.constant.JobAttribute;
import cn.edu.buaa.act.figureeight.entity.JudgmentItem;
import cn.edu.buaa.act.figureeight.entity.ResultEntity;
import cn.edu.buaa.act.figureeight.exception.NullAPIKeyException;
import cn.edu.buaa.act.figureeight.feign.IDataCoreService;
import cn.edu.buaa.act.figureeight.model.*;
import cn.edu.buaa.act.figureeight.service.ExecutePlatformResultService;
import cn.edu.buaa.act.figureeight.service.JobService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author wsj
 */
@RequestMapping("/figure-eight")
@RestController
public class JobRestController {
    @Autowired
    private JobService jobService;

//    @Autowired
//    private AsyncTasks asyncTasks;

    @IgnoreUserToken
    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    public TableResultResponse<Job> getJobs(@RequestParam("APIKey") String apiKey) {
        System.out.println(222);
        List<Job> jobList =  jobService.getJobs(apiKey);
        return new TableResultResponse<>(jobList.size(),jobList);
    }

    @RequestMapping(value = "/job/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<Job> getJob(@PathVariable String jobId, @RequestParam("APIKey") String apiKey) throws NullAPIKeyException {
        Job job = jobService.getJob(jobId,apiKey);
        return new ResponseEntity<Job>(job, HttpStatus.OK);
    }


    @Autowired
    IDataCoreService dataCoreService;

    @IgnoreUserToken
    @RequestMapping(value = "/job/create", method = RequestMethod.POST)
    public Map createJob(@RequestParam("APIKey") String apiKey,@RequestParam("userId") String userId,@RequestBody MultiValueMap<String,Object> body) throws NullAPIKeyException {
        System.out.println(apiKey);
        Job myJob = new Job();
        Map<String, Object> varMap = body.toSingleValueMap();
        varMap.remove("APIKey");
        varMap.remove("userId");
        String taskId = varMap.get("taskId").toString();
        String serviceName = varMap.get("serviceName").toString();
        varMap.remove("taskId");
        varMap.remove("serviceName");
        varMap.remove("processInstanceId");
       // System.out.println(unitEntityList.size());
        JSONArray unitDataList =   JSONArray.parseArray(varMap.get("UnitDataList").toString());//(JSONArray) JSON.toJSON(body.get("UnitDataList"));
        List<String> unitDataListId = JSON.parseArray(varMap.get("UnitDataListId").toString(),String.class);
        System.out.println("ce");
        //    System.out.println(serviceProperties.values());
        String csvFilePath = "D://servicedata/temp/"+taskId+".csv";
        try {
            // 创建CSV写对象 例如:CsvWriter(文件路径，分隔符，编码格式);
            CsvWriter csvWriter = new CsvWriter(csvFilePath, ',', Charset.forName("UTF-8"));
            // 写表头

            List<String> headers =  new ArrayList<>(unitDataList.getJSONObject(0).getJSONObject("data").keySet());
            csvWriter.writeRecord(headers.toArray(new String[headers.size()]));
            for(int i = 0; i<unitDataList.size();i++){
                JSONObject unit = unitDataList.getJSONObject(i);
                JSONObject data = unit.getJSONObject("data");
                if(unitDataListId.contains(data.get("_unit_id").toString())){
                    String[] csvContent = new String[headers.size()];
                    for(int j = 0;j<headers.size();j++){
                        csvContent[j] = data.get(headers.get(j)).toString();
                    }
                    csvWriter.writeRecord(csvContent);
                }
            }
            csvWriter.close();
            System.out.println("--------CSV文件已经写入--------");
        } catch (IOException e) {
            e.printStackTrace();
        }

        varMap.remove("UnitDataList");
        varMap.remove("UnitDataListId");

        varMap.forEach((key,value)->{
            if(value!=null&&!value.toString().isEmpty()){
                myJob.addProperty(key, (String) value);
            }
        });

        //暂时去掉
        Job myJobAfterCreation = jobService.create(myJob,apiKey);
        //jobService.upload(myJobAfterCreation,apiKey,csvFilePath,"text/csv");
        Map result = new HashMap();
        result.put("jobId",myJobAfterCreation.getId());
        result.put("job",myJobAfterCreation);
        result.put("serviceName",serviceName);
//        Map result = new HashMap();
//        result.put("jobId","11");
//        result.put("job",new Job());
//        result.put("serviceName",serviceName);

        ResponseEntity<Map> responseEntity  = dataCoreService.insertServiceResult(result);

        if(responseEntity.getStatusCode().equals(HttpStatus.OK)){
            result.put("serviceResultId",responseEntity.getBody().get("serviceResultId"));
            result.put("success",true);
        }else {
            result.put("success",false);
        }
        return result;

//        try {
//            asyncTasks.upLoadData(serviceProperties);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        varMap.put("finish","true");
//        varMap.put("jobID",myJobAfterCreation.getId());
//        String res=restTemplate.getForObject("http://SPRING-CLOUD-CONSUMER/finish?finish={finish}&&processInstanceID={processInstanceID}&&taskID={taskID}&&jobID={jobID}",String.class,varMap);
        //myJobController.setPayPerAssignment(myJobAfterCreation,serviceProperties.get("payment_cents").getValue());

        //varMap.put("API_KEY",serviceProperties.get("API_KEY").getValue());
//        myJob.addProperty(JobAttribute.CML, serviceProperties.get(JobAttribute.CML.toString()).getValue());
//        myJob.addProperty(JobAttribute.TITLE, serviceProperties.get(JobAttribute.TITLE.toString()).getValue());
//        myJob.addProperty(JobAttribute.INSTRUCTIONS, serviceProperties.get(JobAttribute.INSTRUCTIONS.toString()).getValue());
//        myJob.addProperty(JobAttribute.UNITS_PER_ASSIGNMENT, serviceProperties.get(JobAttribute.UNITS_PER_ASSIGNMENT.toString()).getValue());
//        myJob.addProperty(JobAttribute.JUDGMENTS_PER_UNIT, serviceProperties.get(JobAttribute.JUDGMENTS_PER_UNIT.toString()).getValue());
        //varMap.put(JobAttribute.CML.toString(),serviceProperties.get(JobAttribute.CML.toString()).getValue());
        //varMap.put(JobAttribute.TITLE.toString(),serviceProperties.get(JobAttribute.TITLE.toString()).getValue());
        //varMap.put(JobAttribute.INSTRUCTIONS.toString(),serviceProperties.get(JobAttribute.INSTRUCTIONS.toString()).getValue());
        //varMap.put(JobAttribute.CML.toString(),serviceProperties.get(JobAttribute.CML.toString()).getValue());
    }

//    @RequestMapping(value = "/job/uploadData", method = RequestMethod.POST)
//    public String uploadData(@RequestBody Map<String, ServiceProperty> serviceProperties) {
//        String apiKey = serviceProperties.get(Constants.API_KEY).getValue().toString();
//        serviceProperties.remove(Constants.API_KEY);
//
//        try {
//            Job job = jobService.getJob(serviceProperties.get(JobAttribute.ID.toString()).getValue().toString(),apiKey);
//            jobService.upload(job, serviceProperties.get(Constants.META_PATH).getValue(), "application/json", true);
//        } catch (NullAPIKeyException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    Future<String> futureResult;


//    @RequestMapping(value = "/job/create", method = RequestMethod.POST)
//    public String createJob(@RequestBody Map<String, ServiceProperty> serviceProperties) throws NullAPIKeyException {
//        JobController myJobController = CrowdFlowerFactory.getJobController();
//        myJobController.setApiKey(serviceProperties.get(Constants.API_KEY).getValue());
//        serviceProperties.remove(Constant.API_KEY);
//
//        Job myJob = new Job();
//        Map<String, String> varMap = new HashMap<>();
//        System.out.println(serviceProperties.toString());
//        myJob.addProperties(serviceProperties);
//        Job myJobAfterCreation = myJobController.create(myJob);
//        // myJobController.upload(myJobAfterCreation, serviceProperties.get(Constant.META_PATH).getValue(), "application/json", true);
//
//        try {
//            futureResult = asyncTasks.upLoadData(myJobController,myJobAfterCreation);
//            if(!futureResult.isDone())
//                return "success";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "fail";
//        }
//        return "success";
//
////        try {
////            asyncTasks.upLoadData(serviceProperties);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        varMap.put("finish","true");
////        varMap.put("jobID",myJobAfterCreation.getId());
////        String res=restTemplate.getForObject("http://SPRING-CLOUD-CONSUMER/finish?finish={finish}&&processInstanceID={processInstanceID}&&taskID={taskID}&&jobID={jobID}",String.class,varMap);
//        //myJobController.setPayPerAssignment(myJobAfterCreation,serviceProperties.get("payment_cents").getValue());
//
//        //varMap.put("API_KEY",serviceProperties.get("API_KEY").getValue());
////        myJob.addProperty(JobAttribute.CML, serviceProperties.get(JobAttribute.CML.toString()).getValue());
////        myJob.addProperty(JobAttribute.TITLE, serviceProperties.get(JobAttribute.TITLE.toString()).getValue());
////        myJob.addProperty(JobAttribute.INSTRUCTIONS, serviceProperties.get(JobAttribute.INSTRUCTIONS.toString()).getValue());
////        myJob.addProperty(JobAttribute.UNITS_PER_ASSIGNMENT, serviceProperties.get(JobAttribute.UNITS_PER_ASSIGNMENT.toString()).getValue());
////        myJob.addProperty(JobAttribute.JUDGMENTS_PER_UNIT, serviceProperties.get(JobAttribute.JUDGMENTS_PER_UNIT.toString()).getValue());
//        //varMap.put(JobAttribute.CML.toString(),serviceProperties.get(JobAttribute.CML.toString()).getValue());
//        //varMap.put(JobAttribute.TITLE.toString(),serviceProperties.get(JobAttribute.TITLE.toString()).getValue());
//        //varMap.put(JobAttribute.INSTRUCTIONS.toString(),serviceProperties.get(JobAttribute.INSTRUCTIONS.toString()).getValue());
//        //varMap.put(JobAttribute.CML.toString(),serviceProperties.get(JobAttribute.CML.toString()).getValue());
//    }


    @RequestMapping(value = "/job/{jobId}/pause", method = RequestMethod.POST)
    public String pauseJob(@PathVariable String jobId,@RequestParam("APIKey") String apiKey) {
        jobService.pause(jobId, apiKey);
        return "success";
    }

    //暂时无用
    @RequestMapping(value = "/job/{jobId}/delete", method = RequestMethod.POST)
    public String deleteJob(@PathVariable String jobId,@RequestParam("APIKey") String apiKey) {
        jobService.delete(jobId,apiKey);
        return "success";
    }


    @Autowired
    private ExecutePlatformResultService executePlatformResultService;

    //暂时无用
    @IgnoreUserToken
    @RequestMapping(value = "/job/{jobId}/result", method = RequestMethod.POST)
    public Map getResult(@PathVariable String jobId,@RequestParam("APIKey") String apiKey,@RequestBody MultiValueMap<String,Object> body) {
        Map<String,Object> request = body.toSingleValueMap();


        String metaDataId = request.get("MetaDataId").toString();
        List<String> unitDataIdList = JSON.parseArray(request.get("UnitDataListId").toString(),String.class);


        //对于一部的服务，需要知道taskID
        String taskId = request.get("taskId").toString();
        //这里不需要对外开放，只是暂时仿真需要
        String processInstanceId = request.get("processInstanceId").toString();

        if(metaDataId.equals("5bd95a4dc5b1fe33384840e9")){
            //这里是行人计数任务
            executePlatformResultService.loadAllResultEntity("D://job_839968.json",processInstanceId);
        }
        else {
            //dog任务
            executePlatformResultService.loadAllResultEntity("D://job_981107.json",processInstanceId);
        }


        executePlatformResultService.threadScheduler(jobId,executePlatformResultService.createGetResultRunnable(taskId,jobId,apiKey,metaDataId,unitDataIdList,processInstanceId), "0/5 * * * * *");


        Map result = new HashMap();
        result.put("success",true);
        return result;
    }

//    @RequestMapping(value = "/{jobId}/result/info", method = RequestMethod.POST)
//    public String getResultStatus(@PathVariable String jobId,@RequestParam("APIKey") String apiKey) {
//
//        executePlatformResultService.threadScheduler(processInstanceID,executePlatformResultService.createRunnable(processInstanceID), "0/5 * * * * *");
//
//        jobService.delete(jobId,apiKey);
//        return "success";
//    }


//    @RequestMapping(value = "/job/{jobId}/status", method = RequestMethod.POST)
//    public Map<String,Object> status(@PathVariable String jobId,@RequestParam("APIKey") String apiKey) {
//        return new HashMap<>();
//    }
}
