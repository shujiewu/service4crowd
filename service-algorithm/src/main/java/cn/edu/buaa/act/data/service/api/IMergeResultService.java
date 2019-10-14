package cn.edu.buaa.act.data.service.api;


import cn.buaa.act.datacore.entity.MergeResultEntity;

public interface IMergeResultService {

    void insertAnswerEntity(MergeResultEntity mergeResultEntity);

    MergeResultEntity queryMergeResult(String id);
}
