package cn.edu.buaa.act.data.repository;


import cn.buaa.act.datacore.entity.ServiceResultEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceResultReposiory extends MongoRepository<ServiceResultEntity, String> {

}
