package cn.edu.buaa.act.data.service;



import cn.edu.buaa.act.data.entity.TruthInferenceEntity;
import cn.edu.buaa.act.data.repository.TruthInferenceReposiory;
import cn.edu.buaa.act.data.service.api.ITruthInferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruthInferenceServiceImpl implements ITruthInferenceService {

    @Autowired
    TruthInferenceReposiory truthInferenceReposiory;

    @Override
    public void insertTruthInferenceEntity(TruthInferenceEntity truthInferenceEntity) {
        truthInferenceReposiory.save(truthInferenceEntity);
    }

    @Override
    public Page<TruthInferenceEntity> queryPageByUser(String userId, Pageable pageable) {
        Page<TruthInferenceEntity> result= truthInferenceReposiory.findTruthInferenceEntitiesByUserId(userId,pageable);
        return result;
    }

    @Override
    public List<TruthInferenceEntity> queryAllByUser(String userId) {
        return truthInferenceReposiory.findTruthInferenceEntitiesByUserId(userId);
    }

    @Override
    public TruthInferenceEntity queryTruthInferenceEntityById(String id) {
        return truthInferenceReposiory.findById(id).orElse(null);
    }
}
