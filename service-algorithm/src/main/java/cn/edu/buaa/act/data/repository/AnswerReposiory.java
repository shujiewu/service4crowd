package cn.edu.buaa.act.data.repository;


import cn.buaa.act.crowd.common.entity.AnswerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerReposiory extends MongoRepository<AnswerEntity, String> {
        Page<AnswerEntity> findAnswerEntitiesByUserId(String userId, Pageable pageable);

        @Query(fields = "{'createTime' : 1,'name' : 1,'hasGold' : 1}")
        List<AnswerEntity> findAnswerEntitiesByUserId(String userId);

        AnswerEntity findAnswerEntityByName(String name);
}
