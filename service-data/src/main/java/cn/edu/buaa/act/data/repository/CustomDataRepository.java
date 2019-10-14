package cn.edu.buaa.act.data.repository;


import cn.edu.buaa.act.data.entity.CustomDataEntity;
import cn.edu.buaa.act.data.entity.MetaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wsj
 */
@Repository
public interface CustomDataRepository extends MongoRepository<CustomDataEntity, String> {
    List<CustomDataEntity> findCustomDataEntitiesByUserId(String userId);
    List<CustomDataEntity> findCustomDataEntitiesByNameAndUserId(String name, String userId);
    Page<CustomDataEntity> findCustomDataEntitiesByUserId(String userId, Pageable pageable);
    CustomDataEntity findCustomDataEntityByNameAndUserId(String name, String userId);
}