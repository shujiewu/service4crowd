package cn.edu.buaa.act.data.repository;


import cn.edu.buaa.act.data.entity.MachineTaskEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wsj
 */
@Repository
public interface MachineTaskRepository extends MongoRepository<MachineTaskEntity, String> {
    MachineTaskEntity findMachineTaskEntityByTaskId(String userId);
    List<MachineTaskEntity> findMachineTaskEntitiesByUserId(String userId);
    List<MachineTaskEntity> findMachineTaskEntitiesByProcessInstanceIdAndActivityId(String processInstanceId, String activityId);
    List<MachineTaskEntity> findMachineTaskEntitiesByProcessInstanceId(String processInstanceId);
}