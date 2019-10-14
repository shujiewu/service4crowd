package cn.edu.buaa.act.workflow.repository;


import cn.edu.buaa.act.workflow.domain.ModelHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelHistoryRepository extends JpaRepository<ModelHistory, String> {

	List<ModelHistory> findByCreatedByAndModelTypeAndRemovalDateIsNull(String createdBy, Integer modelType);
	
	List<ModelHistory> findByModelIdAndRemovalDateIsNullOrderByVersionDesc(String modelId);
	
	List<ModelHistory> findByModelIdOrderByVersionDesc(Long modelId);

}
