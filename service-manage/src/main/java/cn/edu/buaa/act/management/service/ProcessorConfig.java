package cn.edu.buaa.act.management.service;

import cn.edu.buaa.act.common.entity.Processor;

import java.util.List;

/**
 * ServiceResolve
 *
 * @author wsj
 * @date 2018/10/20
 */
public interface ProcessorConfig {
    List<Processor> getInfo();
    Processor getConfiguration(String id);
    Processor getProcessor(String name, String version);
    String save(String config, String registerId);
}
