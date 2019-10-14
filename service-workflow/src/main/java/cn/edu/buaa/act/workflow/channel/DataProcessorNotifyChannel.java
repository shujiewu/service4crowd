package cn.edu.buaa.act.workflow.channel;

import cn.edu.buaa.act.common.constant.NotifyChannelConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface DataProcessorNotifyChannel {
    @Output(NotifyChannelConstants.DATA_PROCESSOR_CHANNEL)
    MessageChannel output();
}