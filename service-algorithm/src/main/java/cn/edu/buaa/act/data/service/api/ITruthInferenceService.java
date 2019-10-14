package cn.edu.buaa.act.data.service.api;

import cn.edu.buaa.act.data.entity.TruthInferenceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITruthInferenceService {

    void insertTruthInferenceEntity(TruthInferenceEntity truthInferenceEntity);
    Page<TruthInferenceEntity> queryPageByUser(String userId, Pageable pageable);
    List<TruthInferenceEntity> queryAllByUser(String userId);


    TruthInferenceEntity queryTruthInferenceEntityById(String id);
}
