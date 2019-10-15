package cn.edu.buaa.act.fastwash.service.api;

import cn.edu.buaa.act.fastwash.entity.DataSetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDataSetService {
    DataSetEntity insertDataSet(DataSetEntity dataSetEntity);
    boolean dataSetExist(String dataSetName);
    List<DataSetEntity> findDataSets();
}
