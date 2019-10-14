package cn.edu.buaa.act.mlflow.service;

import cn.edu.buaa.act.mlflow.domain.ExperimentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 *
 * @author wsj
 */
public interface ExperimentService {
    List<ExperimentEntity> findAll();
    ExperimentEntity findByExperimentName(String experimentName);
    ExperimentEntity findByExperimentId(Long experimentId);
    ExperimentEntity insertByExperiment(ExperimentEntity experimentEntity);
    ExperimentEntity insertByExperimentName(String experimentName);


    ExperimentEntity update(ExperimentEntity experimentEntity);
    ExperimentEntity delete(Long id);
    ExperimentEntity findById(Long id);
    Page<ExperimentEntity> findByPage(Pageable pageable);
    Page<ExperimentEntity> findByUserId(String userId, Pageable pageable);
}