package cn.edu.buaa.act.test.task;

import cn.edu.buaa.act.fastwash.data.Annotation;
import cn.edu.buaa.act.fastwash.data.TaskItemEntity;
import cn.edu.buaa.act.fastwash.data.TrainingItem;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Stats {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<TaskItemEntity> getTaskItem(String projectName) {
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        BasicDBObject query = new BasicDBObject();
        List<TaskItemEntity> taskItemEntities = new ArrayList<>();
        try (MongoCursor<Document> cursor = mongoCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document origin = cursor.next();
                String str = origin.toJson();

                TaskItemEntity taskItemEntity = JSONObject.parseObject(str, TaskItemEntity.class);
                if(taskItemEntity.getIterations()>=2)
                    taskItemEntities.add(taskItemEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskItemEntities;
    }

    public List<TrainingItem> findGroundTruth(String projectName,List<String> imageIds){
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_result");
        BasicDBObject query = new BasicDBObject();
        // query.put("imageId",imageId);
        query.put("imageId", new BasicDBObject("$in", imageIds));
        query.put("type","GROUND_TRUTH");
        MongoCursor<Document> cursor = mongoCollection.find(query).iterator();
        // List<Annotation> annotations = new ArrayList<>();
        List<TrainingItem> trainingItems = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                String str = cursor.next().toJson();
                TrainingItem trainingItem = JSONObject.parseObject(str, TrainingItem.class);
//                trainingItem.getTagList().forEach(tag -> {
//                    Annotation annotation = new Annotation();
//                    annotation.setBox(tag.getBox());
//                    annotation.setClassification(tag.getClassification());
//                    annotations.add(annotation);
//                });
                trainingItems.add(trainingItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return trainingItems;
    }

}
