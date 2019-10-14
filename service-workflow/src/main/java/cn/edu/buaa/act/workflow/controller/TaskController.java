package cn.edu.buaa.act.workflow.controller;

import cn.edu.buaa.act.auth.client.annotation.IgnoreUserToken;
import cn.edu.buaa.act.common.constant.NotifyChannelConstants;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.common.msg.PlayLoadMessage;
import cn.edu.buaa.act.workflow.service.impl.ExecuteProcessService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * TaskController
 *
 * @author wsj
 * @date 2018/10/23
 */

@Slf4j
@RestController
@RequestMapping("/workflow")
public class TaskController {

    @IgnoreUserToken
    @RequestMapping(value = "/task/{taskId}/complete",method = RequestMethod.POST)
    public ResponseEntity<String> complete(@PathVariable String taskId,@RequestBody Map<String,Object> request){
        log.info(taskId+"任务完成");
        request.put("status","200");
        ExecuteProcessService.completeMap.put(taskId,request);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
