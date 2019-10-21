package cn.edu.buaa.act.fastwash.service;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.fastwash.common.MongoUtil;
import cn.edu.buaa.act.fastwash.common.Constants;
import cn.edu.buaa.act.fastwash.data.*;
import cn.edu.buaa.act.fastwash.entity.*;
import cn.edu.buaa.act.fastwash.exception.ProjectInvalidException;
import cn.edu.buaa.act.fastwash.repository.ProjectRepository;
import cn.edu.buaa.act.fastwash.service.api.IDataSetService;
import cn.edu.buaa.act.fastwash.service.api.IProjectService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.edu.buaa.act.fastwash.common.Constants.IMAGE_STATUS_UNANNOTATED;
import static cn.edu.buaa.act.fastwash.common.Constants.PROJECT_STATUS_CREATE;

@Service
public class ProjectServiceImpl implements IProjectService{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IDataSetService dataSetService;

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
        //存放groundTruth和汇聚结果
        mongoTemplate.createCollection(projectEntity.getName()+"_result");
        projectEntity.setCreateTime(new Date());
        projectEntity.setStatus(PROJECT_STATUS_CREATE);
        projectEntity = projectRepository.insert(projectEntity);

        if(projectEntity.getId()==null){
            throw new ProjectInvalidException("创建项目失败");
        }
        return projectEntity;
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
    public Page<ProjectEntity> findProjects(Pageable pageable) {
        return projectRepository.findProjectEntitiesByUserId(BaseContextHandler.getUserID(),pageable);
    }

    @Override
    public Page<ProjectEntity> findAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Override
    public Page<DataItemEntity> findImages(String projectName, Pageable pageable) {
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_data");
        MongoCursor<Document> cursor = mongoCollection.find().sort(Sorts.orderBy(Sorts.ascending("_id"))).
                limit(pageable.getPageSize()).skip((pageable.getPageNumber()-1)*pageable.getPageSize()).iterator();
        List<DataItemEntity> dataItemEntities = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                DataItemEntity dataItemEntity = JSONObject.parseObject(cursor.next().toJson(), DataItemEntity.class);
                dataItemEntities.add(dataItemEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        Page<DataItemEntity> dataItemEntityPage = new PageImpl<DataItemEntity>(dataItemEntities,pageable,mongoCollection.count());
        return dataItemEntityPage;
    }

    // Todo: 避免重复提交
    @Override
    public boolean publishProject(String projectName) {
        ProjectEntity projectEntity = projectRepository.findProjectEntityByNameAndUserId(projectName,BaseContextHandler.getUserID());
        if(projectEntity!=null&&Constants.PROJECT_STATUS_CREATE.equals(projectEntity.getStatus())){
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


            if(projectEntity.getImageId()!=null){
                Set<String> dataItemList = new HashSet<>(projectEntity.getImageId());
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
                        trainingItem.setType("GroundTruth");
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
                    trainingItem.setType("GroundTruth");
                    trainingItem.setTagList(groundTruthMap.get(image.getId()));
                    groundTruthItems.add(MongoUtil.toDocument(trainingItem));
                });
            }
            MongoCollection<Document> dataCollection = mongoTemplate.getCollection(projectEntity.getName()+"_data");
            dataCollection.insertMany(dataItemEntities);

            MongoCollection<Document> groundTruthCollection = mongoTemplate.getCollection(projectEntity.getName()+"_result");
            groundTruthCollection.insertMany(groundTruthItems);
            projectEntity.setStatus(Constants.PROJECT_STATUS_PUBLISH);
            projectRepository.save(projectEntity);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public ProjectEntity publishProject(ProjectEntity projectEntity) {
        projectEntity.setUserId(BaseContextHandler.getUserID());
        projectEntity.setStatus(Constants.PROJECT_STATUS_PUBLISH);
        return null;
    }

    @Override
    public void deleteProject(ProjectEntity projectEntity,boolean deleteData) {
        projectEntity.setUserId(BaseContextHandler.getUserID());
        if(deleteData){
            mongoTemplate.dropCollection(projectEntity.getName()+"_data");
        }
        projectRepository.delete(projectEntity);
    }
}

