package cn.edu.buaa.act.model.detection.service;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.fastwash.data.DataItemEntity;
import cn.edu.buaa.act.fastwash.data.TrainingItem;
import cn.edu.buaa.act.model.detection.channel.ModelTrainingChannel;
import cn.edu.buaa.act.model.detection.common.Constants;
import cn.edu.buaa.act.model.detection.entity.InferenceTask;
import cn.edu.buaa.act.model.detection.entity.TrainingRequest;
import cn.edu.buaa.act.model.detection.entity.TrainingTask;
import cn.edu.buaa.act.model.detection.repository.TrainingTaskRepository;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableBinding({ModelTrainingChannel.class})
public class TrainingService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TrainingTaskRepository trainingTaskRepository;

    // TODO: 并发问题，应该投入队列
    public TrainingTask createTrainingTask(String projectName, String dataSetName){
        List<TrainingItem> trainingItems =findMachineTraining(projectName,dataSetName);
        if(trainingItems.size()==0){
            return null;
        }
        List<String> imageIdList = new ArrayList<>();
        trainingItems.forEach(trainingItem -> {
            imageIdList.add(trainingItem.getImageId());
        });
        Set<String> imageIdSet = new TreeSet<String>((Comparator<String>) (o1, o2) -> (Integer.parseInt(o1)-Integer.parseInt(o2)));
        imageIdSet.addAll(imageIdList);
        TrainingTask trainingTask = new TrainingTask();
        trainingTask.setCreateTime(new Date());
        trainingTask.setDataSetName(dataSetName);
        trainingTask.setUserId(BaseContextHandler.getUserID());
        trainingTask.setImageIdList(new ArrayList<>(imageIdSet));
        trainingTask.setProjectName(projectName);
        trainingTask.setStatus(Constants.TRAINING_TASK_CREATED);
        trainingTask = trainingTaskRepository.insert(trainingTask);
        trainingTask.setTrainingItemList(trainingItems);
        if(trainingTask.getId()!=null){
            return trainingTask;
        }else{
            return null;
        }
    }

    public List<TrainingItem> findMachineTraining(String projectName, String dataSetName){
        MongoCollection<Document> groundTruthCollection = mongoTemplate.getCollection(projectName+"_result");
        List<TrainingItem> trainingItemList = new ArrayList<>();
        try (MongoCursor<Document> cursor = groundTruthCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document origin = cursor.next();
                String str = origin.toJson();
                // System.out.println(str);
                TrainingItem trainingItem = JSONObject.parseObject(str, TrainingItem.class);
                trainingItemList.add(trainingItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trainingItemList;
    }

    @StreamListener(Constants.MODEL_TRAINING_RESPONSE)
    public void receiverTrainingNotify(Message<TrainingTask> message) {
        TrainingTask trainingTask = message.getPayload();
        System.out.println("接收到id:"+trainingTask.getId());
        System.out.println("接收到result:"+trainingTask.getTrainingItemList().size());
        TrainingTask originTask = trainingTaskRepository.findById(trainingTask.getId()).orElse(null);
        if(originTask!=null){
            if(Constants.TRAINING_TASK_SUCCESS.equals(trainingTask.getStatus())){
                originTask.setStatus(Constants.TRAINING_TASK_SUCCESS);
                originTask.setEndTime(new Date());
                trainingTaskRepository.save(originTask);
            }
        }
    }




    public TrainingTask createProcessTrainingTask(String processInstanceId, TrainingRequest trainingRequest){
        List<String> imageIdList = trainingRequest.getImageIdList();
        Set<String> imageIdSet = new TreeSet<String>((Comparator<String>) (o1, o2) -> (Integer.parseInt(o1)-Integer.parseInt(o2)));
        imageIdSet.addAll(imageIdList);
        TrainingTask trainingTask = new TrainingTask();
        trainingTask.setCreateTime(new Date());
        trainingTask.setDataSetName(trainingRequest.getDataSetName());
        trainingTask.setUserId(BaseContextHandler.getUserID());
        trainingTask.setImageIdList(new ArrayList<>(imageIdSet));
        trainingTask.setProcessInstanceId(processInstanceId);
        trainingTask.setStatus(Constants.TRAINING_TASK_CREATED);
        trainingTask.setTrainingItemList(trainingRequest.getTrainingItemList());
        trainingTask = trainingTaskRepository.insert(trainingTask);
        if(trainingTask.getId()!=null){
            return trainingTask;
        }else{
            return null;
        }
    }
}
