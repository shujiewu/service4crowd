package cn.edu.buaa.act.model.detection.channel;



import cn.edu.buaa.act.model.detection.common.Constants;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface MachineAnnotationChannel {
    @Input(Constants.MACHINE_ANNOTATION_RESPONSE)
    MessageChannel input();
    @Output(Constants.MACHINE_ANNOTATION_REQUEST)
    MessageChannel output();
}