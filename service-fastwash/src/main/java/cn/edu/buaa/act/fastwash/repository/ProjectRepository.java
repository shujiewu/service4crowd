package cn.edu.buaa.act.fastwash.repository;



import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wsj
 */
@Repository
public interface ProjectRepository extends MongoRepository<ProjectEntity, String> {
    List<ProjectEntity> findProjectEntitiesByUserId(String userId);
    Page<ProjectEntity> findProjectEntitiesByUserId(String userId, Pageable pageable);
    ProjectEntity findProjectEntityByNameAndUserId(String name, String userId);
    ProjectEntity findProjectEntityByName(String name);
}