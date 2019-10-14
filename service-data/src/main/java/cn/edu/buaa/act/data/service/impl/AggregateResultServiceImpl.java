package cn.edu.buaa.act.data.service.impl;


import cn.edu.buaa.act.data.entity.AggregateResultEntity;
import cn.edu.buaa.act.data.repository.AggregateResultReposiory;
import cn.edu.buaa.act.data.service.IAggregateResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AggregateResultServiceImpl implements IAggregateResultService {

    @Autowired
    private AggregateResultReposiory aggregateResultReposiory;

    @Override
    public void insertAggregateResult(AggregateResultEntity aggregateResultEntity) {
        aggregateResultReposiory.insert(aggregateResultEntity);
    }

    @Override
    public AggregateResultEntity queryAggregateResult(String id)  {
        return aggregateResultReposiory.findById(id).get();
    }
}
