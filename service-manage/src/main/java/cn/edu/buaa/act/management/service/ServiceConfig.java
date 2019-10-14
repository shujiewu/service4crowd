package cn.edu.buaa.act.management.service;

import cn.edu.buaa.act.common.entity.MicroService;

import java.util.List;

/**
 * ServiceResolve
 *
 * @author wsj
 * @date 2018/10/20
 */
public interface ServiceConfig {
    List<MicroService> getInfo();

    MicroService getConfiguration(String id);

    List<MicroService> getConfigurationByName(String name);

    MicroService save(MicroService service);

    String save(String config, String registerId);
}
