package cn.edu.buaa.act.fastwash.repository;



import cn.edu.buaa.act.fastwash.entity.DataSetEntity;
import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wsj
 */
@Repository
public interface DataSetConfigRepository extends MongoRepository<DataSetEntity, String> {
    DataSetEntity findDataSetEntityByDataSetName(String dataSetName);

    //@Query(fields="{ 'id' : 1, 'dataSetName' : 1, 'categories' : 1}")
   // List<DataSetEntity> findAll();
}