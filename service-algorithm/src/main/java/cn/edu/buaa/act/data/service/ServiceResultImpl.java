package cn.edu.buaa.act.data.service;

import cn.buaa.act.datacore.entity.ServiceResultEntity;
import cn.buaa.act.datacore.repository.ServiceResultReposiory;
import cn.buaa.act.datacore.service.api.IServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ServiceResultImpl implements IServiceResult {

    @Autowired
    ServiceResultReposiory serviceResultReposiory;

    public ServiceResultEntity insertServiceResult(String serviceName,Map<String,Object> result){
        ServiceResultEntity serviceResultEntity = new ServiceResultEntity();
        serviceResultEntity.setServiceName(serviceName);
        serviceResultEntity.setResult(result);
        return serviceResultReposiory.save(serviceResultEntity);
    }

    public ServiceResultEntity findById(String id){
        return serviceResultReposiory.findOne(id);
    }

}
