package cn.edu.buaa.act.management.repository;


import cn.edu.buaa.act.common.entity.Processor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessorConfigRepository extends MongoRepository<Processor, String> {
    Page<Processor> findProcessorByUserId(String userId, Pageable pageable);
    List<Processor> findProcessorByUserId(String userId);
    Processor findProcessorByNameAndVersion(String name, String version);
}
