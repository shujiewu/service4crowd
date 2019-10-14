package cn.edu.buaa.act.workflow.controller;

import cn.edu.buaa.act.common.constant.NotifyChannelConstants;
import cn.edu.buaa.act.common.msg.PlayLoadMessage;
import cn.edu.buaa.act.workflow.channel.AlgorithmNotifyChannel;
import cn.edu.buaa.act.workflow.channel.ReceiveResultNotifyChannel;
import cn.edu.buaa.act.workflow.feign.IDataCoreService;
import cn.edu.buaa.act.workflow.service.impl.ExecuteProcessService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;

import java.util.Map;

/**
 * MessageQueueListener
 *
 * @author wsj
 * @date 2018/10/25
 */
@Slf4j
@EnableBinding({AlgorithmNotifyChannel.class})
public class AlgorithmMessageListener {

    @Autowired
    IDataCoreService dataCoreService;

    @StreamListener(NotifyChannelConstants.ALGORITHM_PROCESSOR_COMPLETE_NOTIFY_CHANNEL)
    public void receiverMergeNotify(Message<byte[]> message) {
        String str = new String(message.getPayload());
        str = str.replaceAll("True", "true");
        System.out.println(str);
        JSONObject jsonObject = JSONObject.parseObject(str);
        jsonObject.put("status","200");

        ResponseEntity<Map> responseEntity  = dataCoreService.insertServiceResult(jsonObject);

        if(responseEntity.getStatusCode().equals(HttpStatus.OK)){
            jsonObject.put("serviceResultId",responseEntity.getBody().get("serviceResultId"));
            jsonObject.put("success",true);
        }else {
            jsonObject.put("success",false);
        }
        ExecuteProcessService.completeMap.put(jsonObject.getString("taskId"),jsonObject);
        log.info("频道" + NotifyChannelConstants.ALGORITHM_PROCESSOR_COMPLETE_NOTIFY_CHANNEL + "收到信息");
    }
}
