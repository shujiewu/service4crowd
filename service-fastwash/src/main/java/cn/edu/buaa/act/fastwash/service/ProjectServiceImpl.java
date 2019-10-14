package cn.edu.buaa.act.fastwash.service;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.fastwash.constant.Constants;
import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import cn.edu.buaa.act.fastwash.exception.ProjectInvalidException;
import cn.edu.buaa.act.fastwash.repository.ProjectRepository;
import cn.edu.buaa.act.fastwash.service.api.IProjectService;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static cn.edu.buaa.act.fastwash.constant.Constants.PROJECT_STATUS_CREATE;

@Service
public class ProjectServiceImpl implements IProjectService{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public ProjectEntity insertProject(ProjectEntity projectEntity) {

        projectEntity.setUserId(BaseContextHandler.getUserID());
        if(projectExist(projectEntity)){
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
    public boolean projectExist(ProjectEntity projectEntity) {
        return projectRepository.findProjectEntityByNameAndUserId(projectEntity.getName(), projectEntity.getUserId()) != null;
    }

    @Override
    public Page<ProjectEntity> findProjects(Pageable pageable) {
        return projectRepository.findProjectEntitiesByUserId(BaseContextHandler.getUserID(),pageable);
    }
}

