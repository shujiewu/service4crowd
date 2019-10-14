package cn.edu.buaa.act.management.repository;


import cn.edu.buaa.act.common.entity.MicroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceConfigReposiory extends MongoRepository<MicroService, String> {
    Page<MicroService> findMicroServicesByUserId(String userId, Pageable pageable);

    List<MicroService> findMicroServicesByUserId(String userId);

    MicroService findMicroServiceByServiceName(String serviceName);

    List<MicroService> findMicroServicesByServiceName(String serviceName);
}
