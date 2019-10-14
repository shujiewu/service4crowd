package cn.edu.buaa.act.management.service;

import cn.edu.buaa.act.management.entity.ServiceDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * @author wsj
 */
public interface ServiceDefine {

    /**
     * @param pageable
     * @param searchName
     * @return
     */
    Page<ServiceDefinition> findDefinitionByNameLike(Pageable pageable, String searchName);

    ServiceDefinition createServiceDefinition(String name, Map<String, String> properties);

    ServiceDefinition createServiceApp(String definitionName, String registeredName, boolean deploy);
    void deleteDefinition(String name);

    ServiceDefinition findDefinitionByName(String name);
}
