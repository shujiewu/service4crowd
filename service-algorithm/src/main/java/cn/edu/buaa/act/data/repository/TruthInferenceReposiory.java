package cn.edu.buaa.act.data.repository;


import cn.edu.buaa.act.data.entity.TruthInferenceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TruthInferenceReposiory extends MongoRepository<TruthInferenceEntity, String> {
        @Query(value = "{ userId: { $in: [ 'ALL', ?0] }}")
        Page<TruthInferenceEntity> findTruthInferenceEntitiesByUserId(String userId, Pageable pageable);

        @Query(value = "{ userId: { $in: [ 'ALL', ?0] }}")
        List<TruthInferenceEntity> findTruthInferenceEntitiesByUserId(String userId);
}
