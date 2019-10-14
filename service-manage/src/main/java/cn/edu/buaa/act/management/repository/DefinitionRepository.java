package cn.edu.buaa.act.management.repository;

import cn.edu.buaa.act.management.entity.ServiceDefinition;
import cn.edu.buaa.act.management.entity.ServiceRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wsj
 */
@Repository
public interface DefinitionRepository extends JpaRepository<ServiceDefinition, Integer> {
    Page<ServiceDefinition> findServiceDefinitionByNameLike(Pageable pageable, String name);
    ServiceDefinition findServiceDefinitionByName(String name);

    void deleteServiceDefinitionByName(String name);
}