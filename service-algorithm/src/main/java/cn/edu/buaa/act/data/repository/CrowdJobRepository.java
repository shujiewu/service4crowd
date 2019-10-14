package cn.edu.buaa.act.data.repository;


import cn.buaa.act.datacore.entity.CrowdJobEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrowdJobRepository extends MongoRepository<CrowdJobEntity, String> {
    List<CrowdJobEntity> findCrowdJobEntitiesByUserId(String userId);
    List<CrowdJobEntity> findCrowdJobEntitiesByProcessInstanceIdAndActivityId(String processInstanceId, String activityId);
    List<CrowdJobEntity> findCrowdJobEntitiesByProcessInstanceId(String processInstanceId);
    CrowdJobEntity findCrowdJobEntityByJobId(String jobId);
}