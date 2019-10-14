package cn.edu.buaa.act.mlflow.service.impl;

import cn.edu.buaa.act.mlflow.domain.ExperimentEntity;
import cn.edu.buaa.act.mlflow.domain.RunInfoEntity;
import cn.edu.buaa.act.mlflow.repository.RunInfoRepository;
import cn.edu.buaa.act.mlflow.service.ExperimentService;
import cn.edu.buaa.act.mlflow.service.RunInfoService;
import org.mlflow.tracking.MlflowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.mlflow.api.proto.Service.CreateRun;
import org.mlflow.api.proto.Service.RunInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RunInfoServiceImpl
 *
 * @author wsj
 * @date 2018/10/9
 */
@Service
public class RunInfoServiceImpl implements RunInfoService {
    @Autowired
    private RunInfoRepository runInfoRepository;

    @Autowired
    private ExperimentService experimentService;

    @Autowired
    private MlflowClient mlflowClient;

    @Override
    public Page<RunInfoEntity> findAll(Pageable pageable) {
        return runInfoRepository.findAll(pageable);
    }

    @Override
    public Page<RunInfoEntity> findByNameAndSourceVersion(Pageable pageable, String name, String sourceVersion) {
        return runInfoRepository.findRunInfoEntityByNameIsLikeAndSourceVersionIsLike(pageable,"%"+name+"%","%"+sourceVersion+"%");
    }

    @Override
    public Page<RunInfoEntity> findByName(Pageable pageable, String name) {
        return runInfoRepository.findRunInfoEntityByNameIsLike(pageable,"%"+name+"%");
    }

    @Override
    public Page<RunInfoEntity> findBySourceVersion(Pageable pageable, String sourceVersion) {
        return runInfoRepository.findRunInfoEntityBySourceVersionIsLike(pageable,"%"+sourceVersion+"%");
    }

    @Override
    public Page<RunInfoEntity> findByUserId(String userId, Pageable pageable) {
        return runInfoRepository.findRunInfoEntityByUserId(userId, pageable);
    }

    @Override
    public Page<RunInfoEntity> findByExperimentId(Long experimentId, Pageable pageable) {
        return null;
    }

    @Override
    public List<RunInfoEntity> findByExperimentId(Long experimentId) {
        List<RunInfoEntity> runInfoEntityPage = runInfoRepository.findRunInfoEntityByExperimentId(experimentId);
        List<RunInfo> runInfoList = mlflowClient.listRunInfos(experimentId);
        Map<String,RunInfo> runInfoMap = runInfoList.stream().collect(Collectors.toMap(RunInfo::getRunUuid, a -> a));
        List<RunInfoEntity> runInfoEntityList = runInfoEntityPage.stream().map(runInfoEntity -> {
            RunInfo runInfo;
            if((runInfo = runInfoMap.get(runInfoEntity.getRunUuid()))!=null){
                runInfoMapToEntity(runInfo,runInfoEntity);
                // runInfoEntity= runInfoRepository.save(runInfoEntity);
                runInfoMap.remove(runInfoEntity.getRunUuid());
            }
            return runInfoEntity;
        }).collect(Collectors.toList());

        if(!runInfoMap.isEmpty()){
            List<RunInfoEntity> newRunInfo = new ArrayList<>();
            runInfoMap.forEach((aLong, runInfo) -> {
                RunInfoEntity runInfoEntity = new RunInfoEntity();
                runInfoMapToEntity(runInfo,runInfoEntity);
                newRunInfo.add(runInfoEntity);
            });
            runInfoEntityList.addAll(newRunInfo);
        }
        runInfoRepository.saveAll(runInfoEntityList);
        return runInfoEntityList;
    }


