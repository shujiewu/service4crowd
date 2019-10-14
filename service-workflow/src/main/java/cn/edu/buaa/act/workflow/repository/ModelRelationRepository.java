package cn.edu.buaa.act.workflow.repository;



import cn.edu.buaa.act.workflow.domain.ModelRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModelRelationRepository extends JpaRepository<ModelRelation, Long> {
	
	@Query("from ModelRelation mr where mr.parentModelId = :parentModelId")
	List<ModelRelation> findByParentModelId(@Param("parentModelId") String parentModelId);
	
	@Query("from ModelRelation mr where mr.parentModelId = :parentModelId and mr.type = :type")
	List<ModelRelation> findByParentModelIdAndType(@Param("parentModelId") String parentModelId, @Param("type") String type);
	
	@Query("from ModelRelation mr where mr.modelId = :modelId")
	List<ModelRelation> findByChildModelId(@Param("modelId") String modelId);
	
	@Query("from ModelRelation mr where mr.modelId = :modelId and mr.type = :type")
	List<ModelRelation> findByChildModelIdAndType(@Param("modelId") String modelId, @Param("type") String type);

	@Modifying
	@Query("delete from ModelRelation mr where mr.parentModelId = :parentModelId")
	void deleteModelRelationsForParentModel(@Param("parentModelId") String parentModelId);

}
