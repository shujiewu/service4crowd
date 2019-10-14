package cn.edu.buaa.act.data.repository;


import cn.buaa.act.crowd.common.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRegisterReposiory extends MongoRepository<ServiceEntity, String> {
        Page<ServiceEntity> findServiceEntitiesByUserId(String userId, Pageable pageable);
        List<ServiceEntity> findServiceEntitiesByUserId(String userId);
        ServiceEntity findServiceEntityByServiceId(String serviceId);
        ServiceEntity findServiceEntityByTitle(String serviceTitle);

}
