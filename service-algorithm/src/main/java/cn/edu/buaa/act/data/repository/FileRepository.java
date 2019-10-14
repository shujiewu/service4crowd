package cn.edu.buaa.act.data.repository;


import cn.buaa.act.datacore.entity.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends MongoRepository<FileEntity, String> {
    List<FileEntity> findFileEntitiesByUserId(String userId);
    FileEntity findFileEntityByIdAndUserId(String id, String userId);
}