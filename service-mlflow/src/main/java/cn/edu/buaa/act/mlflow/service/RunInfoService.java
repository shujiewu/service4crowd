package cn.edu.buaa.act.mlflow.service;

import cn.edu.buaa.act.mlflow.domain.RunInfoEntity;
import org.mlflow.api.proto.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 *
 * @author wsj
 */
public interface RunInfoService {
    Page<RunInfoEntity> findAll(Pageable pageable);

    Page<RunInfoEntity> findByNameAndSourceVersion(Pageable pageable, String name, String sourceVersion);

    Page<RunInfoEntity> findByName(Pageable pageable, String name);

    Page<RunInfoEntity> findBySourceVersion(Pageable pageable, String sourceVersion);

    Page<RunInfoEntity> findByUserId(String userId, Pageable pageable);
    Page<RunInfoEntity> findByExperimentId(Long experimentId, Pageable pageable);

    List<RunInfoEntity> findByExperimentId(Long experimentId);

    //没有加用户权限判断
    Page<RunInfoEntity> refresh(Pageable pageable);

    RunInfoEntity findById(Long id);
    RunInfoEntity findByRunId(String uuid);

    RunInfoEntity createRun(Service.CreateRun createRun);

    RunInfoEntity createRunWithRunName(Service.CreateRun createRun, String runName);

    RunInfoEntity terminateRun(String runUuid);

    RunInfoEntity update(RunInfoEntity runInfoEntity);

    RunInfoEntity update(String runId);

    RunInfoEntity insert(RunInfoEntity runInfoEntity);
    RunInfoEntity deleteById(Long id);
}