package cn.edu.buaa.act.data.repository;


import cn.buaa.act.datacore.entity.MergeResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MergeResultReposiory extends MongoRepository<MergeResultEntity, String> {
}
