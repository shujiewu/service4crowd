package cn.edu.buaa.act.management.service.impl;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.entity.Algorithm;
import cn.edu.buaa.act.common.entity.AtomicService;
import cn.edu.buaa.act.common.entity.MicroService;
import cn.edu.buaa.act.common.util.ServiceResponse;
import cn.edu.buaa.act.management.repository.AlgorithmConfigRepository;
import cn.edu.buaa.act.management.service.AlgorithmConfig;
import cn.edu.buaa.act.management.service.ServiceConfig;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AlgorithmConfigImpl
 *
 * @author wsj
 * @date 2018/10/29
 */
@Service
public class AlgorithmConfigImpl implements AlgorithmConfig {
    @Autowired
    AlgorithmConfigRepository algorithmConfigRepository;



    @Autowired
    ServiceConfig serviceConfig;

    @Override
    public List<Algorithm> getInfo() {
        return algorithmConfigRepository.findAll();
    }

    @Override
    public Algorithm getConfiguration(String id) {
        return algorithmConfigRepository.findById(id).get();
    }

    @Override
    public Algorithm getAlgorithm(String name,String version) {
        return algorithmConfigRepository.findAlgorithmByNameAndVersion(name,version);
    }

    @Override
    public String save(String config, String registerId) {
        Algorithm algorithm = JSONObject.parseObject(config,Algorithm.class);
        algorithm.setCreateTime(new Date());
        algorithm.setUserId(BaseContextHandler.getUserID());
        algorithm = algorithmConfigRepository.save(algorithm);

        //同时向算法插入服务
        AtomicService atomicService = new AtomicService();


        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatus(200);
        serviceResponse.setBody(algorithm.getOutputParameters());
        serviceResponse.setDescription("output");
        List<ServiceResponse> serviceResponses = new ArrayList<>();
        serviceResponses.add(serviceResponse);
        if(algorithm.getAsync()){
            atomicService.setAsyncServiceResponses(serviceResponses);
            atomicService.setAsync(true);
        }else{
            atomicService.setServiceResponses(serviceResponses);
            atomicService.setAsync(false);
        }
        atomicService.setBody(algorithm.getInputParameters());
        atomicService.setCreateTime(new Date());
        atomicService.setServiceName(algorithm.getName()+"-"+algorithm.getVersion());
        atomicService.setMethod("POST");
        atomicService.setUrl("/algorithm/"+algorithm.getName()+"/"+algorithm.getVersion());
        atomicService.setDescription(algorithm.getDescription());
        List<MicroService> microServices = serviceConfig.getConfigurationByName("service-mlflow");
        MicroService last = microServices.get(microServices.size()-1);
        last.getAtomicServiceList().add(atomicService);
        serviceConfig.save(last);
        //这里还没有设置异步获取结果的url
        return algorithm.getId();
    }
}
