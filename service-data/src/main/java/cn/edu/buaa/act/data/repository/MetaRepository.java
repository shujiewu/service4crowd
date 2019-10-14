package cn.edu.buaa.act.data.repository;


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
public interface MetaRepository extends MongoRepository<MetaEntity, String> {
    List<MetaEntity> findMetaEntitiesByUserId(String userId);
    List<MetaEntity> findMetaEntitiesByMetaNameAndUserId(String metaName, String userId);
    Page<MetaEntity> findMetaEntitiesByUserId(String userId, Pageable pageable);
    MetaEntity findMetaEntityByMetaNameAndUserId(String metaName, String userId);
}