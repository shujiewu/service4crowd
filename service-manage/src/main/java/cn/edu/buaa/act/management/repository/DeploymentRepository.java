package cn.edu.buaa.act.management.repository;

import cn.edu.buaa.act.management.entity.ServiceDefinition;
import cn.edu.buaa.act.management.entity.ServiceDeployment;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wsj
 */
@Repository
public interface DeploymentRepository extends JpaRepository<ServiceDeployment, Integer> {

    List<ServiceDeployment> findAllByDefinitionName(String definitionName);
}