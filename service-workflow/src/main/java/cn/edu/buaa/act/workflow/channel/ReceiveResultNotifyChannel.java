package cn.edu.buaa.act.workflow.channel;

import cn.edu.buaa.act.common.constant.NotifyChannelConstants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

/**
 * @author wsj
 */
@Component
public interface ReceiveResultNotifyChannel {

    @Output(NotifyChannelConstants.RECEIVE_RESULT_NOTIFY_CHANNEL)
    MessageChannel output();

    @Input(NotifyChannelConstants.RESULT_COMPLETE_NOTIFY_CHANNEL)
    MessageChannel input();
}