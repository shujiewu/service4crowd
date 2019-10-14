package cn.edu.buaa.act.management.service.impl;

import cn.edu.buaa.act.common.entity.AtomicService;
import cn.edu.buaa.act.common.entity.MicroService;
import cn.edu.buaa.act.common.util.ServiceProperty;
import cn.edu.buaa.act.common.util.ServiceResponse;
import cn.edu.buaa.act.management.repository.ServiceConfigReposiory;
import cn.edu.buaa.act.management.service.ServiceConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import cn.edu.buaa.act.management.common.ConfigProperties;

import static cn.edu.buaa.act.management.common.ConfigProperties.METHOD_TYPE_MQ;
import static cn.edu.buaa.act.management.common.ConfigProperties.MQ_CHANNEL;


/**
 * ServiceResolveImpl
 *
 * @author wsj
 * @date 2018/10/20
 */
@Service
public class ServiceConfigImpl implements ServiceConfig {
    @Autowired
    ServiceConfigReposiory serviceConfigReposiory;

    @Override
    public List<MicroService> getInfo(){
        return serviceConfigReposiory.findAll();
    }

    @Override
    public MicroService getConfiguration(String id){
        return serviceConfigReposiory.findById(id).get();
    }

    @Override
    public List<MicroService> getConfigurationByName(String name){
        return serviceConfigReposiory.findMicroServicesByServiceName(name);
    }

    @Override
    public MicroService save(MicroService service){
        return serviceConfigReposiory.save(service);
    }

    @Override
    public String save(String config, String registerId){
        JSONObject jsonConfig = JSONObject.parseObject(config);
        MicroService microService = new MicroService();
        microService.setCreateTime(new Date());
        microService.setServiceName(jsonConfig.getString(ConfigProperties.NAME));
        microService.setServiceType(jsonConfig.getString(ConfigProperties.SERVICE_TYPE));


        JSONArray atomicArray = jsonConfig.getJSONArray(ConfigProperties.ATOMIC_SERVICES);
        List<AtomicService> atomicServices = new ArrayList<>(atomicArray.size());
        for(int i = 0;i<atomicArray.size();i++){
            JSONObject atomicJson = atomicArray.getJSONObject(i);
            AtomicService atomicService = new AtomicService();
            atomicService.setServiceName(atomicJson.getString(ConfigProperties.NAME));
            atomicService.setDescription(atomicJson.getString(ConfigProperties.DESCRIPTION));
            atomicService.setMethod(atomicJson.getString(ConfigProperties.METHOD).toUpperCase());

            if(METHOD_TYPE_MQ.equals(atomicService.getMethod())){
                atomicService.setChannel(atomicJson.getString(MQ_CHANNEL));
                atomicService.setAsync(true);
                atomicService.setAsyncResultUrl(atomicJson.getString(ConfigProperties.ASYNC_RESULT_URL));
                JSONArray asyncResponse = atomicJson.getJSONArray(ConfigProperties.ASYNC_RESPONSE);
                atomicService.setAsyncServiceResponses(decodeResponse(asyncResponse));
                JSONArray messageParam = atomicJson.getJSONArray(ConfigProperties.MQ_BODY);
                atomicService.setMessageBody(decodeProperty(messageParam));
                atomicServices.add(atomicService);
                continue;
            }
            atomicService.setUrl(atomicJson.getString(ConfigProperties.URL));
            atomicService.setAsync(atomicJson.getBoolean(ConfigProperties.ASYNC));
            if(atomicService.getAsync()){
                atomicService.setAsyncResultUrl(atomicJson.getString(ConfigProperties.ASYNC_RESULT_URL));
                JSONArray asyncResponse = atomicJson.getJSONArray(ConfigProperties.ASYNC_RESPONSE);
                atomicService.setAsyncServiceResponses(decodeResponse(asyncResponse));
            }
            JSONArray uriParam = atomicJson.getJSONArray(ConfigProperties.URI_PARAMETERS);
            atomicService.setUriParameters(decodeProperty(uriParam));

            JSONArray body = atomicJson.getJSONArray(ConfigProperties.BODY);
            atomicService.setBody(decodeProperty(body));

            JSONArray queryParam = atomicJson.getJSONArray(ConfigProperties.QUERY_PARAMETERS);
            atomicService.setQueryParameters(decodeProperty(queryParam));

            JSONArray response = atomicJson.getJSONArray(ConfigProperties.RESPONSE);
            atomicService.setServiceResponses(decodeResponse(response));

            atomicServices.add(atomicService);
        }
        microService.setAtomicServiceList(atomicServices);
        microService.setDescription(jsonConfig.getString(ConfigProperties.DESCRIPTION));
        microService.setRegisterId(registerId);

        microService =serviceConfigReposiory.save(microService);
        return microService.getId();
    }

    public List<ServiceResponse> decodeResponse(JSONArray jsonArray){
        if(jsonArray==null) {
            return null;
        }
        List<ServiceResponse> serviceResponses = new ArrayList<>(jsonArray.size());
        for(int i = 0 ;i<jsonArray.size();i++){
            JSONObject property = jsonArray.getJSONObject(i);
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setStatus(property.getInteger(ConfigProperties.STATUS));
            serviceResponse.setDescription(property.getString(ConfigProperties.DESCRIPTION));
            serviceResponse.setBody(decodeProperty(property.getJSONArray(ConfigProperties.PARAMETERS)));
            serviceResponses.add(serviceResponse);
        }
        return serviceResponses;
    }


    public List<ServiceProperty> decodeProperty(JSONArray jsonArray){
        if(jsonArray==null) {
            return null;
        }
        List<ServiceProperty> serviceProperties = new ArrayList<>(jsonArray.size());
        for(int i = 0 ;i<jsonArray.size();i++){
            JSONObject property = jsonArray.getJSONObject(i);
            ServiceProperty serviceProperty = new ServiceProperty();
            serviceProperty.setName(property.getString(ConfigProperties.NAME));
            serviceProperty.setType(property.getString(ConfigProperties.TYPE));
            serviceProperty.setDescription(property.getString(ConfigProperties.DESCRIPTION));
            serviceProperty.setDefaultValue(property.get(ConfigProperties.DEFAULT));
            serviceProperties.add(serviceProperty);
        }
        return serviceProperties;
    }
}
