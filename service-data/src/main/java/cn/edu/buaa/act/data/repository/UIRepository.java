package cn.edu.buaa.act.data.repository;



import cn.edu.buaa.act.data.entity.UIEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wsj
 */
@Repository
public interface UIRepository extends MongoRepository<UIEntity, String> {
    @Query(fields="{ 'title' : 1, 'description' : 1, 'createTime' : 1, 'lastUpdateTime' : 1}")
    List<UIEntity> findUIEntityByUserId(String userId);
    UIEntity findUIEntityById(String Id);
    List<UIEntity> findUItEntitiesByIdIn(List<String> uiIdList);
}