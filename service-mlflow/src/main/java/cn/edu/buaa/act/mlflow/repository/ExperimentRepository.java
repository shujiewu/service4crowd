package cn.edu.buaa.act.mlflow.repository;


import cn.edu.buaa.act.mlflow.domain.ExperimentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author wsj
 */
public interface ExperimentRepository extends JpaRepository<ExperimentEntity, Long> {
    /**
     * @param userId
     * @param pageable
     * @return
     */
    Page<ExperimentEntity> findExperimentEntitiesByUserId(String userId, Pageable pageable);

    /**
     * @param name
     * @return
     */
    ExperimentEntity findExperimentEntityByName(String name);


    /**
     * @param experimentId
     * @return
     */
    ExperimentEntity findExperimentEntityByExperimentId(Long experimentId);
}