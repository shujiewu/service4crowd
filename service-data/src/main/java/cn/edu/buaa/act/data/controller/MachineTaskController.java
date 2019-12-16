package cn.edu.buaa.act.data.controller;


import cn.edu.buaa.act.auth.client.annotation.IgnoreUserToken;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.data.entity.MachineTaskEntity;
import cn.edu.buaa.act.data.model.MachineTaskRepresentation;
import cn.edu.buaa.act.data.service.IMachineTaskService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author wsj
 */
@RequestMapping("/data")
@RestController
public class MachineTaskController {

    @Autowired
    private IMachineTaskService machineJobService;

    @RequestMapping(value = "/machineTask/{processInstanceID}", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<MachineTaskRepresentation> getMachineJobByProcessInstanceId(@PathVariable("processInstanceID") String processInstanceID) {
        List<MachineTaskRepresentation> result = new ArrayList<>();
        List<MachineTaskEntity> crowdJobEntities = machineJobService.queryAllByProcessId(processInstanceID);
        crowdJobEntities.forEach(crowdJobEntity -> {
            MachineTaskRepresentation machineJobRepresentation = new MachineTaskRepresentation();
            machineJobRepresentation.setCreateTime(crowdJobEntity.getCreateTime());
            machineJobRepresentation.setCompleteTime(crowdJobEntity.getEndTime());
            machineJobRepresentation.setTaskId(crowdJobEntity.getTaskId());
            machineJobRepresentation.setStatus(crowdJobEntity.getStatus());
            machineJobRepresentation.setServiceName(crowdJobEntity.getServiceName());
            machineJobRepresentation.setServiceResultId(crowdJobEntity.getServiceResultId());
            result.add(machineJobRepresentation);
        });
        return new TableResultResponse<MachineTaskRepresentation>(result.size(), result);
    }

    @IgnoreUserToken
    @RequestMapping(value = "/machineTask/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> createMachineTask(@RequestBody Map<String, String> request) {
        Map result = new HashMap();
        MachineTaskEntity machineEntity = new MachineTaskEntity();
        machineEntity.setActivityId(request.get("activityId"));
        machineEntity.setCreateTime(new Date());
        machineEntity.setProcessInstanceId(request.get("processInstanceId"));
        machineEntity.setStatus("create");
        machineEntity.setUserId(request.get("userId"));
        machineEntity.setServiceName(request.get("serviceName"));
        machineEntity.setTaskId(request.get("taskId"));
        machineJobService.insertEntity(machineEntity);
        result.put("success",true);
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    @IgnoreUserToken
    @RequestMapping(value = "/machineTask/complete", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> completeMachineTask(@RequestBody Map<String, String> request) {
        MachineTaskEntity machineTaskEntity = machineJobService.queryByTaskId(request.get("taskId"));
        Map result = new HashMap();
        if(machineTaskEntity!=null){
            machineTaskEntity.setStatus(request.get("status"));
            machineTaskEntity.setServiceResultId(request.get("serviceResultId"));
            machineTaskEntity.setEndTime(new Date());
            machineJobService.insertEntity(machineTaskEntity);
            result.put("success",true);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
        else {
            result.put("success",false);
            result.put("message","Not Found");
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
    }

    @IgnoreUserToken
    @RequestMapping(value = "/machineTask/asyncRun", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> asyncRunMachineTask(@RequestBody Map<String, String> request) {
        MachineTaskEntity machineTaskEntity = machineJobService.queryByTaskId(request.get("taskId"));
        Map result = new HashMap();
        if(machineTaskEntity!=null){
            machineTaskEntity.setStatus(request.get("status"));
            machineJobService.insertEntity(machineTaskEntity);
            result.put("success",true);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
        else {
            result.put("success",false);
            result.put("message","Not Found");
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
    }
}
