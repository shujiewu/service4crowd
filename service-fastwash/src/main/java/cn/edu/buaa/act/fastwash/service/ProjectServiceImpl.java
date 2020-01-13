package cn.edu.buaa.act.fastwash.service;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.fastwash.common.MongoUtil;
import cn.edu.buaa.act.fastwash.common.Constants;
import cn.edu.buaa.act.fastwash.data.*;
import cn.edu.buaa.act.fastwash.entity.*;
import cn.edu.buaa.act.fastwash.exception.ProjectInvalidException;
import cn.edu.buaa.act.fastwash.feign.ModelDetectionFeign;
import cn.edu.buaa.act.fastwash.model.PublishRequest;
import cn.edu.buaa.act.fastwash.repository.ProjectRepository;
import cn.edu.buaa.act.fastwash.service.api.IDataSetService;
import cn.edu.buaa.act.fastwash.service.api.IProjectService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static cn.edu.buaa.act.fastwash.common.Constants.IMAGE_STATUS_UNANNOTATED;
import static cn.edu.buaa.act.fastwash.common.Constants.PROJECT_STATUS_CREATE;
import static cn.edu.buaa.act.fastwash.common.MongoUtil.toDocument;

@Service
public class ProjectServiceImpl implements IProjectService{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IDataSetService dataSetService;

    @Autowired
    private ModelDetectionFeign modelDetectionFeign;

    @Override
    public ProjectEntity insertProject(ProjectEntity projectEntity) {
        projectEntity.setUserId(BaseContextHandler.getUserID());
        if(projectRepository.findProjectEntityByName(projectEntity.getName()+"_"+BaseContextHandler.getUserID())!=null){
            throw new ProjectInvalidException("项目名已存在");
        }
        //重新设置项目名
        projectEntity.setName(projectEntity.getName()+"_"+BaseContextHandler.getUserID());
        //存放dataItem
        mongoTemplate.createCollection(projectEntity.getName()+"_data");
        //存放任务
        mongoTemplate.createCollection(projectEntity.getName()+"_task");
        //存放groundTruth和汇聚结果
        mongoTemplate.createCollection(projectEntity.getName()+"_result");

        if(!insertDataAndResultCol(projectEntity)){
            throw new ProjectInvalidException("创建项目失败");
        }

        if(projectEntity.getId()==null){
            throw new ProjectInvalidException("创建项目失败");
        }
        return projectEntity;
    }

