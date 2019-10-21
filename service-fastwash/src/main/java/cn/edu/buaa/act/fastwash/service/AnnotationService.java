package cn.edu.buaa.act.fastwash.service;

import cn.edu.buaa.act.fastwash.common.Constants;
import cn.edu.buaa.act.fastwash.data.Annotation;
import cn.edu.buaa.act.fastwash.data.DataItemEntity;
import cn.edu.buaa.act.fastwash.data.TrainingItem;
import cn.edu.buaa.act.fastwash.entity.*;
import cn.edu.buaa.act.fastwash.service.api.IAnnotationService;
import cn.edu.buaa.act.fastwash.service.api.IDataSetService;
import cn.edu.buaa.act.fastwash.service.api.IProjectService;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.edu.buaa.act.fastwash.common.MongoUtil.toDocument;
import static cn.edu.buaa.act.fastwash.common.ReadFile.getImageBinary;

@Service
public class AnnotationService implements IAnnotationService {

    @Autowired
    IDataSetService dataSetService;

    @Autowired
    IProjectService projectService;

    @Autowired
    private MongoTemplate mongoTemplate;


    private Image getImage(String dataSetName,String imageId){
        Image image = dataSetService.findImage(dataSetName,imageId);
        image.setBlob(getImageBinary("D:\\data\\VOC2007\\JPEGImages\\"+image.getFile_name(),"JPG"));
        return image;
    }

    @Override
    public CrowdAnnotationTask findGroundTruthList(String projectName, String dataSetName, String imageId) {
        CrowdAnnotationTask crowdAnnotationTask = new CrowdAnnotationTask();

        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_result");
        BasicDBObject query = new BasicDBObject();
        query.put("imageId",imageId);
        query.put("type","GroundTruth");
        MongoCursor<Document> cursor = mongoCollection.find(query).iterator();
        List<Annotation> annotations = new ArrayList<>();
        try {
            if (cursor.hasNext()) {
                String str = cursor.next().toJson();
                TrainingItem trainingItem = JSONObject.parseObject(str, TrainingItem.class);
                if(!trainingItem.getImageId().equals(imageId)){
                    throw new Exception();
                }
                trainingItem.getTagList().forEach(tag -> {
                    Annotation annotation = new Annotation();
                    annotation.setBox(tag.getBox());
                    annotation.setClassification(tag.getClassification());
                    annotations.add(annotation);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        crowdAnnotationTask.setDetImg(getImage(dataSetName,imageId));
        crowdAnnotationTask.setItems(annotations);
        return crowdAnnotationTask;
    }

    // Todo：并发修改可能会丢失，从逻辑上让它串行执行
    // TODO: 插入之前判断lastUpdate
    @Override
    public void submitCrowdAnnotation(String projectName, CrowdAnnotationTask crowdAnnotationTask) {
        MongoCollection<Document> mongoCollection =  mongoTemplate.getCollection(projectName+"_data");
        BasicDBObject query = new BasicDBObject();
        query.put("dataSetName",crowdAnnotationTask.getDetImg().getDataSetName());
        query.put("imageId",crowdAnnotationTask.getDetImg().getId());
        MongoCursor<Document> cursor = mongoCollection.find(query).iterator();
        try {
            if (cursor.hasNext()) {
                Document origin =  cursor.next();
                String str = origin.toJson();
                // System.out.println(str);
                DataItemEntity dataItemEntity = JSONObject.parseObject(str, DataItemEntity.class);
                dataItemEntity.setId(null);
                if(Constants.IMAGE_STATUS_UNANNOTATED.equals(dataItemEntity.getStatus())){
                    dataItemEntity.setStatus(Constants.IMAGE_STATUS_CROWD_ANNOTATED);
                }
                Map<String,List<Annotation>> classToAnnotation = new HashMap<>();
                crowdAnnotationTask.getItems().forEach(annotation -> {
                    classToAnnotation.computeIfAbsent(annotation.getClassification().getId(),k -> new ArrayList<>());
                    classToAnnotation.get(annotation.getClassification().getId()).add(annotation);
                });

                if(dataItemEntity.getAnnotations()==null){
                    dataItemEntity.setAnnotations(new HashMap<>());
                }
                String timeStamp =  Long.toString(new Date().getTime());
                classToAnnotation.forEach((classification,annotationList)->{
                    dataItemEntity.getAnnotations().computeIfAbsent(classification, k -> new HashMap<String,List<Annotation>>());
                    dataItemEntity.getAnnotations().get(classification).put(timeStamp,annotationList);
                });
                //trick time设置为string
                dataItemEntity.setLastUpdateTime(timeStamp);
                if(dataItemEntity.getUpdateTime()==null){
                    dataItemEntity.setUpdateTime(new ArrayList<>());
                }
                dataItemEntity.getUpdateTime().add(timeStamp);
                mongoCollection.replaceOne(origin,toDocument(dataItemEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    @Override
    public CrowdAnnotationTask findLastAnnotationList(String projectName, String dataSetName, String imageId) {
        MongoCollection<Document> mongoCollection =  mongoTemplate.getCollection(projectName+"_data");
        CrowdAnnotationTask crowdAnnotationTask = new CrowdAnnotationTask();
        crowdAnnotationTask.setDetImg(getImage(dataSetName,imageId));

        BasicDBObject query = new BasicDBObject();
        query.put("dataSetName",dataSetName);
        query.put("imageId",imageId);
        MongoCursor<Document> cursor = mongoCollection.find(query).iterator();
        try {
            if (cursor.hasNext()) {
                Document origin =  cursor.next();
                String str = origin.toJson();
                DataItemEntity dataItemEntity = JSONObject.parseObject(str, DataItemEntity.class);

                if(Constants.IMAGE_STATUS_UNANNOTATED.equals(dataItemEntity.getStatus())){
                    // dataItemEntity.setStatus(Constants.IMAGE_STATUS_CROWD_ANNOTATED);
                    crowdAnnotationTask.setItems(new ArrayList<>());
                    return crowdAnnotationTask;
                } else{
                    if(dataItemEntity.getAnnotations()==null){
                        crowdAnnotationTask.setItems(new ArrayList<>());
                        return crowdAnnotationTask;
                    }else{
                        List<Annotation> annotationResult = new ArrayList<>();
                        String timeStamp = dataItemEntity.getLastUpdateTime();
                        dataItemEntity.getAnnotations().forEach((classification,annotationMap)->{
                            if(annotationMap.get(timeStamp)!=null)
                                annotationResult.addAll(annotationMap.get(timeStamp));
                        });
//                        dataItemEntity.getAnnotations().forEach((classification,annotationMap)->{
//                            TreeMap<String, List<Annotation>> sortMap = new TreeMap<String, List<Annotation>>(new Comparator<String>() {
//                                @Override
//                                public int compare(String o1, String o2) {
//                                    return (int)(Long.parseLong(o2)-Long.parseLong(o1));
//                                }
//                            });
//                            sortMap.putAll(annotationMap);
//                            List<Annotation> annotationList = sortMap.firstEntry().getValue();
//                            annotationResult.addAll(annotationList);
//                        });
                        crowdAnnotationTask.setItems(annotationResult);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return crowdAnnotationTask;
    }
}
