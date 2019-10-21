package cn.edu.buaa.act.model.detection.repository;


import cn.edu.buaa.act.model.detection.entity.TrainingTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wsj
 */
@Repository
public interface TrainingTaskRepository extends MongoRepository<TrainingTask, String> {
    List<TrainingTask> findTrainingTasksByUserId(String userId);
    List<TrainingTask> findTrainingTasksByUserIdAndProjectName(String userId, String projectName);
    List<TrainingTask> findTrainingTasksByUserIdAndProjectNameAndStatus(String userId, String projectName, String status);
    List<TrainingTask> findTrainingTasksByUserIdAndProjectNameAndDataSetNameAndStatus(String userId, String projectName, String dataSetName, String status);
}