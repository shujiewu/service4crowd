package cn.edu.buaa.act.data.repository;


import cn.buaa.act.datacore.entity.MachineJobEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineJobRepository extends MongoRepository<MachineJobEntity, String> {
    List<MachineJobEntity> findMachineJobEntitiesByUserId(String userId);
    List<MachineJobEntity> findMachineJobEntitiesByProcessInstanceIdAndActivityId(String processInstanceId, String activityId);
    List<MachineJobEntity> findMachineJobEntitiesByProcessInstanceId(String processInstanceId);
}