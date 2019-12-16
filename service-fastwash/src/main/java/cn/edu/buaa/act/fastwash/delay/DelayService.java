package cn.edu.buaa.act.fastwash.delay;


import cn.edu.buaa.act.fastwash.data.TaskItemEntity;
import cn.edu.buaa.act.fastwash.feign.IDataCoreService;
import cn.edu.buaa.act.fastwash.feign.IWorkflowService;
import cn.edu.buaa.act.fastwash.service.AnnotationService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DelayService {

    //TODO：线程不安全
    private  Consumer consumer;

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private IDataCoreService iDataCoreService;

    @Autowired
    private IWorkflowService iWorkflowService;

    public void addToDelayQueue(Message delayMessage){
        if(consumer==null){
            DelayQueue<Message> queue = new DelayQueue<Message>();
            consumer = new Consumer(queue);
            ExecutorService exec = Executors.newFixedThreadPool(1);
            exec.execute(new Consumer(queue));
        }
        consumer.getQueue().offer(delayMessage);
    }

    public void findRecentTask(Message delayMessage){
        List<TaskItemEntity> taskItemEntityList = annotationService.getRecentCompleteTasks(delayMessage.getProjectName());
        if(taskItemEntityList.size()==0){
            delayMessage.setExecuteTime(delayMessage.getDelayTime());
            consumer.getQueue().offer(delayMessage);
            System.out.println("addToMessageQueue"+delayMessage.getTaskId());
        }
        else{
            System.out.println("delayGetResult"+taskItemEntityList.size());
            JSONObject result = new JSONObject();
            result.put("recentResult",taskItemEntityList);
            result.put("projectName",delayMessage.getProjectName());
            result.put("success",true);
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            iWorkflowService.complete(delayMessage.getTaskId(),result);
        }
    }
}
