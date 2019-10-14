package cn.edu.buaa.act.data.service;

import cn.buaa.act.datacore.entity.MergeResultEntity;
import cn.buaa.act.datacore.repository.MergeResultReposiory;
import cn.buaa.act.datacore.service.api.IMergeResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MergeResultServiceImpl implements IMergeResultService {

    @Autowired
    MergeResultReposiory mergeResultReposiory;
    @Override
    public void insertAnswerEntity(MergeResultEntity mergeResultEntity) {
        mergeResultReposiory.insert(mergeResultEntity);
    }
    @Override
    public MergeResultEntity queryMergeResult(String id) {
        return mergeResultReposiory.findOne(id);
    }
}
