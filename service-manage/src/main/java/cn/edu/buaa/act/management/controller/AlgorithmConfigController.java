package cn.edu.buaa.act.management.controller;

import cn.edu.buaa.act.common.entity.MicroService;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.management.entity.ServiceRegistration;
import cn.edu.buaa.act.management.service.ServiceConfig;
import cn.edu.buaa.act.management.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ServiceConfigController
 *
 * @author wsj
 * @date 2018/10/20
 */
//@RestController
//@RequestMapping("/service/algorithm/configuration")
//public class AlgorithmConfigController {
//    @Autowired
//    ServiceConfig serviceConfig;
//
//    @Autowired
//    ServiceRegistry serviceRegistry;
//
//    @RequestMapping(value = "/info/list", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.OK)
//    public TableResultResponse<MicroService> infoList() {
//        List<MicroService> microServiceList = serviceConfig.getInfo();
//        return new TableResultResponse<>(microServiceList.size(),microServiceList);
//    }
//
//
//    @RequestMapping(value = "/{type}/{name}/{version}", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.OK)
//    public MicroService infoList(@PathVariable String type, @PathVariable String name, @PathVariable String version) {
//        ServiceRegistration serviceRegistration = serviceRegistry.find(name,type,version);
//        if(serviceRegistration.getPropertyId()!=null){
//            return serviceConfig.getConfiguration(serviceRegistration.getPropertyId());
//        }
//        else{
//            return new MicroService();
//        }
//    }
//}