    private boolean insertDataAndResultCol(ProjectEntity projectEntity){
        String dataSetName = projectEntity.getDataSetName();
        if(dataSetName==null){
            return false;
        }
        DataSetEntity dataSetEntity = dataSetService.findDataSet(dataSetName);
        List<Document> dataItemEntities = new ArrayList<>();
        List<Document> groundTruthItems = new ArrayList<>();


        Map<String,List<Tag>> groundTruthMap = new HashMap<>();
        Map<String,Category> categoryMap = new HashMap<>();
        dataSetEntity.getCategories().forEach(category -> {
            categoryMap.put(category.getId(),category);
        });
        dataSetEntity.getAnnotations().forEach(annotation->{
            String imageId = annotation.getString("image_id");
            String categoryId = annotation.getString("category_id");
            JSONArray bbox = annotation.getJSONArray("bbox");
            Box box = new Box();
            box.setX(Double.valueOf(bbox.get(0).toString()));
            box.setY(Double.valueOf(bbox.get(1).toString()));
            box.setW(Double.valueOf(bbox.get(2).toString()));
            box.setH(Double.valueOf(bbox.get(3).toString()));
            box.setScore(1.0);
            Classification classification = new Classification();
            classification.setId(categoryId);
            classification.setValue(categoryMap.get(categoryId).getName());
            Tag tag = new Tag();
            tag.setBox(box);
            tag.setClassification(classification);
            groundTruthMap.computeIfAbsent(imageId, k -> new ArrayList<>());
            groundTruthMap.get(imageId).add(tag);
        });

        if(projectEntity.getImageIdList()!=null){
            Set<String> dataItemList = new HashSet<>(projectEntity.getImageIdList());
            dataSetEntity.getImages().forEach(image -> {
                if(dataItemList.contains(image.getId())){
                    DataItemEntity dataItemEntity = new DataItemEntity();
                    dataItemEntity.setDataSetName(dataSetName);
                    dataItemEntity.setImageId(image.getId());
                    dataItemEntity.setStatus(IMAGE_STATUS_UNANNOTATED);
                    dataItemEntity.setFileName(image.getFile_name());
                    dataItemEntity.setHasGroundTruth(true);
                    dataItemEntity.setHeight(image.getHeight());
                    dataItemEntity.setWidth(image.getWidth());
                    dataItemEntities.add(MongoUtil.toDocument(dataItemEntity));

                    TrainingItem trainingItem = new TrainingItem();
                    trainingItem.setDataSetName(dataSetName);
                    trainingItem.setImageId(image.getId());
                    //trainingItem.setLastUpdatedTime(new Date());
                    trainingItem.setType(Constants.TRAINING_ITEM_GROUND_TRUTH);
                    trainingItem.setTagList(groundTruthMap.get(image.getId()));
                    groundTruthItems.add(MongoUtil.toDocument(trainingItem));
                }
            });
        }else{
            dataSetEntity.getImages().forEach(image -> {
                DataItemEntity dataItemEntity = new DataItemEntity();
                dataItemEntity.setDataSetName(dataSetName);
                dataItemEntity.setImageId(image.getId());
                dataItemEntity.setStatus(IMAGE_STATUS_UNANNOTATED);
                dataItemEntity.setFileName(image.getFile_name());
                dataItemEntity.setHasGroundTruth(true);
                dataItemEntity.setHeight(image.getHeight());
                dataItemEntity.setWidth(image.getWidth());
                dataItemEntities.add(MongoUtil.toDocument(dataItemEntity));

                TrainingItem trainingItem = new TrainingItem();
                trainingItem.setDataSetName(dataSetName);
                trainingItem.setImageId(image.getId());
                //trainingItem.setLastUpdatedTime(new Date());
                trainingItem.setType(Constants.TRAINING_ITEM_GROUND_TRUTH);
                trainingItem.setTagList(groundTruthMap.get(image.getId()));
                groundTruthItems.add(MongoUtil.toDocument(trainingItem));
            });
        }
        MongoCollection<Document> dataCollection = mongoTemplate.getCollection(projectEntity.getName()+"_data");
        dataCollection.insertMany(dataItemEntities);

        MongoCollection<Document> groundTruthCollection = mongoTemplate.getCollection(projectEntity.getName()+"_result");
        groundTruthCollection.insertMany(groundTruthItems);

        projectEntity.setCreateTime(new Date());
        projectEntity.setStatus(PROJECT_STATUS_CREATE);
        projectEntity.setTotal(dataItemEntities.size());
        projectEntity.setDone(0);
        projectEntity.setRun(0);
        projectEntity = projectRepository.insert(projectEntity);

        return true;
    }

    @Override
    public boolean projectExist(String projectName) {
        return projectRepository.findProjectEntityByName(projectName+"_"+BaseContextHandler.getUserID()) != null;
    }

    @Override
    public ProjectEntity findProjectEntityByName(String projectName) {
        return projectRepository.findProjectEntityByName(projectName);
    }

    @Override
    public Page<ProjectEntity> findAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectEntity> findProjects(Pageable pageable) {
        return projectRepository.findProjectEntitiesByUserId(BaseContextHandler.getUserID(),pageable);
    }

    @Override
    public Page<ProjectEntity> findProjects(Pageable pageable, String status) {
        return projectRepository.findProjectEntitiesByStatus(pageable,status);
    }

