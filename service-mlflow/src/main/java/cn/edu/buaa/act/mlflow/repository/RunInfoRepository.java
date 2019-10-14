package cn.edu.buaa.act.mlflow.repository;


import cn.edu.buaa.act.mlflow.domain.ExperimentEntity;
import cn.edu.buaa.act.mlflow.domain.RunInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author wsj
 */
public interface RunInfoRepository extends JpaRepository<RunInfoEntity, Long> {
    /**
     * @param userId
     * @param pageable
     * @return
     */
    Page<RunInfoEntity> findRunInfoEntityByUserId(String userId, Pageable pageable);

    /**
     * @param experimentId
     * @return
     */
    List<RunInfoEntity> findRunInfoEntityByExperimentId(Long experimentId);

    /**
     * @param experimentId
     * @param pageable
     * @return
     */
    Page<RunInfoEntity> findRunInfoEntityByExperimentId(Long experimentId, Pageable pageable);


    Page<RunInfoEntity> findRunInfoEntityByNameIsLikeAndSourceVersionIsLike(Pageable pageable,String name,String sourceVersion);

    Page<RunInfoEntity> findRunInfoEntityByNameIsLike(Pageable pageable,String name);

    Page<RunInfoEntity> findRunInfoEntityBySourceVersionIsLike(Pageable pageable,String sourceVersion);

    /**
     * @param uuid
     * @return
     */
    RunInfoEntity findByRunUuid(String uuid);
}