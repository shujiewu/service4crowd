package cn.edu.buaa.act.management.controller;

import cn.edu.buaa.act.auth.client.annotation.IgnoreUserToken;
import cn.edu.buaa.act.common.entity.MicroService;
import cn.edu.buaa.act.common.entity.Processor;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.management.entity.ServiceRegistration;
import cn.edu.buaa.act.management.service.AlgorithmConfig;
import cn.edu.buaa.act.management.service.ProcessorConfig;
import cn.edu.buaa.act.management.service.ServiceConfig;
import cn.edu.buaa.act.management.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ServiceConfigController
 *
 * @author wsj
 * @date 2018/10/20
 */
@RestController
@RequestMapping("/service/configuration")
public class ConfigController {
    @Autowired
    ServiceConfig serviceConfig;

    @Autowired
    AlgorithmConfig algorithmConfig;

    @Autowired
    ProcessorConfig processorConfig;

    @Autowired
    ServiceRegistry serviceRegistry;

    @RequestMapping(value = "/info/list", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @IgnoreUserToken
    public TableResultResponse<MicroService> infoList() {
        List<MicroService> microServiceList = serviceConfig.getInfo();
        return new TableResultResponse<>(microServiceList.size(),microServiceList);
    }


    @RequestMapping(value = "/{type}/{name}/{version}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Object infoList(@PathVariable String type, @PathVariable String name, @PathVariable String version) {
        ServiceRegistration serviceRegistration = serviceRegistry.find(name,type,version);
        if(serviceRegistration.getPropertyId()!=null){
            if(type.equals("WEB")){
                return serviceConfig.getConfiguration(serviceRegistration.getPropertyId());
            } else if(type.equals("ALGORITHM")){
                return algorithmConfig.getConfiguration(serviceRegistration.getPropertyId());
            }else if(type.equals("PROCESSOR")) {
                return processorConfig.getConfiguration(serviceRegistration.getPropertyId());
            }else{
                return null;
            }
        }
        else{
            return null;
        }
    }
}
