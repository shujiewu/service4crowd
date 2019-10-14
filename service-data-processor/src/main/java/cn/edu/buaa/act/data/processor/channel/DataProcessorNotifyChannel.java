package cn.edu.buaa.act.data.processor.channel;

import cn.edu.buaa.act.common.constant.NotifyChannelConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface DataProcessorNotifyChannel {
    @Input(NotifyChannelConstants.DATA_PROCESSOR_CHANNEL)
    MessageChannel input();
    @Output(NotifyChannelConstants.RESULT_COMPLETE_NOTIFY_CHANNEL)
    MessageChannel output();
}