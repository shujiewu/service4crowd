package cn.edu.buaa.act.management.service.impl;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.entity.Processor;
import cn.edu.buaa.act.common.entity.AtomicService;
import cn.edu.buaa.act.common.entity.MicroService;
import cn.edu.buaa.act.common.util.ServiceResponse;
import cn.edu.buaa.act.management.repository.ProcessorConfigRepository;
import cn.edu.buaa.act.management.service.ProcessorConfig;
import cn.edu.buaa.act.management.service.ServiceConfig;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ProcessorConfigImpl
 *
 * @author wsj
 * @date 2018/10/29
 */
@Service
public class ProcessorConfigImpl implements ProcessorConfig {
    @Autowired
    ProcessorConfigRepository processorConfigRepository;
    @Autowired
    ServiceConfig serviceConfig;
    @Override
    public List<Processor> getInfo() {
        return processorConfigRepository.findAll();
    }

    @Override
    public Processor getConfiguration(String id) {
        return processorConfigRepository.findById(id).get();
    }

    @Override
    public Processor getProcessor(String name,String version) {
        return processorConfigRepository.findProcessorByNameAndVersion(name,version);
    }

    @Override
    public String save(String config, String registerId) {
        Processor processor = JSONObject.parseObject(config,Processor.class);
        processor.setCreateTime(new Date());
        processor.setUserId(BaseContextHandler.getUserID());
        processor = processorConfigRepository.save(processor);

        //同时向算法插入服务
        AtomicService atomicService = new AtomicService();


        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatus(200);
        serviceResponse.setBody(processor.getOutputParameters());
        serviceResponse.setDescription("output");
        List<ServiceResponse> serviceResponses = new ArrayList<>();
        serviceResponses.add(serviceResponse);
        if(processor.getAsync()){
            atomicService.setAsyncServiceResponses(serviceResponses);
            atomicService.setAsync(true);
        }else{
            atomicService.setServiceResponses(serviceResponses);
            atomicService.setAsync(false);
        }
        atomicService.setBody(processor.getInputParameters());
        atomicService.setCreateTime(new Date());
        atomicService.setServiceName(processor.getName()+"-"+processor.getVersion());
        atomicService.setMethod("POST");
        atomicService.setUrl("/processor/"+processor.getName()+"/"+processor.getVersion());
        atomicService.setDescription(processor.getDescription());

        List<MicroService> microServices = serviceConfig.getConfigurationByName("service-data-processor");
        MicroService last = microServices.get(microServices.size()-1);
        last.getAtomicServiceList().add(atomicService);
        serviceConfig.save(last);
        //这里还没有设置异步获取结果的url
        return processor.getId();
    }
}
