package cn.edu.buaa.act.management.repository;


import cn.edu.buaa.act.common.entity.Algorithm;
import cn.edu.buaa.act.common.entity.MicroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlgorithmConfigRepository extends MongoRepository<Algorithm, String> {
    Page<Algorithm> findAlgorithmByUserId(String userId, Pageable pageable);

    List<Algorithm> findAlgorithmByUserId(String userId);

    Algorithm findAlgorithmByNameAndVersion(String name,String version);
}
