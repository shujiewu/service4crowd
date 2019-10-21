package cn.edu.buaa.act.model.detection.service;

import cn.edu.buaa.act.common.constant.NotifyChannelConstants;
import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.msg.PlayLoadMessage;
import cn.edu.buaa.act.fastwash.data.Annotation;
import cn.edu.buaa.act.fastwash.data.Box;
import cn.edu.buaa.act.fastwash.data.Classification;
import cn.edu.buaa.act.fastwash.data.DataItemEntity;
import cn.edu.buaa.act.model.detection.channel.MachineAnnotationChannel;
import cn.edu.buaa.act.model.detection.channel.ModelTrainingChannel;
import cn.edu.buaa.act.model.detection.common.Constants;
import cn.edu.buaa.act.model.detection.entity.InferenceTask;
import cn.edu.buaa.act.model.detection.repository.InferenceTaskRepository;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static cn.edu.buaa.act.model.detection.common.MongoUtil.toDocument;

@Service
@EnableBinding({MachineAnnotationChannel.class})
public class InferenceService {

    @Autowired
    private InferenceTaskRepository inferenceTaskRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<InferenceTask> findRuntimeInferenceTask(String projectName, String dataSetName){
        return inferenceTaskRepository.findInferenceTasksByUserIdAndProjectNameAndDataSetNameAndStatus(BaseContextHandler.getUserID(),projectName,dataSetName,Constants.INFERENCE_TASK_CREATED);
    }

    // TODO: 并发问题，应该投入队列
    public InferenceTask createInferenceTask(String projectName, String dataSetName,List<String> imageIdList){
        Set<String> imageIdSet = new HashSet<>(imageIdList);
        List<InferenceTask> inferenceRuntimeTasks = findRuntimeInferenceTask(projectName,dataSetName);
        inferenceRuntimeTasks.forEach(inferenceTask -> {
            inferenceTask.getImageIdList().forEach(imageId->{
                if(imageIdSet.contains(imageId)){
                    imageIdSet.remove(imageId);
                }
            });
        });
        if(imageIdSet.size()==0)
            return null;
        InferenceTask inferenceTask = new InferenceTask();
        inferenceTask.setCreateTime(new Date());
        inferenceTask.setDataSetName(dataSetName);
        inferenceTask.setUserId(BaseContextHandler.getUserID());
        inferenceTask.setImageIdList(new ArrayList<>(imageIdSet));
        inferenceTask.setProjectName(projectName);
        inferenceTask.setStatus(Constants.INFERENCE_TASK_CREATED);
        return inferenceTaskRepository.insert(inferenceTask);
    }

    @Async("asyncExecutor")
    public CompletableFuture<String> doInference(String projectName, String dataSetName, List<String> imageId) throws InterruptedException {

        return CompletableFuture.completedFuture("Do doJoinLabel Complete");
    }

    @StreamListener(Constants.MACHINE_ANNOTATION_RESPONSE)
    public void receiverInferenceNotify(Message<InferenceTask> message) {
        InferenceTask inferenceTask = message.getPayload();
        System.out.println("接收到id:"+inferenceTask.getId());
        System.out.println("接收到result:"+inferenceTask.getInferenceResult());
        InferenceTask originTask = inferenceTaskRepository.findById(inferenceTask.getId()).orElse(null);
        if(originTask!=null){
            if(Constants.INFERENCE_TASK_SUCCESS.equals(inferenceTask.getStatus())){
                originTask.setStatus(Constants.INFERENCE_TASK_SUCCESS);
                originTask.setEndTime(new Date());
                originTask.setInferenceResult(inferenceTask.getInferenceResult());
                inferenceTaskRepository.save(originTask);

                submitMachineAnnotation(inferenceTask.getProjectName(),
                        inferenceTask.getDataSetName(),
                        inferenceTask.getImageIdList(),
                        inferenceTask.getInferenceResult());
            }
        }
    }

    private void submitMachineAnnotation(String projectName, String dataSetName, List<String> imageIdList, JSONObject inferenceObject) {
        MongoCollection<Document> mongoCollection =  mongoTemplate.getCollection(projectName+"_data");
        BasicDBObject query = new BasicDBObject();
        BasicDBList idValues = new BasicDBList();
        idValues.addAll(imageIdList);
        query.put("imageId", new BasicDBObject("$in", idValues));
        query.put("dataSetName",dataSetName);
        try (MongoCursor<Document> cursor = mongoCollection.find(query).iterator()) {
            while (cursor.hasNext()) {
                Document origin = cursor.next();
                String str = origin.toJson();
                // System.out.println(str);
                DataItemEntity dataItemEntity = JSONObject.parseObject(str, DataItemEntity.class);
                dataItemEntity.setId(null);
                if (Constants.IMAGE_STATUS_UNANNOTATED.equals(dataItemEntity.getStatus())) {
                    dataItemEntity.setStatus(Constants.IMAGE_STATUS_MACHINE_ANNOTATED);
                }
                Map<String, List<Annotation>> classToAnnotation = new HashMap<>();
                JSONObject imageResult = inferenceObject.getJSONObject(dataItemEntity.getImageId());
                if (imageResult == null) {
                    continue;
                }
                JSONObject classToAnnResult = imageResult.getJSONObject("annotation");
                if (classToAnnResult == null) {
                    continue;
                }
                Set<String> clsSet = classToAnnResult.keySet();
                clsSet.forEach(cls -> {
                    classToAnnotation.computeIfAbsent(cls, k -> new ArrayList<>());
                    JSONArray clsBoxArray = classToAnnResult.getJSONArray(cls);
                    for (int i = 0; i < clsBoxArray.size(); i++) {
                        JSONObject bbox = clsBoxArray.getJSONObject(i);
                        Annotation annotation = new Annotation();
                        Box box = new Box();
                        box.setX(bbox.getDouble("x"));
                        box.setY(bbox.getDouble("y"));
                        box.setW(bbox.getDouble("w"));
                        box.setH(bbox.getDouble("h"));
                        box.setScore(bbox.getDouble("score"));
                        annotation.setBox(box);
                        annotation.setModelId("baseModel");
                        annotation.setType("modelInference");
                        Classification classification = new Classification();
                        classification.setId(cls);
                        // classification.setValue();
                        annotation.setClassification(classification);
                        classToAnnotation.get(cls).add(annotation);
                    }
                });
                if (dataItemEntity.getAnnotations() == null) {
                    dataItemEntity.setAnnotations(new HashMap<>());
                }
                String timeStamp = Long.toString(new Date().getTime());
                classToAnnotation.forEach((classification, annotationList) -> {
                    dataItemEntity.getAnnotations().computeIfAbsent(classification, k -> new HashMap<String, List<Annotation>>());
                    dataItemEntity.getAnnotations().get(classification).put(timeStamp, annotationList);
                });
                //trick time设置为string
                dataItemEntity.setLastUpdateTime(timeStamp);
                if (dataItemEntity.getUpdateTime() == null) {
                    dataItemEntity.setUpdateTime(new ArrayList<>());
                }
                dataItemEntity.getUpdateTime().add(timeStamp);
                mongoCollection.replaceOne(origin, toDocument(dataItemEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
