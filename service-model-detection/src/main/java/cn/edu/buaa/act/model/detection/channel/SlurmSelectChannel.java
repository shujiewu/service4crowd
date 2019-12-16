package cn.edu.buaa.act.model.detection.channel;


import cn.edu.buaa.act.model.detection.common.Constants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface SlurmSelectChannel {
    @Input(Constants.SLURM_SELECT_RESPONSE)
    MessageChannel input();
    @Output(Constants.SLURM_SELECT_REQUEST)
    MessageChannel output();
}
