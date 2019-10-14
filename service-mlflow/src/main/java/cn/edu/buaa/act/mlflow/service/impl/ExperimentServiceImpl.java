package cn.edu.buaa.act.mlflow.service.impl;

import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.mlflow.domain.ExperimentEntity;
import cn.edu.buaa.act.mlflow.exception.ExperimentAlreadyExistedException;
import cn.edu.buaa.act.mlflow.exception.NoExperimentException;
import cn.edu.buaa.act.mlflow.repository.ExperimentRepository;
import cn.edu.buaa.act.mlflow.service.ExperimentService;
import org.mlflow.tracking.MlflowClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.mlflow.api.proto.Service.Experiment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wsj
 */
@Service
public class ExperimentServiceImpl implements ExperimentService {

    private static final Logger logger = LoggerFactory.getLogger(ExperimentServiceImpl.class);

    @Autowired
    ExperimentRepository experimentRepository;

    @Autowired
    private MlflowClient mlflowClient;

    public void experimentMapToEntity(Experiment experiment,ExperimentEntity experimentEntity){
        experimentEntity.setName(experiment.getName());
        experimentEntity.setLastUpdateTime(new Date());
        experimentEntity.setArtifactLocation(experiment.getArtifactLocation());
        experimentEntity.setLifecycleStage(experiment.getLifecycleStage());
        experimentEntity.setExperimentId(experiment.getExperimentId());
        experimentEntity.setUserId("1");
        if(experimentEntity.getCreationTime()==null) {
            experimentEntity.setCreationTime(new Date());
        }
    }

    @Override
    public List<ExperimentEntity> findAll() {
        List<Experiment> experimentList = mlflowClient.listExperiments();
        List<ExperimentEntity> experimentEntityList = experimentRepository.findAll();
        Map<Long, Experiment> experimentMap = experimentList.stream().collect(Collectors.toMap(Experiment::getExperimentId,a -> a));
        System.out.println(experimentMap);
        experimentEntityList = experimentEntityList.stream().map(experimentEntity -> {
            Experiment experiment = experimentMap.get(experimentEntity.getExperimentId());
            if(experiment!=null){
                experimentEntity.setLifecycleStage(experiment.getLifecycleStage());
                experimentEntity.setLastUpdateTime(new Date());
                experimentMap.remove(experimentEntity.getExperimentId());
                return experimentRepository.save(experimentEntity);
            }
            else {
                return experimentEntity;
            }
        }).collect(Collectors.toList());

        if(!experimentMap.isEmpty()){
            List<ExperimentEntity> newExperiment = new ArrayList<>();
            experimentMap.forEach((aLong, experiment) -> {
                ExperimentEntity experimentEntity = new ExperimentEntity();
                experimentMapToEntity(experiment,experimentEntity);
                newExperiment.add(experimentRepository.save(experimentEntity));
            });
            experimentEntityList.addAll(newExperiment);
        }
        return experimentEntityList;
    }

    @Override
    public ExperimentEntity findByExperimentName(String experimentName) {
        org.mlflow.api.proto.Service.Experiment experiment = mlflowClient.getExperimentByName(experimentName).orElse(null);
        ExperimentEntity experimentEntity = experimentRepository.findExperimentEntityByName(experimentName);
        if(experiment!=null&&experimentEntity!=null){
            experimentEntity.setLastUpdateTime(new Date());
            experimentEntity.setName(experiment.getName());
            experimentEntity.setExperimentId(experiment.getExperimentId());
            experimentEntity.setLifecycleStage(experiment.getLifecycleStage());
            experimentEntity.setArtifactLocation(experiment.getArtifactLocation());
            experimentEntity = experimentRepository.save(experimentEntity);
        }
//        else {
//            logger.info(experimentName+"not exists");
//            throw new NoExperimentException(experimentName);
//        }
        return experimentEntity;
    }

    @Override
    public ExperimentEntity findByExperimentId(Long experimentId) {
        //这里还没有写更新
        ExperimentEntity experimentEntity = experimentRepository.findExperimentEntityByExperimentId(experimentId);
        return experimentEntity;
    }
    @Override
    public Page<ExperimentEntity> findByUserId(String userId, Pageable pageable) {
        //这里还没有写更新
        return experimentRepository.findExperimentEntitiesByUserId(userId,pageable);
    }

    @Override
    public Page<ExperimentEntity> findByPage(Pageable pageable) {
        return experimentRepository.findAll(pageable);
    }

    @Override
    public ExperimentEntity insertByExperiment(ExperimentEntity experimentEntity) {
        return experimentRepository.save(experimentEntity);
    }

    @Override
    public ExperimentEntity insertByExperimentName(String experimentName) {
        ExperimentEntity experimentEntity;
        if((experimentEntity=experimentRepository.findExperimentEntityByName(experimentName))!=null){
            logger.info(experimentName+"already exists");
            throw new ExperimentAlreadyExistedException(experimentEntity);
        }
        Long experimentId = mlflowClient.createExperiment(experimentName);
        if(experimentId>=0){
            org.mlflow.api.proto.Service.Experiment experiment = mlflowClient.getExperiment(experimentId).getExperiment();
            experimentEntity = new ExperimentEntity();
            experimentEntity.setExperimentId(experiment.getExperimentId());
            experimentEntity.setLifecycleStage(experiment.getLifecycleStage());
            experimentEntity.setArtifactLocation(experiment.getArtifactLocation());
            experimentEntity.setName(experiment.getName());
            experimentEntity.setUserId("1");
            experimentEntity.setCreationTime(new Date());
            experimentEntity.setLastUpdateTime(new Date());
            experimentRepository.save(experimentEntity);
            return experimentEntity;
        }
        else{
            return null;
        }
    }

    @Override
    public ExperimentEntity update(ExperimentEntity experimentEntity) {
        return null;
    }

    @Override
    public ExperimentEntity delete(Long id) {
        return null;
    }

    @Override
    public ExperimentEntity findById(Long id) {
        return null;
    }

}