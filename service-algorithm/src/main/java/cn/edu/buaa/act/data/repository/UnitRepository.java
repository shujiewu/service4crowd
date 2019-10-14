package cn.edu.buaa.act.data.repository;


import cn.buaa.act.datacore.entity.UnitEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends MongoRepository<UnitEntity, String> {
    List<UnitEntity> findUnitEntityById(String userName);
    List<UnitEntity> findUnitEntitiesByIdIn(List<String> unitIdList);
}