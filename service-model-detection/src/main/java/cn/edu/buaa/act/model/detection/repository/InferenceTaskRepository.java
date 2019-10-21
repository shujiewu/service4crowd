package cn.edu.buaa.act.model.detection.repository;


import cn.edu.buaa.act.model.detection.entity.InferenceTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wsj
 */
@Repository
public interface InferenceTaskRepository extends MongoRepository<InferenceTask, String> {
    List<InferenceTask> findInferenceTasksByUserId(String userId);
    List<InferenceTask> findInferenceTasksByUserIdAndProjectName(String userId,String projectName);
    List<InferenceTask> findInferenceTasksByUserIdAndProjectNameAndStatus(String userId,String projectName,String status);
    List<InferenceTask> findInferenceTasksByUserIdAndProjectNameAndDataSetNameAndStatus(String userId,String projectName,String dataSetName, String status);
}