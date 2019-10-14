package cn.edu.buaa.act.management.service.impl;

import cn.edu.buaa.act.management.common.ServiceType;
import cn.edu.buaa.act.management.util.ResourceUtils;
import cn.edu.buaa.act.management.entity.ServiceRegistration;
import cn.edu.buaa.act.management.exception.ServiceAlreadyRegisteredException;
import cn.edu.buaa.act.management.repository.RegistrationRepository;
import cn.edu.buaa.act.management.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

/**
 * ServiceRegistryImpl
 *
 * @author wsj
 * @date 2018/9/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ServiceRegistryImpl implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);


    @Autowired
    RegistrationRepository registrationRepository;

    @Override
    public boolean exist(String name, String type) {
        return find(name, type) != null;
    }

    @Override
    public boolean exist(String name, String type, String version) {
        return find(name, type, version) != null;
    }

    @Override
    public Page<ServiceRegistration> findAll(Pageable pageable) {
        return registrationRepository.findAll(pageable);
    }

    @Override
    public ServiceRegistration find(String name, String type) {
        return this.registrationRepository.findServiceRegistrationByNameAndTypeAndDefaultVersionIsTrue(name, type);
    }

    @Override
    public ServiceRegistration find(String name, String type, String version) {
        return this.registrationRepository.findServiceRegistrationByNameAndTypeAndVersion(name, type,version);
    }

    @Override
    public Page<ServiceRegistration> findAllByType(String type, Pageable pageable) {

        return this.registrationRepository.findServiceRegistrationsByType(type,pageable);
    }

    @Override
    public List<ServiceRegistration> findAllByTypeAndName(String type,String name) {
        return this.registrationRepository.findAllByTypeAndName(type,name);
    }

    @Override
    public Page<ServiceRegistration> findAllByName(String name, Pageable pageable) {

        return this.registrationRepository.findServiceRegistrationsByNameIsLike("%"+name+"%",pageable);
    }

    @Override
    public Page<ServiceRegistration> findAllByTypeAndNameIsLike(String  type, String name, Pageable pageable) {

        return this.registrationRepository.findServiceRegistrationsByTypeAndNameIsLike(type,"%"+name+"%",pageable);
    }

    @Override
    public ServiceRegistration save(ServiceRegistration app) {
        //有这个版本就更新
        ServiceRegistration appRegistration = this.registrationRepository.findServiceRegistrationByNameAndTypeAndVersion(app.getName(), app.getType(), app.getVersion());
        if (appRegistration != null) {
            appRegistration.setUri(app.getUri());
            appRegistration.setMetaDataUri(app.getMetaDataUri());
            return this.registrationRepository.save(appRegistration);
        }
        else {
            //如果没有这个service，设置默认版本
            if (find(app.getName(), app.getType()) == null) {
                app.setDefaultVersion(true);
            }
            return this.registrationRepository.save(app);
        }
    }

    @Override
    public ServiceRegistration save(ServiceRegistration app, Boolean force) {
        //有这个版本就更新
        ServiceRegistration appRegistration = this.registrationRepository.findServiceRegistrationByNameAndTypeAndVersion(app.getName(), app.getType(), app.getVersion());
        if (appRegistration != null) {
            if(force){
                // appRegistration.setUri(app.getUri());
                appRegistration.setMetaDataUri(app.getMetaDataUri());
                return this.registrationRepository.save(appRegistration);
            }
            else{
                throw new ServiceAlreadyRegisteredException(appRegistration);
            }
        }
        else {
            //如果没有这个service，设置默认版本
            if (find(app.getName(), app.getType()) == null) {
                app.setDefaultVersion(true);
            }
            return this.registrationRepository.save(app);
        }
    }

    @Override
    public ServiceRegistration save(String name, String type, URI uri, URI metadataUri) {
        return null;
    }

    @Override
    public ServiceRegistration save(String name, String type, String version, URI uri, URI metadataUri) {
        return this.save(new ServiceRegistration(name, type, version, uri, metadataUri));
    }

    @Override
    public void delete(String name, String type, String version) {
        if(version==null){
            this.registrationRepository.deleteServiceRegistrationByNameAndTypeAndDefaultVersionIsTrue(name,type);
        }
        else{
            this.registrationRepository.deleteServiceRegistrationByNameAndTypeAndVersion(name, type, version);
        }
    }

    @Override
    public Resource getServiceResource(ServiceRegistration service) {
        return null;
    }

    @Override
    public Resource getServiceResource(String name) {
        //这里需要修改type
        ServiceRegistration appRegistration = find(name,ServiceType.ALGORITHM);
        return ResourceUtils.getResource(appRegistration.getUri().toString());
    }

    @Override
    public Resource getServiceMetadataResource(ServiceRegistration service) {
        return null;
    }
}
