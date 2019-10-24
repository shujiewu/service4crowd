package cn.edu.buaa.act.fastwash.service.api;


import cn.edu.buaa.act.fastwash.data.TaskItemEntity;
import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import cn.edu.buaa.act.fastwash.data.DataItemEntity;
import cn.edu.buaa.act.fastwash.model.PublishRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProjectService {

    ProjectEntity insertProject(ProjectEntity projectEntity);

    void deleteProject(ProjectEntity projectEntity,boolean deleteData);
    boolean projectExist(String projectName);
    ProjectEntity findProjectEntityByName(String projectName);
    Page<ProjectEntity> findAllProjects(Pageable pageable);
    Page<ProjectEntity> findProjects(Pageable pageable);
    Page<ProjectEntity> findProjects(Pageable pageable, String status);
    Page<DataItemEntity> findImages(String projectName, Pageable pageable);
    Page<TaskItemEntity> findTasks(String projectName, Pageable pageable);
    List<String> findImages(String projectName,String dataSetName);
    boolean publishProject(String projectName, String dataSetName, PublishRequest publishRequest);
}