    @Override
    public Page<DataItemEntity> findImages(String projectName, Pageable pageable) {
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_data");
        List<DataItemEntity> dataItemEntities = new ArrayList<>();
        try (MongoCursor<Document> cursor = mongoCollection.find().sort(Sorts.orderBy(Sorts.ascending("_id"))).
                limit(pageable.getPageSize()).skip((pageable.getPageNumber() - 1) * pageable.getPageSize()).iterator()) {
            while (cursor.hasNext()) {
                DataItemEntity dataItemEntity = JSONObject.parseObject(cursor.next().toJson(), DataItemEntity.class);
                dataItemEntities.add(dataItemEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Page<DataItemEntity> dataItemEntityPage = new PageImpl<DataItemEntity>(dataItemEntities,pageable,mongoCollection.count());
        return dataItemEntityPage;
    }

    @Override
    public Page<TaskItemEntity> findTasks(String projectName, Pageable pageable) {
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_task");
        List<TaskItemEntity> taskItemEntities = new ArrayList<>();
        try (MongoCursor<Document> cursor = mongoCollection.find().sort(Sorts.orderBy(Sorts.ascending("_id"))).
                limit(pageable.getPageSize()).skip((pageable.getPageNumber() - 1) * pageable.getPageSize()).iterator()) {
            while (cursor.hasNext()) {
                TaskItemEntity taskItemEntity = JSONObject.parseObject(cursor.next().toJson(), TaskItemEntity.class);
                taskItemEntities.add(taskItemEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Page<TaskItemEntity> taskItemEntityPage = new PageImpl<TaskItemEntity>(taskItemEntities,pageable,mongoCollection.count());
        return taskItemEntityPage;
    }

    @Override
    public List<String> findImages(String projectName, String dataSetName) {
        return null;
    }

    @Override
    public boolean publishProject(String projectName, String dataSetName, PublishRequest publishRequest) {
        ProjectEntity projectEntity = projectRepository.findProjectEntityByName(projectName);
        List<DataItemEntity> dataItemEntities = new LinkedList<>();
        MongoCollection<Document> dataCollection = mongoTemplate.getCollection(projectName+"_data");
        if(Constants.PUBLISH_RANDOM.equals(publishRequest.getStrategy())){
            if(publishRequest.getTotal()>0){
                BasicDBObject query = new BasicDBObject();
                query.put("status", Constants.IMAGE_STATUS_UNANNOTATED);
                try (MongoCursor<Document> cursor = dataCollection.find(query).iterator()) {
                    while (cursor.hasNext()) {
                        Document origin = cursor.next();
                        String str = origin.toJson();
                        DataItemEntity dataItemEntity = JSONObject.parseObject(str, DataItemEntity.class);
                        dataItemEntities.add(dataItemEntity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(publishRequest.getTotal()<dataItemEntities.size()){
                    Random rand=new Random(new Date().getTime());
                    Collections.shuffle(dataItemEntities,rand);
                    int sum = dataItemEntities.size();
                    for(int i = 0;i<sum-publishRequest.getTotal();i++)
                        dataItemEntities.remove(dataItemEntities.size()-1);
                }
            }else{
                return false;
            }
        }
        //TODO: 固定策略发放任务，指定图片ID
        if(Constants.PUBLISH_FIXED.equals(publishRequest.getStrategy())){
            if(publishRequest.getImageIdList()!=null){
            }
        }
        List<String> imageIdList = new ArrayList<>();
        dataItemEntities.forEach(dataItemEntity -> {
            imageIdList.add(dataItemEntity.getImageId());
        });

        MongoCollection<Document> groundTruthCollection = mongoTemplate.getCollection(projectName+"_result");
        BasicDBObject query = new BasicDBObject();
        BasicDBList idValues = new BasicDBList();
        idValues.addAll(imageIdList);
        query.put("imageId", new BasicDBObject("$in", idValues));
        query.put("dataSetName", dataSetName);
        query.put("type", Constants.TRAINING_ITEM_GROUND_TRUTH);

        Map<String,List<Tag>> groundTruthMap = new HashMap<>();

        if(projectEntity.getType().equals("Detection")){
            try (MongoCursor<Document> cursor = groundTruthCollection.find(query).iterator()) {
                while (cursor.hasNext()) {
                    Document origin = cursor.next();
                    String str = origin.toJson();
                    TrainingItem trainingItem = JSONObject.parseObject(str, TrainingItem.class);
                    groundTruthMap.put(trainingItem.getImageId(),trainingItem.getTagList());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<TaskItemEntity> taskItemEntities = new ArrayList<>();
        List<Document> taskItemDocs = new ArrayList<>();
        dataItemEntities.forEach(dataItemEntity -> {
            if(groundTruthMap.containsKey(dataItemEntity.getImageId())){
                Set<String> classIds = new HashSet<>();
                groundTruthMap.get(dataItemEntity.getImageId()).forEach(tag -> {
                    classIds.add(tag.getClassification().getId());
                });
                classIds.forEach(classId ->{
                    TaskItemEntity taskItemEntity = new TaskItemEntity();
                    taskItemEntity.setStatus(Constants.TASK_STATUS_UNANNOTATED);
                    taskItemEntity.setClassId(classId);
                    taskItemEntity.setDataSetName(dataSetName);
                    taskItemEntity.setFileName(dataItemEntity.getFileName());
                    taskItemEntity.setIterations(0);
                    taskItemEntity.setMaxWorkerPerTask(publishRequest.getMaxWorkerPerTask());
                    taskItemEntity.setMaxIterationsPerTask(publishRequest.getMaxIterationsPerTask());
                    taskItemEntity.setImageId(dataItemEntity.getImageId());
                    taskItemEntities.add(taskItemEntity);
                    taskItemDocs.add(toDocument(taskItemEntity));
                });
            }
            else{
                TaskItemEntity taskItemEntity = new TaskItemEntity();
                taskItemEntity.setStatus(Constants.TASK_STATUS_UNANNOTATED);
                taskItemEntity.setClassId("unsure");
                taskItemEntity.setDataSetName(dataSetName);
                taskItemEntity.setFileName(dataItemEntity.getFileName());
                taskItemEntity.setIterations(0);
                taskItemEntity.setMaxWorkerPerTask(publishRequest.getMaxWorkerPerTask());
                taskItemEntity.setMaxIterationsPerTask(publishRequest.getMaxIterationsPerTask());
                taskItemEntity.setImageId(dataItemEntity.getImageId());
                taskItemEntities.add(taskItemEntity);
                taskItemDocs.add(toDocument(taskItemEntity));
            }
            dataCollection.updateOne(Filters.eq("imageId",dataItemEntity.getImageId()), new Document("$set",new Document("status",Constants.IMAGE_STATUS_ANNOTATING)));
        });
        MongoCollection<Document> taskCollection = mongoTemplate.getCollection(projectName+"_task");
        taskCollection.insertMany(taskItemDocs);

        //TODO:将推断结果插入TaskItemEntity中
        if(publishRequest.isInference()){
            ObjectRestResponse restResponse = modelDetectionFeign.inference(projectName,dataSetName,imageIdList);
            if(!restResponse.isSuccess()){
                System.out.println("Model Inference Failed");
            }
        }
        projectEntity.setStatus(Constants.PROJECT_STATUS_PUBLISH);
        projectEntity.setRun(projectEntity.getRun()+dataItemEntities.size());
        projectRepository.save(projectEntity);

        annotationService.addToRuntimeProject(projectEntity.getName());
        //修改状态
//        Bson filter = Filters.eq("age", 1);
//        FindIterable findIterable = collection.find(filter);
//        MongoCursor cursor = findIterable.iterator();
//        while (cursor.hasNext()) {
//            System.out.println(cursor.next());
//        }
//        dataCollection.replaceOne(origin,toDocument(dataItemEntity));
        return true;
    }

    @Override
    public void deleteProject(ProjectEntity projectEntity,boolean deleteData) {
        projectEntity.setUserId(BaseContextHandler.getUserID());
        if(deleteData){
            mongoTemplate.dropCollection(projectEntity.getName()+"_data");
        }
        projectRepository.delete(projectEntity);
    }

    @Override
    public ProjectEntity insertProcessProject(String taskId, ProjectEntity projectEntity) {
        projectEntity.setUserId(BaseContextHandler.getUserID());
        if(projectRepository.findProjectEntityByName(taskId+"_"+BaseContextHandler.getUserID())!=null){
            throw new ProjectInvalidException("项目名已存在");
        }
        //重新设置项目名
        projectEntity.setName(taskId+"_"+BaseContextHandler.getUserID());
        projectEntity.setProperties(new ArrayList<>());
        projectEntity.setMaxWorkerPerTask(projectEntity.getMaxWorkerPerTask());
        projectEntity.setType("Detection");

        //hack设置
        ProjectEntity projectEntity1 = projectRepository.findProjectEntityByName("test10_5");
        projectEntity.setClassification(projectEntity1.getClassification());

        //存放dataItem
        mongoTemplate.createCollection(projectEntity.getName()+"_data");
        //存放任务
        mongoTemplate.createCollection(projectEntity.getName()+"_task");
        //存放groundTruth和汇聚结果
        mongoTemplate.createCollection(projectEntity.getName()+"_result");

        if(!insertDataAndResultCol(projectEntity)){
            throw new ProjectInvalidException("创建项目失败");
        }
        if(projectEntity.getId()==null){
            throw new ProjectInvalidException("创建项目失败");
        }
        try {
            publishProcessProject(projectEntity);
        }catch (Exception e){
            throw new ProjectInvalidException("部署项目失败");
        }
        return projectEntity;
    }
    @Autowired
    AnnotationService annotationService;

    public boolean publishProcessProject(ProjectEntity projectEntity){
        List<DataItemEntity> dataItemEntities = new LinkedList<>();
        MongoCollection<Document> dataCollection = mongoTemplate.getCollection(projectEntity.getName()+"_data");
        BasicDBObject query = new BasicDBObject();
        query.put("status", Constants.IMAGE_STATUS_UNANNOTATED);
        try (MongoCursor<Document> cursor = dataCollection.find(query).iterator()) {
            while (cursor.hasNext()) {
                Document origin = cursor.next();
                String str = origin.toJson();
                DataItemEntity dataItemEntity = JSONObject.parseObject(str, DataItemEntity.class);
                dataItemEntities.add(dataItemEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Map<String,ImageToClass> imageToClassMap = new HashMap<>();
        projectEntity.getImageToClass().forEach(imageToClass -> {
            imageToClassMap.put(imageToClass.getImageId(),imageToClass);
        });

        Map<String,List<Annotation>> imageToAnnotationMap =new HashMap<>();
        projectEntity.getImageToAnnotation().forEach(imageToAnn -> {
            imageToAnnotationMap.put(imageToAnn.getImageId(),imageToAnn.getAnnotationList());
        });

        List<TaskItemEntity> taskItemEntities = new ArrayList<>();
        List<Document> taskItemDocs = new ArrayList<>();
        dataItemEntities.forEach(dataItemEntity -> {
            if(imageToClassMap.containsKey(dataItemEntity.getImageId())){
                Set<String> classIds = new HashSet<>();
                imageToClassMap.get(dataItemEntity.getImageId()).getClassificationList().forEach(tag -> {
                    classIds.add(tag.getId());
                });
                String timeStamp =  Long.toString(new Date().getTime());
                classIds.forEach(classId ->{
                    TaskItemEntity taskItemEntity = new TaskItemEntity();
                    taskItemEntity.setStatus(Constants.TASK_STATUS_UNANNOTATED);
                    taskItemEntity.setClassId(classId);
                    taskItemEntity.setDataSetName(projectEntity.getDataSetName());
                    taskItemEntity.setFileName(dataItemEntity.getFileName());

                    List<Annotation> classAnnotation = new ArrayList<>();
                    if(imageToAnnotationMap.containsKey(dataItemEntity.getImageId())){
                        imageToAnnotationMap.get(dataItemEntity.getImageId()).forEach(annotation -> {
                            if(annotation.getClassification().getId().equals(classId)){
                                classAnnotation.add(annotation);
                            }
                        });
                    }
                    taskItemEntity.setMaxWorkerPerTask(projectEntity.getMaxWorkerPerTask());
                    taskItemEntity.setIterations(1);
                    taskItemEntity.setLastUpdateTime(timeStamp);
                    taskItemEntity.setUpdateTime(new ArrayList<>());
                    taskItemEntity.getUpdateTime().add(timeStamp);

                    taskItemEntity.setWorkerList(new ArrayList<>());
                    taskItemEntity.getWorkerList().add("baseModel");

                    taskItemEntity.setChange(new ArrayList<>());
                    taskItemEntity.getChange().add(false);

                    taskItemEntity.setAnnotations(new HashMap<>());
                    taskItemEntity.getAnnotations().put(timeStamp,classAnnotation);

                    taskItemEntity.setImageId(dataItemEntity.getImageId());
                    taskItemEntities.add(taskItemEntity);
                    taskItemDocs.add(toDocument(taskItemEntity));
                });
            }
            dataCollection.updateOne(Filters.eq("imageId",dataItemEntity.getImageId()), new Document("$set",new Document("status",Constants.IMAGE_STATUS_ANNOTATING)));
        });
        MongoCollection<Document> taskCollection = mongoTemplate.getCollection(projectEntity.getName()+"_task");
        taskCollection.insertMany(taskItemDocs);

        projectEntity.setStatus(Constants.PROJECT_STATUS_PUBLISH);
        projectEntity.setRun(projectEntity.getRun()+dataItemEntities.size());
        projectRepository.save(projectEntity);

        annotationService.addToRuntimeProject(projectEntity.getName());
        return true;
    }
}

