package cn.edu.buaa.act.data.service.impl;

import cn.edu.buaa.act.data.entity.ServiceResultEntity;
import cn.edu.buaa.act.data.repository.ServiceResultReposiory;
import cn.edu.buaa.act.data.service.IServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ServiceResultImpl implements IServiceResult {

    @Autowired
    ServiceResultReposiory serviceResultReposiory;

    @Override
    public ServiceResultEntity insertServiceResult(String serviceName, Map<String,Object> result){
        ServiceResultEntity serviceResultEntity = new ServiceResultEntity();
        serviceResultEntity.setServiceName(serviceName);
        serviceResultEntity.setResult(result);
        return serviceResultReposiory.save(serviceResultEntity);
    }

    @Override
    public ServiceResultEntity findById(String id){
        return serviceResultReposiory.findById(id).get();
    }

}
