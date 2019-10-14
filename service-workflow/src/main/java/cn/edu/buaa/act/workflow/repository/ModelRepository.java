package cn.edu.buaa.act.workflow.repository;


import cn.edu.buaa.act.workflow.domain.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {

  @Query("from Model as model where model.createdBy = :user and model.modelType = :modelType")
  List<Model> findModelsCreatedBy(@Param("user") String createdBy, @Param("modelType") Integer modelType, Sort sort);

  @Query("from Model as model where model.createdBy = :user and model.modelType = :modelType")
  Page<Model> findPageModelsCreatedBy(@Param("user") String createdBy, @Param("modelType") Integer modelType, Pageable pageable);

  @Query("from Model as model where model.createdBy = :user and model.status = :status and model.modelType = :modelType")
  Page<Model> findPageModelsCreatedByStatus(@Param("user") String createdBy, @Param("modelType") Integer modelType, @Param("status") String status, Pageable pageable);

  @Query("from Model as model where model.createdBy = :user and "
      + "(lower(model.name) like :filter or lower(model.description) like :filter) and model.modelType = :modelType")
  List<Model> findModelsCreatedBy(@Param("user") String createdBy, @Param("modelType") Integer modelType, @Param("filter") String filter, Sort sort);

  @Query("from Model as model where model.createdBy = :user and "
          + "(lower(model.name) like :filterName or lower(model.key) like :filterKey) and model.modelType = :modelType")
  Page<Model> findPageModelsCreatedBy(@Param("user") String createdBy, @Param("modelType") Integer modelType, @Param("filterName") String filterName, @Param("filterKey") String filterKey, Pageable pageable);

  @Query("from Model as model where model.createdBy = :user and "
          + "(lower(model.name) like :filterName or lower(model.key) like :filterKey) and model.status = :status and model.modelType = :modelType")
  Page<Model> findPageModelsCreatedByStatus(@Param("user") String createdBy, @Param("modelType") Integer modelType, @Param("status") String status, @Param("filterName") String filterName, @Param("filterKey") String filterKey, Pageable pageable);


  @Query("from Model as model where model.key = :key and model.modelType = :modelType")
  List<Model> findModelsByKeyAndType(@Param("key") String key, @Param("modelType") Integer modelType);

  @Query("from Model as model where (lower(model.name) like :filter or lower(model.description) like :filter) " + "and model.modelType = :modelType")
  List<Model> findModelsByModelType(@Param("modelType") Integer modelType, @Param("filter") String filter);

  @Query("from Model as model where model.modelType = :modelType")
  List<Model> findModelsByModelType(@Param("modelType") Integer modelType);

  @Query("from Model as model where model.processInstanceID = :processInstanceID")
  Model findModelByProcessInstanceID(@Param("processInstanceID") String processInstanceID);

  @Query("select count(m.id) from Model m where m.createdBy = :user and m.modelType = :modelType")
  Long countByModelTypeAndUser(@Param("modelType") int modelType, @Param("user") String user);

//  @Query("select m from ModelRelation mr inner join mr.model m where mr.parentModelId = :parentModelId")
//  List<Model> findModelsByParentModelId(@Param("parentModelId") String parentModelId);
//
//  @Query("select m from ModelRelation mr inner join mr.model m where mr.parentModelId = :parentModelId and m.modelType = :modelType")
//  List<Model> findModelsByParentModelIdAndType(@Param("parentModelId") String parentModelId, @Param("modelType") Integer modelType);
//
//  @Query("select m.id, m.name, m.modelType from ModelRelation mr inner join mr.parentModel m where mr.modelId = :modelId")
//  List<Model> findModelsByChildModelId(@Param("modelId") String modelId);

  @Query("select model.key from Model as model where model.id = :modelId and model.createdBy = :user")
  String appDefinitionIdByModelAndUser(@Param("modelId") String modelId, @Param("user") String user);

  @Query("select count(m.id) from Model m where m.createdBy = :user and m.status = :status")
  Long countByModelStatusAndUser(@Param("status") String status, @Param("user") String user);

  @Query("select count(m.id) from Model m where m.createdBy = :user and m.modelType = :modelType")
  Long countByUser(@Param("user") String user, @Param("modelType") int modelType);
}
