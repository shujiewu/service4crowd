package cn.edu.buaa.act.workflow.controller;

import cn.edu.buaa.act.common.constant.NotifyChannelConstants;
import cn.edu.buaa.act.common.msg.PlayLoadMessage;
import cn.edu.buaa.act.workflow.channel.DataProcessorNotifyChannel;
import cn.edu.buaa.act.workflow.channel.ReceiveResultNotifyChannel;
import cn.edu.buaa.act.workflow.feign.IDataCoreService;
import cn.edu.buaa.act.workflow.service.impl.ExecuteProcessService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * MessageQueueListener
 *
 * @author wsj
 * @date 2018/10/25
 */
@Slf4j
@EnableBinding({ReceiveResultNotifyChannel.class})
public class MessageQueueListener {

    @StreamListener(NotifyChannelConstants.RESULT_COMPLETE_NOTIFY_CHANNEL)
    public void receiverPdfNotify(Message<PlayLoadMessage> message) {
        PlayLoadMessage<Map> playLoadMessage= message.getPayload();
        log.info("频道"+NotifyChannelConstants.RESULT_COMPLETE_NOTIFY_CHANNEL+"收到信息");

        playLoadMessage.getMessage().put("status","200");
        playLoadMessage.getMessage().put("serviceResultId",message.getPayload().getServiceResultId());
        ExecuteProcessService.completeMap.put(playLoadMessage.getTaskId(),playLoadMessage.getMessage());
    }
}
