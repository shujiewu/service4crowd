package cn.edu.buaa.act.fastwash.service.api;

import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProjectService {

    ProjectEntity insertProject(ProjectEntity projectEntity);
    ProjectEntity publishProject(ProjectEntity projectEntity);
    void deleteProject(ProjectEntity projectEntity,boolean deleteData);
    boolean projectExist(String projectName);
    Page<ProjectEntity> findProjects(Pageable pageable);



}
