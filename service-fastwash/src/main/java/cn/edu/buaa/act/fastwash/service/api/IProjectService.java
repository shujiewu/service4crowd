package cn.edu.buaa.act.fastwash.service.api;


import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import cn.edu.buaa.act.fastwash.data.DataItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProjectService {

    ProjectEntity insertProject(ProjectEntity projectEntity);

    void deleteProject(ProjectEntity projectEntity,boolean deleteData);
    boolean projectExist(String projectName);
    ProjectEntity findProjectEntityByName(String projectName);
    Page<ProjectEntity> findProjects(Pageable pageable);
    Page<ProjectEntity> findAllProjects(Pageable pageable);
    Page<DataItemEntity> findImages(String projectName, Pageable pageable);
    ProjectEntity publishProject(ProjectEntity projectEntity);
    boolean publishProject(String projectName);
}
