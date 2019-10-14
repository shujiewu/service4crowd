package cn.edu.buaa.act.workflow;

import cn.edu.buaa.act.auth.client.config.ClientConfig;
import cn.edu.buaa.act.auth.client.config.UserAuthConfig;
import cn.edu.buaa.act.auth.client.feign.UserAuthFeign;
import cn.edu.buaa.act.common.entity.MicroService;
import cn.edu.buaa.act.common.msg.BaseResponse;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.workflow.common.Constant;
import cn.edu.buaa.act.workflow.feign.IServiceManage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MicroServiceRunner implements CommandLineRunner {

    @Autowired
    IServiceManage iServiceManage;


    @Override
    public void run(String... args) throws Exception {
        log.info("初始化加载微服务");
        try {
            JSONObject jsonResult = iServiceManage.serviceConfigList();
            JSONArray microSeriveArray = jsonResult.getJSONObject("data").getJSONArray("rows");
            for(int i=0;i<microSeriveArray.size();i++){
                MicroService microService = JSONObject.parseObject(microSeriveArray.get(i).toString(),MicroService.class);
                Constant.microServiceMap.put(microService.getServiceName(),microService);
            }
        }catch (IllegalStateException e){

        }
    }

//    @Scheduled()
//    public void run(String... args) throws Exception {
//        log.info("初始化加载微服务");
//        JSONObject jsonResult = iServiceManage.serviceConfigList();
//        JSONArray microSeriveArray = jsonResult.getJSONObject("data").getJSONArray("rows");
//        for(int i=0;i<microSeriveArray.size();i++){
//            MicroService microService = JSONObject.parseObject(microSeriveArray.get(i).toString(),MicroService.class);
//            Constant.microServiceMap.put(microService.getServiceName(),microService);
//        }
//    }
}