    //没有加用户权限判断
    @Override
    public Page<RunInfoEntity> refresh(Pageable pageable) {
        List<ExperimentEntity> experimentList = experimentService.findAll();
        experimentList.parallelStream().forEach(experiment -> {
            List<RunInfoEntity> runInfoEntityPage = runInfoRepository.findRunInfoEntityByExperimentId(experiment.getExperimentId());
            List<RunInfo> runInfoList = mlflowClient.listRunInfos(experiment.getExperimentId());
            if(runInfoEntityPage!=null&&runInfoList!=null){
                Map<String,RunInfo> runInfoMap = runInfoList.parallelStream().collect(Collectors.toMap(RunInfo::getRunUuid, a -> a));
                List<RunInfoEntity> runInfoEntityList = runInfoEntityPage.parallelStream().map(runInfoEntity -> {
                    RunInfo runInfo;
                    if((runInfo = runInfoMap.get(runInfoEntity.getRunUuid()))!=null){
                        runInfoMapToEntity(runInfo,runInfoEntity);
                        runInfoMap.remove(runInfoEntity.getRunUuid());
                    }
                    return runInfoEntity;
                }).collect(Collectors.toList());
                if(!runInfoMap.isEmpty()){
                    List<RunInfoEntity> newRunInfo = new ArrayList<>();
                    runInfoMap.forEach((aLong, runInfo) -> {
                        RunInfoEntity runInfoEntity = new RunInfoEntity();
                        runInfoMapToEntity(runInfo,runInfoEntity);
                        newRunInfo.add(runInfoEntity);
                    });
                    runInfoEntityList.addAll(newRunInfo);
                }
                runInfoRepository.saveAll(runInfoEntityList);
            }
        });
        return runInfoRepository.findAll(pageable);
    }

    @Override
    public RunInfoEntity findById(Long id) {
        return runInfoRepository.findById(id).orElse(null);
    }

    @Override
    public RunInfoEntity findByRunId(String uuid) {
        return runInfoRepository.findByRunUuid(uuid);
    }


    public void runInfoMapToEntity(RunInfo runInfo, RunInfoEntity runInfoEntity){
        runInfoEntity.setArtifactUri(runInfo.getArtifactUri());
        runInfoEntity.setEntryPointName(runInfo.getEntryPointName());
        runInfoEntity.setExperimentId(runInfo.getExperimentId());
        runInfoEntity.setLifecycleStage(runInfo.getLifecycleStage());
        // runInfoEntity.setName(runInfo.getName());
        // System.out.println(runInfo.getNameBytes().toString());
        runInfoEntity.setRunUuid(runInfo.getRunUuid());
        runInfoEntity.setStatus(runInfo.getStatus().toString());
        runInfoEntity.setSourceName(runInfo.getSourceName());
        runInfoEntity.setSourceType(runInfo.getSourceType().toString());
        runInfoEntity.setSourceVersion(runInfo.getSourceVersion());
        if(runInfo.getEndTime()!=0){
            runInfoEntity.setEndTime(new Date(runInfo.getEndTime()));
        }
        runInfoEntity.setStartTime(new Date(runInfo.getStartTime()));
    }

    @Override
    public RunInfoEntity createRun(CreateRun createRun) {
        RunInfo runInfo = mlflowClient.createRun(createRun);
        RunInfoEntity runInfoEntity = new RunInfoEntity();
        runInfoMapToEntity(runInfo,runInfoEntity);

        return runInfoRepository.save(runInfoEntity);
    }

    @Override
    public RunInfoEntity createRunWithRunName(CreateRun createRun, String runName) {
        RunInfo runInfo = mlflowClient.createRun(createRun);
        RunInfoEntity runInfoEntity = new RunInfoEntity();
        runInfoMapToEntity(runInfo,runInfoEntity);
        runInfoEntity.setName(runName);
        return runInfoRepository.save(runInfoEntity);
    }

    @Override
    public RunInfoEntity terminateRun(String runUuid) {
        mlflowClient.setTerminated(runUuid);
        RunInfoEntity runInfoEntity = runInfoRepository.findByRunUuid(runUuid);
        runInfoEntity.setEndTime(new Date());
        return runInfoRepository.save(runInfoEntity);
    }

    @Override
    public RunInfoEntity update(RunInfoEntity runInfoEntity) {
        return null;
    }

    @Override
    public RunInfoEntity update(String runId) {
        System.out.println(runId);
        RunInfo runInfo = mlflowClient.getRun(runId).getInfo();
        RunInfoEntity runInfoEntity = findByRunId(runId);
        runInfoMapToEntity(runInfo,runInfoEntity);
        return runInfoRepository.save(runInfoEntity);
    }

    @Override
    public RunInfoEntity insert(RunInfoEntity runInfoEntity) {
        return runInfoRepository.save(runInfoEntity);
    }

    @Override
    public RunInfoEntity deleteById(Long id) {
        return null;
    }
}
