package cn.edu.buaa.act.model.detection.channel;


import cn.edu.buaa.act.model.detection.common.Constants;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface ModelTrainingChannel {
    @Output(Constants.MODEL_TRAINING_REQUEST)
    MessageChannel output();
}
