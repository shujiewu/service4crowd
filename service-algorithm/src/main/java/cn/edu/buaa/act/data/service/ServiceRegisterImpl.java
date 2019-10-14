package cn.edu.buaa.act.data.service;

import cn.buaa.act.crowd.common.entity.ServiceEntity;
import cn.buaa.act.datacore.repository.ServiceRegisterReposiory;
import cn.buaa.act.datacore.service.api.IServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ServiceRegisterImpl
 *
 * @author wsj
 * @date 2018/6/11
 */
@Service
public class ServiceRegisterImpl implements IServiceRegister {

    @Autowired
    ServiceRegisterReposiory serviceRegisterReposiory;

    @Override
    public ServiceEntity insertServiceEntity(ServiceEntity serviceEntity){
        return serviceRegisterReposiory.save(serviceEntity);
    }

    @Override
    public Page<ServiceEntity> queryPageByUser(String userId, Pageable pageable) {
        Page<ServiceEntity> result= serviceRegisterReposiory.findServiceEntitiesByUserId(userId,pageable);
        return result;
    }

    @Override
    public List<ServiceEntity> queryAllByUser(String userId) {
        return serviceRegisterReposiory.findServiceEntitiesByUserId(userId);
    }

    @Override
    public ServiceEntity queryByServiceId(String serviceId) {
        return serviceRegisterReposiory.findServiceEntityByServiceId(serviceId);
    }

    @Override
    public ServiceEntity queryByServiceTitle(String serviceTitle) {
        return serviceRegisterReposiory.findServiceEntityByTitle(serviceTitle);
    }

    @Override
    public void deleteById(String Id) {
        serviceRegisterReposiory.delete(Id);
    }
}
