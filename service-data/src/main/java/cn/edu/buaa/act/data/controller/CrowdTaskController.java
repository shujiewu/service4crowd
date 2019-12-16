package cn.edu.buaa.act.data.controller;


import cn.edu.buaa.act.auth.client.annotation.IgnoreUserToken;
import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.data.entity.CrowdTaskEntity;
import cn.edu.buaa.act.data.model.CrowdTaskRepresentation;
import cn.edu.buaa.act.data.service.ICrowdTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping("/data")
@RestController
public class CrowdTaskController {
    @Autowired
    ICrowdTaskService crowdJobService;

    /**
     * @description 获取所有的人类任务
     * @date 2018/10/19
     * @param
     * @return
     */
    @RequestMapping(value = "/crowdTask/list", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<CrowdTaskRepresentation> getReportByUser() {
        CrowdTaskRepresentation crowdTaskRepresentation = new CrowdTaskRepresentation();
        String com = "2015-12-05T06:45:53+00:00";
        String create = "2015-12-05T04:27:05+00:00";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        try {
            Date date1 = df.parse(com);
            Date date2 = df.parse(create);
            crowdTaskRepresentation.setCompleteTime(date1);
            crowdTaskRepresentation.setCreateTime(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        crowdTaskRepresentation.setStatus("完成");
        crowdTaskRepresentation.setTaskId("839968");
        List<CrowdTaskRepresentation> result = new ArrayList<>();
        result.add(crowdTaskRepresentation);
        List<CrowdTaskEntity> crowdJobEntities = crowdJobService.queryAllByUser(BaseContextHandler.getUserID());
        crowdJobEntities.forEach(crowdJobEntity -> {
            CrowdTaskRepresentation crowdTaskRepresentation1 = new CrowdTaskRepresentation();
            crowdTaskRepresentation1.setCreateTime(crowdJobEntity.getCreateTime());
            crowdTaskRepresentation1.setTaskId(crowdJobEntity.getJobId());
            crowdTaskRepresentation1.setStatus(crowdJobEntity.getStatus());
            crowdTaskRepresentation1.setCompleteTime(crowdJobEntity.getEndTime());
            result.add(crowdTaskRepresentation1);
        });
        return new TableResultResponse<CrowdTaskRepresentation>(result.size(), result);
    }


    @RequestMapping(value = "/crowdTask/{processInstanceID}", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<CrowdTaskRepresentation> getCrowdTaskByProcessInstanceId(@PathVariable("processInstanceID") String processInstanceID) {
        List<CrowdTaskRepresentation> result = new ArrayList<>();
        CrowdTaskRepresentation crowdTaskRepresentation = new CrowdTaskRepresentation();
        String com = "2015-12-05T06:45:53+00:00";
        String create = "2015-12-05T04:27:05+00:00";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        try {
            Date date1 = df.parse(com);
            Date date2 = df.parse(create);
            crowdTaskRepresentation.setCompleteTime(date1);
            crowdTaskRepresentation.setCreateTime(date2);
            crowdTaskRepresentation.setJobType("HumanTask");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        crowdTaskRepresentation.setStatus("complete");
        crowdTaskRepresentation.setTaskId("839968");

        List<CrowdTaskEntity> crowdJobEntities = crowdJobService.queryAllByProcessId(processInstanceID);
        crowdJobEntities.forEach(crowdJobEntity -> {
            CrowdTaskRepresentation crowdTaskRepresentation1 = new CrowdTaskRepresentation();
            crowdTaskRepresentation1.setCreateTime(crowdJobEntity.getCreateTime());
            crowdTaskRepresentation1.setTaskId(crowdJobEntity.getTaskId());
            crowdTaskRepresentation1.setStatus(crowdJobEntity.getStatus());
            crowdTaskRepresentation1.setCompleteTime(crowdJobEntity.getEndTime());
            crowdTaskRepresentation1.setJobType(crowdJobEntity.getJobType());
            crowdTaskRepresentation1.setServiceName(crowdJobEntity.getServiceName());
            crowdTaskRepresentation1.setServiceResultId(crowdJobEntity.getServiceResultId());
            result.add(crowdTaskRepresentation1);
        });
        return new TableResultResponse<CrowdTaskRepresentation>(result.size(), result);
    }
    @IgnoreUserToken
    @RequestMapping(value = "/crowdTask/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> createCrowdTask(@RequestBody Map<String, String> request) {
        Map map = new HashMap();
        CrowdTaskEntity crowdJobEntity = new CrowdTaskEntity();
        crowdJobEntity.setCreateTime(new Date());
        crowdJobEntity.setActivityId(request.get("activityId"));
        crowdJobEntity.setProcessInstanceId(request.get("processInstanceId"));
        crowdJobEntity.setServiceName(request.get("serviceName"));
        crowdJobEntity.setJobType(request.get("jobType"));
        crowdJobEntity.setJobId(request.get("jobId"));
        crowdJobEntity.setTaskId(request.get("taskId"));
        crowdJobEntity.setStatus("create");
        crowdJobEntity.setUserId(request.get("userId"));
        crowdJobEntity=crowdJobService.insertEntity(crowdJobEntity);
        if(crowdJobEntity.getId()!=null){
            map.put("success",true);
        }
        else {
            map.put("success", false);
        }
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }
    @IgnoreUserToken
    @RequestMapping(value = "/crowdTask/complete", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> completeCrowdTask(@RequestBody Map<String, String> request) {
        Map map = new HashMap();
        // List<CrowdTaskEntity> crowdJobEntityList = crowdJobService.queryAllByProcessAndActivtity(request.get("processInstanceId"),request.get("activityId"));
        CrowdTaskEntity crowdTaskEntity = crowdJobService.queryByTaskId(request.get("taskId"));
        if(crowdTaskEntity!=null){
            crowdTaskEntity.setEndTime(new Date());
            crowdTaskEntity.setStatus(request.get("status"));
            crowdTaskEntity.setServiceResultId(request.get("serviceResultId"));
            crowdTaskEntity=crowdJobService.insertEntity(crowdTaskEntity);
        }
        if(crowdTaskEntity.getId()!=null) {
            map.put("success", true);
        } else {
            map.put("success", false);
        }
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @IgnoreUserToken
    @RequestMapping(value = "/crowdTask/asyncRun", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> asyncRunCrowdTask(@RequestBody Map<String, String> request) {
        Map map = new HashMap();
        CrowdTaskEntity crowdTaskEntity = crowdJobService.queryByTaskId(request.get("taskId"));
        if(crowdTaskEntity!=null){
            crowdTaskEntity.setStatus(request.get("status"));
            crowdTaskEntity=crowdJobService.insertEntity(crowdTaskEntity);
        }
        if(crowdTaskEntity.getId()!=null) {
            map.put("success", true);
        } else {
            map.put("success", false);
            map.put("message","Not Found");
        }
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }
}
