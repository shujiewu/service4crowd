package cn.edu.buaa.act.fastwash.service;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.util.Query;
import cn.edu.buaa.act.fastwash.common.Constants;
import cn.edu.buaa.act.fastwash.common.MongoUtil;
import cn.edu.buaa.act.fastwash.data.*;
import cn.edu.buaa.act.fastwash.entity.*;
import cn.edu.buaa.act.fastwash.repository.ProjectRepository;
import cn.edu.buaa.act.fastwash.service.api.IAnnotationService;
import cn.edu.buaa.act.fastwash.service.api.IDataSetService;
import cn.edu.buaa.act.fastwash.service.api.IProjectService;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.edu.buaa.act.fastwash.common.MongoUtil.toDocument;
import static cn.edu.buaa.act.fastwash.common.ReadFile.getImageBinary;

@Service
@EnableScheduling
public class AnnotationService implements IAnnotationService {

    @Autowired
    IDataSetService dataSetService;

    @Autowired
    IProjectService projectService;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param dataSetName
     * @param imageId
     * @description 获取图片的数据
     * @return
     */
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
        query.put("type",Constants.TRAINING_ITEM_GROUND_TRUTH);
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
                if(Constants.IMAGE_STATUS_UNANNOTATED.equals(dataItemEntity.getStatus())||Constants.IMAGE_STATUS_MACHINE_ANNOTATED.equals(dataItemEntity.getStatus())){
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


    private ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentLinkedQueue<TaskItemEntity>>> taskQueueMap =  new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,ConcurrentHashMap<String,String>> taskQueueStatus =  new ConcurrentHashMap<>();


//    public void insertCrowdAnnotation(String projectName, CrowdAnnotationTask crowdAnnotationTask){
//        MongoCollection<Document> mongoCollection =  mongoTemplate.getCollection(projectName+"_task");
//        BasicDBObject query = new BasicDBObject();
//        query.put("dataSetName",crowdAnnotationTask.getDetImg().getDataSetName());
//        query.put("imageId",crowdAnnotationTask.getDetImg().getId());
//        query.put("classId",crowdAnnotationTask.getClassId());
//        MongoCursor<Document> cursor = mongoCollection.find(query).iterator();
//        try {
//            if (cursor.hasNext()) {
//                Document origin =  cursor.next();
//                String str = origin.toJson();
//
//                TaskItemEntity taskItemEntity = JSONObject.parseObject(str, TaskItemEntity.class);
//                taskItemEntity.setId(null);
//
//                if(taskItemEntity.getIterations()+1==Constants.ANNOTATION_MAX_PER_CLASS){
//                    taskItemEntity.setStatus(Constants.TASK_STATUS_COMPLETED);
//                } else if(Constants.TASK_STATUS_UNANNOTATED.equals(taskItemEntity.getStatus())||Constants.TASK_STATUS_MACHINE_ANNOTATED.equals(taskItemEntity.getStatus())){
//                    taskItemEntity.setStatus(Constants.TASK_STATUS_CROWD_ANNOTATED);
//                }
//
//                if(taskItemEntity.getAnnotations()==null){
//                    taskItemEntity.setAnnotations(new HashMap<>());
//                }
//                String timeStamp =  Long.toString(new Date().getTime());
//                taskItemEntity.getAnnotations().put(timeStamp,crowdAnnotationTask.getItems());
//
//                //trick time设置为string
//                taskItemEntity.setLastUpdateTime(timeStamp);
//                if(taskItemEntity.getUpdateTime()==null){
//                    taskItemEntity.setUpdateTime(new ArrayList<>());
//                }
//                taskItemEntity.getUpdateTime().add(timeStamp);
//                mongoCollection.replaceOne(origin,toDocument(taskItemEntity));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            cursor.close();
//        }
//    }

    private void resetRuntimeStatus(String projectName){
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        //找到状态不是runtime的且迭代次数不超过最大的
        BasicDBObject query = new BasicDBObject();
        //query.put("iterations", new BasicDBObject("$lte", Constants.ANNOTATION_MAX_PER_CLASS));
        query.put("status", Constants.TASK_STATUS_CROWD_RUNTIME);
        try (MongoCursor<Document> cursor = mongoCollection.find(query).iterator()) {
            while (cursor.hasNext()) {
                Document origin = cursor.next();
                String str = origin.toJson();
                TaskItemEntity taskItemEntity = JSONObject.parseObject(str, TaskItemEntity.class);
                if (taskItemEntity.getWorkerList() != null) {
                    if (taskItemEntity.getWorkerList().size() == 1) {
                        taskItemEntity.setStatus(Constants.TASK_STATUS_MACHINE_ANNOTATED);
                    } else if (taskItemEntity.getWorkerList().size() > 1) {
                        taskItemEntity.setStatus(Constants.TASK_STATUS_CROWD_ANNOTATED);
                    }
                } else {
                    taskItemEntity.setStatus(Constants.TASK_STATUS_UNANNOTATED);
                }
                mongoCollection.updateOne(Filters.eq("_id",new ObjectId(JSONObject.parseObject(taskItemEntity.getId()).get("$oid").toString())), new Document("$set",new Document("status",taskItemEntity.getStatus())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addClassTaskToQueue(String projectName,int targetNum){
        if(taskQueueMap.get(projectName)==null){
            taskQueueMap.put(projectName,new ConcurrentHashMap<>());
        }
        ConcurrentHashMap<String,ConcurrentLinkedQueue<TaskItemEntity>> projectMap = taskQueueMap.get(projectName);

        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        //找到状态不是runtime的且迭代次数不超过最大的
        BasicDBObject query = new BasicDBObject();
        query.put("iterations", new BasicDBObject("$lt", Constants.ANNOTATION_MAX_PER_CLASS));

        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("status", new BasicDBObject("$ne", Constants.TASK_STATUS_CROWD_RUNTIME)));
        values.add(new BasicDBObject("status", new BasicDBObject("$ne", Constants.TASK_STATUS_COMPLETED)));
        query.put("$and", values);
        int count = 0;
        try (MongoCursor<Document> cursor = mongoCollection.find(query).iterator()) {
            while (cursor.hasNext()) {
                Document origin = cursor.next();
                String str = origin.toJson();
                TaskItemEntity taskItemEntity = JSONObject.parseObject(str, TaskItemEntity.class);
                if (projectMap.get(taskItemEntity.getClassId()) == null) {
                    projectMap.put(taskItemEntity.getClassId(), new ConcurrentLinkedQueue<>());
                }
                if (projectMap.get(taskItemEntity.getClassId()).size() < targetNum){
                    projectMap.get(taskItemEntity.getClassId()).offer(taskItemEntity);
                    count++;
                }
                mongoCollection.updateOne(Filters.eq("_id",new ObjectId(JSONObject.parseObject(taskItemEntity.getId()).get("$oid").toString())), new Document("$set",new Document("status",Constants.TASK_STATUS_CROWD_RUNTIME)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Add To Queue Size ="+count);
    }
    private void initQueue(String projectName){
        if(taskQueueMap.get(projectName)==null){
            taskQueueMap.put(projectName,new ConcurrentHashMap<>());
        }
        resetRuntimeStatus(projectName);
        // addToQueue
        addClassTaskToQueue(projectName,Integer.MAX_VALUE);
    }

    @Override
    public CrowdAnnotationTask findLastAnnotationList(String projectName, String classId){
        if(taskQueueMap.get(projectName)==null){
            taskQueueMap.put(projectName,new ConcurrentHashMap<>());
        }
        ConcurrentHashMap<String,ConcurrentLinkedQueue<TaskItemEntity>> projectMap = taskQueueMap.get(projectName);
        ConcurrentLinkedQueue<TaskItemEntity> taskItemEntities = null;

        if(classId.equals("0")){
            String[] keys = projectMap.keySet().toArray(new String[0]);
            Random random = new Random(new Date().getTime());
            String randomKey = keys[random.nextInt(keys.length)];
            taskItemEntities = projectMap.get(randomKey);
        }else{
            taskItemEntities = projectMap.get(classId);
        }
        if(taskItemEntities==null){
            return new CrowdAnnotationTask();
        }
        TaskItemEntity taskItemEntity = taskItemEntities.poll();
        int loopTimes = taskItemEntities.size()+1;
        int iter = 1;

        boolean find = true;
        while (taskItemEntity!=null&&taskItemEntity.getWorkerList()!=null&&iter<=loopTimes){
            int count = 0;
            for(String workerId: taskItemEntity.getWorkerList()){
                if(workerId.equals(BaseContextHandler.getUserID())){
                    count++;
                }
            }
            logger.info("worker count = "+count);
            if(count>=Constants.ANNOTATION_MAX_PER_CLASS_PER_WORKER){
                taskItemEntities.offer(taskItemEntity);
                find = false;
                taskItemEntity = taskItemEntities.poll();
            }else{
                find = true;
                break;
            }
            iter++;
        }

        CrowdAnnotationTask crowdAnnotationTask = new CrowdAnnotationTask();
        if(find&&taskItemEntity!=null){
            crowdAnnotationTask.setId(taskItemEntity.getId());
            crowdAnnotationTask.setDetImg(getImage(taskItemEntity.getDataSetName(),taskItemEntity.getImageId()));
            crowdAnnotationTask.setClassId(taskItemEntity.getClassId());
            crowdAnnotationTask.setCreateTime(Long.toString(new Date().getTime()));
            if(taskItemEntity.getAnnotations()!=null){
                crowdAnnotationTask.setItems(taskItemEntity.getAnnotations().get(taskItemEntity.getLastUpdateTime()));
            }
        }
        return crowdAnnotationTask;
    }

    @Override
    public CrowdAnnotationTask findLastAnnotationList(String projectName, String dataSetName, String imageId, String classId) {
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        BasicDBObject query = new BasicDBObject();
        query.put("dataSetName",dataSetName);
        query.put("imageId",imageId);
        query.put("classId",classId);
        CrowdAnnotationTask crowdAnnotationTask = new CrowdAnnotationTask();
        try (MongoCursor<Document> cursor = mongoCollection.find(query).iterator()) {
            if (cursor.hasNext()) {
                Document origin = cursor.next();
                String str = origin.toJson();

                TaskItemEntity taskItemEntity = JSONObject.parseObject(str, TaskItemEntity.class);

                if(taskItemEntity!=null){
                    crowdAnnotationTask.setId(taskItemEntity.getId());
                    crowdAnnotationTask.setDetImg(getImage(taskItemEntity.getDataSetName(),taskItemEntity.getImageId()));
                    crowdAnnotationTask.setClassId(taskItemEntity.getClassId());
                    if(taskItemEntity.getAnnotations()!=null){
                        crowdAnnotationTask.setItems(taskItemEntity.getAnnotations().get(taskItemEntity.getLastUpdateTime()));
                    }
                }
            }
        }
        return crowdAnnotationTask;
    }

    @Override
    public void submitCrowdTask(String projectName, CrowdAnnotationTask crowdAnnotationTask) {
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        BasicDBObject query = new BasicDBObject();
        try (MongoCursor<Document> cursor = mongoCollection.find(Filters.eq("_id",
                new ObjectId(JSONObject.parseObject(crowdAnnotationTask.getId()).get("$oid").toString()))).iterator()) {
            if (cursor.hasNext()) {
                Document origin = cursor.next();
                String str = origin.toJson();

                TaskItemEntity taskItemEntity = JSONObject.parseObject(str, TaskItemEntity.class);
                String timeStamp =  Long.toString(new Date().getTime());
                taskItemEntity.setLastUpdateTime(timeStamp);
                if(taskItemEntity.getUpdateTime()==null){
                    taskItemEntity.setUpdateTime(new ArrayList<>());
                }
                taskItemEntity.getUpdateTime().add(timeStamp);
                taskItemEntity.setIterations(taskItemEntity.getIterations()+1);

                if(taskItemEntity.getWorkerList()==null){
                    taskItemEntity.setWorkerList(new ArrayList<>());
                }
                taskItemEntity.getWorkerList().add(BaseContextHandler.getUserID());


                if(taskItemEntity.getIterations()>=Constants.ANNOTATION_MAX_PER_CLASS){
                    taskItemEntity.setStatus(Constants.TASK_STATUS_COMPLETED);
                }

                if(taskItemEntity.getAnnotations()==null){
                    taskItemEntity.setAnnotations(new HashMap<>());
                }
                if(crowdAnnotationTask.getItems()==null){
                    crowdAnnotationTask.setItems(new ArrayList<>());
                }
                crowdAnnotationTask.getItems().forEach(item->{
                    item.setWorkerId(BaseContextHandler.getUserID());
                });

                taskItemEntity.getAnnotations().put(timeStamp,crowdAnnotationTask.getItems());
                if(taskItemEntity.getCreateTime()==null){
                    taskItemEntity.setCreateTime(new ArrayList<>());
                }
                taskItemEntity.getCreateTime().add(crowdAnnotationTask.getCreateTime());

                taskItemEntity.setId(null);
                mongoCollection.replaceOne(Filters.eq("_id",
                        new ObjectId(JSONObject.parseObject(crowdAnnotationTask.getId()).get("$oid").toString())),toDocument(taskItemEntity));
                taskItemEntity.setId(crowdAnnotationTask.getId());

                if(taskItemEntity.getIterations()<Constants.ANNOTATION_MAX_PER_CLASS){
                    ConcurrentHashMap<String,ConcurrentLinkedQueue<TaskItemEntity>> projectMap = taskQueueMap.get(projectName);
                    if (projectMap.get(taskItemEntity.getClassId()) == null) {
                        projectMap.put(taskItemEntity.getClassId(), new ConcurrentLinkedQueue<>());
                    }
                    if (projectMap.get(taskItemEntity.getClassId()).size() < Integer.MAX_VALUE)
                        projectMap.get(taskItemEntity.getClassId()).offer(taskItemEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Logger logger = LoggerFactory.getLogger(AnnotationService.class);

    private AtomicBoolean start = new AtomicBoolean(true);
    @Scheduled(fixedDelay = 50000)
    public void addTaskToQueue() {
//        if(start.get()){
//            // 添加到队列
//            // initQueue("test7_5");
//            initQueue("test10_5");
//            start.set(false);
//            return;
//        }
//        taskQueueMap.forEach((projectName,taskQueueMap)-> {
//              addClassTaskToQueue(projectName,Integer.MAX_VALUE);
//        });
    }


    // new API
    public void addToRuntimeProject(String projectName){
        if(taskQueueMap.get(projectName)==null){
            taskQueueMap.put(projectName,new ConcurrentHashMap<>());
        }
        ConcurrentHashMap<String,ConcurrentLinkedQueue<TaskItemEntity>> projectMap = taskQueueMap.get(projectName);
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");

        BasicDBObject query = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("status", new BasicDBObject("$ne", Constants.TASK_STATUS_CROWD_RUNTIME)));
        values.add(new BasicDBObject("status", new BasicDBObject("$ne", Constants.TASK_STATUS_COMPLETED)));
        query.put("$and", values);
        int count = 0;
        try (MongoCursor<Document> cursor = mongoCollection.find(query).iterator()) {
            while (cursor.hasNext()) {
                Document origin = cursor.next();
                String str = origin.toJson();
                TaskItemEntity taskItemEntity = JSONObject.parseObject(str, TaskItemEntity.class);
                if (projectMap.get(taskItemEntity.getClassId()) == null) {
                    projectMap.put(taskItemEntity.getClassId(), new ConcurrentLinkedQueue<>());
                }
                projectMap.get(taskItemEntity.getClassId()).offer(taskItemEntity);
                count++;
                mongoCollection.updateOne(Filters.eq("_id",new ObjectId(JSONObject.parseObject(taskItemEntity.getId()).get("$oid").toString())), new Document("$set",new Document("status",Constants.TASK_STATUS_CROWD_RUNTIME)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        recentCompleteTaskMap.put(projectName,new ConcurrentLinkedQueue<>());
        logger.info("Add To Queue Size ="+count);
    }

    private ConcurrentHashMap<String,ConcurrentLinkedQueue<TaskItemEntity>> recentCompleteTaskMap =  new ConcurrentHashMap<>();

    public List<TaskItemEntity> getRecentCompleteTasks(String projectName){
        List<TaskItemEntity> taskItemEntities = new ArrayList<>();
        if(recentCompleteTaskMap.containsKey(projectName)){
            ConcurrentLinkedQueue<TaskItemEntity> taskQueue = recentCompleteTaskMap.get(projectName);
            recentCompleteTaskMap.put(projectName,new ConcurrentLinkedQueue<>());
            taskItemEntities.addAll(taskQueue);
        }
        return taskItemEntities;
    }

    public void addToRuntimeProject(String projectName,List<String> taskItemEntityIdList) throws Exception {
        if(taskItemEntityIdList==null){
            throw new Exception("taskItemIdList is null");
        }
        logger.info("addToQueue:"+taskItemEntityIdList);
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        BasicDBObject query = new BasicDBObject();
        ConcurrentHashMap<String,ConcurrentLinkedQueue<TaskItemEntity>> projectMap = taskQueueMap.get(projectName);
        taskItemEntityIdList.forEach(taskId->{
            try (MongoCursor<Document> cursor = mongoCollection.find(Filters.eq("_id",
                    new ObjectId(taskId))).iterator()) {
                if (cursor.hasNext()) {
                    Document origin = cursor.next();
                    String str = origin.toJson();
                    TaskItemEntity taskItemEntity = JSONObject.parseObject(str, TaskItemEntity.class);
                    if (projectMap.get(taskItemEntity.getClassId()) == null) {
                        projectMap.put(taskItemEntity.getClassId(), new ConcurrentLinkedQueue<>());
                    }
                    projectMap.get(taskItemEntity.getClassId()).offer(taskItemEntity);
                    mongoCollection.updateOne(Filters.eq("_id",new ObjectId(JSONObject.parseObject(taskItemEntity.getId()).get("$oid").toString())), new Document("$set",new Document("status",Constants.TASK_STATUS_CROWD_RUNTIME)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public long getRemainingTask(String projectName){
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        MongoCollection<Document> groundTruthCollection = mongoTemplate.getCollection(projectName+"_result");
        long remainTask = mongoCollection.count()-groundTruthCollection.count();
        return remainTask;
    }

    public long getTrainingIdList(String projectName){
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        MongoCollection<Document> groundTruthCollection = mongoTemplate.getCollection(projectName+"_result");
        long remainTask = mongoCollection.count()-groundTruthCollection.count();
        return remainTask;
    }


    public long addToTrainingSet(String projectName,List<String> taskItemEntityIdList) throws Exception {
        if(taskItemEntityIdList==null){
            throw new Exception("taskItemIdList is null");
        }
        System.out.println("taskItemIdList");
        System.out.println(taskItemEntityIdList);
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        BasicDBObject query = new BasicDBObject();
        ConcurrentHashMap<String,ConcurrentLinkedQueue<TaskItemEntity>> projectMap = taskQueueMap.get(projectName);

        List<Document> groundTruthItems = new ArrayList<>();
        taskItemEntityIdList.forEach(taskId->{
            try (MongoCursor<Document> cursor = mongoCollection.find(Filters.eq("_id",
                    new ObjectId(taskId))).iterator()) {
                if (cursor.hasNext()) {
                    Document origin = cursor.next();
                    String str = origin.toJson();
                    TaskItemEntity taskItemEntity = JSONObject.parseObject(str, TaskItemEntity.class);

                    TrainingItem trainingItem = new TrainingItem();
                    trainingItem.setDataSetName(taskItemEntity.getDataSetName());
                    trainingItem.setImageId(taskItemEntity.getImageId());
                    //trainingItem.setLastUpdatedTime(new Date());
                    trainingItem.setType(Constants.TRAINING_ITEM_CROWD);

                    List<Annotation> annotationList = taskItemEntity.getAnnotations().get(taskItemEntity.getLastUpdateTime());
                    List<Tag> tagList = new ArrayList<>();
                    annotationList.forEach(annotation ->{
                        Tag tag = new Tag();
                        tag.setBox(annotation.getBox());
                        tag.setClassification(annotation.getClassification());
                        tag.setProperty(annotation.getProperty());
                        tagList.add(tag);
                    });
                    trainingItem.setTagList(tagList);
                    groundTruthItems.add(MongoUtil.toDocument(trainingItem));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        MongoCollection<Document> groundTruthCollection = mongoTemplate.getCollection(projectName+"_result");
        groundTruthCollection.insertMany(groundTruthItems);

        long remainTask = mongoCollection.count()-groundTruthCollection.count();
        return remainTask;
    }

    @Autowired
    ProjectRepository projectRepository;
    public void completeProject(String projectName){
        ProjectEntity projectEntity = projectRepository.findProjectEntityByName(projectName);
        projectEntity.setStatus(Constants.PROJECT_STATUS_COMPLETE);
        projectEntity.setEndTime(new Date());
        taskQueueStatus.remove(projectName);
        recentCompleteTaskMap.remove(projectName);
    }

    @Override
    public void submitCrowdTaskComplete(String projectName, CrowdAnnotationTask crowdAnnotationTask) {
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        BasicDBObject query = new BasicDBObject();
        try (MongoCursor<Document> cursor = mongoCollection.find(Filters.eq("_id",
                new ObjectId(JSONObject.parseObject(crowdAnnotationTask.getId()).get("$oid").toString()))).iterator()) {
            if (cursor.hasNext()) {
                Document origin = cursor.next();
                String str = origin.toJson();

                TaskItemEntity taskItemEntity = JSONObject.parseObject(str, TaskItemEntity.class);
                String timeStamp =  Long.toString(new Date().getTime());
                taskItemEntity.setLastUpdateTime(timeStamp);
                if(taskItemEntity.getUpdateTime()==null){
                    taskItemEntity.setUpdateTime(new ArrayList<>());
                }
                taskItemEntity.getUpdateTime().add(timeStamp);
                taskItemEntity.setIterations(taskItemEntity.getIterations()+1);

                if(taskItemEntity.getWorkerList()==null){
                    taskItemEntity.setWorkerList(new ArrayList<>());
                }
                taskItemEntity.getWorkerList().add(BaseContextHandler.getUserID());


                if(taskItemEntity.getMaxIterationsPerTask()>0){
                    if(taskItemEntity.getIterations()<taskItemEntity.getMaxIterationsPerTask()){
                        taskQueueMap.get(projectName).get(taskItemEntity.getClassId()).offer(taskItemEntity);
                    }else{
                        taskItemEntity.setStatus(Constants.TASK_STATUS_COMPLETED);
                    }
                }else{
                    taskItemEntity.setStatus(Constants.TASK_STATUS_COMPLETED);
                }

                if(taskItemEntity.getAnnotations()==null){
                    taskItemEntity.setAnnotations(new HashMap<>());
                }
                if(crowdAnnotationTask.getItems()==null){
                    crowdAnnotationTask.setItems(new ArrayList<>());
                }
                crowdAnnotationTask.getItems().forEach(item->{
                    item.setWorkerId(BaseContextHandler.getUserID());
                });

                if(taskItemEntity.getChange()==null){
                    taskItemEntity.setChange(new ArrayList<>());
                }
                taskItemEntity.getChange().add(false);
                crowdAnnotationTask.getItems().forEach(annotation -> {
                    if(annotation.getStatus().equals("editAnnotation")||annotation.getStatus().equals("newAnnotation")){
                        taskItemEntity.getChange().set(taskItemEntity.getChange().size()-1,true);
                    }
                });

                taskItemEntity.getAnnotations().put(timeStamp,crowdAnnotationTask.getItems());
                if(taskItemEntity.getCreateTime()==null){
                    taskItemEntity.setCreateTime(new ArrayList<>());
                }
                taskItemEntity.getCreateTime().add(crowdAnnotationTask.getCreateTime());

                taskItemEntity.setId(null);
                mongoCollection.replaceOne(Filters.eq("_id",
                        new ObjectId(JSONObject.parseObject(crowdAnnotationTask.getId()).get("$oid").toString())),toDocument(taskItemEntity));
                taskItemEntity.setId(JSONObject.parseObject(crowdAnnotationTask.getId()).get("$oid").toString());

                //加入最近完成的队列
                recentCompleteTaskMap.get(projectName).offer(taskItemEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CrowdAnnotationTask findLastAnnotationListNew(String projectName, String classId){
        if(taskQueueMap.get(projectName)==null){
            taskQueueMap.put(projectName,new ConcurrentHashMap<>());
        }
        ConcurrentHashMap<String,ConcurrentLinkedQueue<TaskItemEntity>> projectMap = taskQueueMap.get(projectName);
        ConcurrentLinkedQueue<TaskItemEntity> taskItemEntities = null;
        TaskItemEntity taskItemEntity = null;
        boolean find = false;
        if(classId.equals("0")){
            String[] keys = projectMap.keySet().toArray(new String[0]);
            Random random = new Random(new Date().getTime());
            int startPos = random.nextInt(keys.length);
            int classIter = 0;
            while(classIter<keys.length&&startPos<=keys.length){
                classIter++;
                if(startPos==keys.length){
                    startPos=0;
                }
                String randomKey = keys[startPos];
                taskItemEntities = projectMap.get(randomKey);
                Iterator<TaskItemEntity> taskItemEntityIterator = taskItemEntities.iterator();
                TaskItemEntity temp = null;
                while(taskItemEntityIterator.hasNext()){
                    temp = taskItemEntityIterator.next();
                    int count = 0;
                    if(temp.getWorkerList()!=null){
                        for(String workerId: temp.getWorkerList()){
                            if(workerId.equals(BaseContextHandler.getUserID())){
                                count++;
                            }
                        }
                    }
                    if(count>=temp.getMaxWorkerPerTask()){
                        continue;
                    }else{
                        find = true;
                        taskItemEntityIterator.remove();
                        taskItemEntity = temp;
                        break;
                    }
                }
                if(find){
                    break;
                }else{
                    startPos++;
                    continue;
                }
            }
        }else{
            taskItemEntities = projectMap.get(classId);
            if(taskItemEntities==null){
                return new CrowdAnnotationTask();
            }
            Iterator<TaskItemEntity> taskItemEntityIterator = taskItemEntities.iterator();
            TaskItemEntity temp = null;
            while(taskItemEntityIterator.hasNext()){
                temp = taskItemEntityIterator.next();
                int count = 0;
                if(temp.getWorkerList()!=null){
                    for(String workerId: temp.getWorkerList()){
                        if(workerId.equals(BaseContextHandler.getUserID())){
                            count++;
                        }
                    }
                }
                if(count>=temp.getMaxWorkerPerTask()){
                    continue;
                }else{
                    find = true;
                    taskItemEntityIterator.remove();
                    taskItemEntity = temp;
                    break;
                }
            }
        }
        CrowdAnnotationTask crowdAnnotationTask = new CrowdAnnotationTask();
        if(find&&taskItemEntity!=null){
            crowdAnnotationTask.setId(taskItemEntity.getId());
            crowdAnnotationTask.setDetImg(getImage(taskItemEntity.getDataSetName(),taskItemEntity.getImageId()));
            crowdAnnotationTask.setClassId(taskItemEntity.getClassId());
            crowdAnnotationTask.setCreateTime(Long.toString(new Date().getTime()));
            if(taskItemEntity.getAnnotations()!=null){
                crowdAnnotationTask.setItems(taskItemEntity.getAnnotations().get(taskItemEntity.getLastUpdateTime()));
            }
        }
        return crowdAnnotationTask;
    }
}
