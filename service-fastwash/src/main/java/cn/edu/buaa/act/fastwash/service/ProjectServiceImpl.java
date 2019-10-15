package cn.edu.buaa.act.fastwash.service;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.fastwash.common.MongoUtil;
import cn.edu.buaa.act.fastwash.constant.Constants;
import cn.edu.buaa.act.fastwash.entity.DataItemEntity;
import cn.edu.buaa.act.fastwash.entity.DataSetEntity;
import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import cn.edu.buaa.act.fastwash.exception.ProjectInvalidException;
import cn.edu.buaa.act.fastwash.repository.ProjectRepository;
import cn.edu.buaa.act.fastwash.service.api.IDataSetService;
import cn.edu.buaa.act.fastwash.service.api.IProjectService;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static cn.edu.buaa.act.fastwash.constant.Constants.IMAGE_STATUS_UNANNOTATED;
import static cn.edu.buaa.act.fastwash.constant.Constants.PROJECT_STATUS_CREATE;

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
        if(projectExist(projectEntity.getName())){
            throw new ProjectInvalidException("项目名已存在");
        }
        MongoCollection<Document> mongoCollection =  mongoTemplate.createCollection(projectEntity.getName()+"_data_"+projectEntity.getUserId());

        projectEntity.setCreateTime(new Date());
        projectEntity.setStatus(PROJECT_STATUS_CREATE);
        projectEntity = projectRepository.insert(projectEntity);

        if(projectEntity.getId()==null){
            throw new ProjectInvalidException("创建项目失败");
        }
        return projectEntity;
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
            mongoTemplate.dropCollection(projectEntity.getName()+"_data_"+projectEntity.getUserId());
        }
        projectRepository.delete(projectEntity);
    }

    @Override
    public boolean projectExist(String projectName) {
        return projectRepository.findProjectEntityByNameAndUserId(projectName, BaseContextHandler.getUserID()) != null;
    }

    @Override
    public Page<ProjectEntity> findProjects(Pageable pageable) {
        return projectRepository.findProjectEntitiesByUserId(BaseContextHandler.getUserID(),pageable);
    }

    @Override
    public Page<DataItemEntity> findImages(String projectName, Pageable pageable) {
        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectName+"_data_"+BaseContextHandler.getUserID());
        Sort sort =pageable.getSort();
        //加入查询条件
        // BasicDBObject query = new BasicDBObject();
        // query.append("id","-1");
        MongoCursor<Document> cursor = mongoCollection.find().sort(Sorts.orderBy(Sorts.ascending("id"))).
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
                });
            }
            MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(projectEntity.getName()+"_data_"+projectEntity.getUserId());
            mongoCollection.insertMany(dataItemEntities);
            projectEntity.setStatus(Constants.PROJECT_STATUS_PUBLISH);
            projectRepository.save(projectEntity);
            return true;
        }
        else{
            return false;
        }
    }
}

