package cn.edu.buaa.act.data.repository;


import cn.edu.buaa.act.data.entity.ServiceResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wsj
 */
@Repository
public interface ServiceResultReposiory extends MongoRepository<ServiceResultEntity, String> {

}
