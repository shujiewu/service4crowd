package cn.edu.buaa.act.management.service;

import cn.edu.buaa.act.common.entity.Algorithm;
import cn.edu.buaa.act.common.entity.MicroService;

import java.util.List;

/**
 * ServiceResolve
 *
 * @author wsj
 * @date 2018/10/20
 */
public interface AlgorithmConfig {
    List<Algorithm> getInfo();
    Algorithm getConfiguration(String id);

    Algorithm getAlgorithm(String name, String version);

    String save(String config, String registerId);
}
