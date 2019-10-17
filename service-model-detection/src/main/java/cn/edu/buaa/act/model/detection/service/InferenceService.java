package cn.edu.buaa.act.model.detection.service;

import cn.edu.buaa.act.common.msg.PlayLoadMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class InferenceService {
    @Async("asyncExecutor")
    public CompletableFuture<String> doInference(PlayLoadMessage<Map> message) throws InterruptedException {
        return CompletableFuture.completedFuture("Do doJoinLabel Complete");
    }
}
