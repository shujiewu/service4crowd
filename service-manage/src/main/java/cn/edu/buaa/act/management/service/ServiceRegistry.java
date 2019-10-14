package cn.edu.buaa.act.management.service;

import cn.edu.buaa.act.management.common.ServiceType;
import cn.edu.buaa.act.management.entity.ServiceRegistration;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.util.List;

/**
 * @author wsj
 */
public interface ServiceRegistry {
    /**
     * @param name
     * @param type
     * @return
     */
    boolean exist(String name, String type);

    /**
     * @param name
     * @param type
     * @param version
     * @return
     */
    boolean exist(String name, String type,String version);

    /**
     * @return
     */
    Page<ServiceRegistration> findAll(Pageable pageable);

    /**
     * @param name
     * @param type
     * @return
     */
    ServiceRegistration find(String name, String type);

    /**
     * @param name
     * @param type
     * @param version
     * @return
     */
    ServiceRegistration find(String name, String type, String version);


    Page<ServiceRegistration> findAllByType(String type, Pageable pageable);

    List<ServiceRegistration> findAllByTypeAndName(String type, String name);

    Page<ServiceRegistration> findAllByName(String name, Pageable pageable);

    Page<ServiceRegistration> findAllByTypeAndNameIsLike(String type, String filter, Pageable pageable);

    /**
     * @param service
     * @return
     */
    ServiceRegistration save(ServiceRegistration service);

    ServiceRegistration save(ServiceRegistration app, Boolean force);

    ServiceRegistration save(String name, String type, URI uri, URI metadataUri);


    /**
     * @param service
     * @return
     */
    Resource getServiceResource(ServiceRegistration service);


    /**
     * @param name
     * @return
     */
    Resource getServiceResource(String name);

    /**
     * @param service
     * @return
     */
    Resource getServiceMetadataResource(ServiceRegistration service);


    ServiceRegistration save(String name, String type, String version, URI uri, URI metadataUri);

    /**
     * @param name
     * @param type
     * @param version
     */
    public void delete(String name, String type, String version);
}
