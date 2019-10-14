package cn.edu.buaa.act.data.repository;



import cn.edu.buaa.act.data.entity.AggregateResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wsj
 */
@Repository
public interface AggregateResultReposiory extends MongoRepository<AggregateResultEntity,String> {
}
