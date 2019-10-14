package cn.edu.buaa.act.data.repository;


import cn.edu.buaa.act.data.entity.CrowdTaskEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wsj
 */
@Repository
public interface CrowdTaskRepository extends MongoRepository<CrowdTaskEntity, String> {
    List<CrowdTaskEntity> findCrowdTaskEntitiesByUserId(String userId);
    List<CrowdTaskEntity> findCrowdTaskEntitiesByProcessInstanceIdAndActivityId(String processInstanceId, String activityId);
    List<CrowdTaskEntity> findCrowdTaskEntitiesByProcessInstanceId(String processInstanceId);
    CrowdTaskEntity findCrowdTaskEntityByJobId(String jobId);
    CrowdTaskEntity findCrowdTaskEntityByTaskId(String taskId);
}