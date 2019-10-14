package cn.edu.buaa.act.data.processor.service.api;


import cn.edu.buaa.act.data.entity.ServiceResultEntity;

import java.util.Map;

/**
 * @author wsj
 */
public interface IServiceResult {
    public ServiceResultEntity insertServiceResult(String serviceName, Map<String, Object> result);

    public ServiceResultEntity findById(String id);

}
