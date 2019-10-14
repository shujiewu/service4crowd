package cn.edu.buaa.act.data.service;


import cn.edu.buaa.act.data.entity.AggregateResultEntity;

/**
 * @author wsj
 */
public interface IAggregateResultService {
    void insertAggregateResult(AggregateResultEntity aggregateResultEntity);
    AggregateResultEntity queryAggregateResult(String id);
}
