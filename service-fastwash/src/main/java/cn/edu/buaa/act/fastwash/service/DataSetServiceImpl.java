package cn.edu.buaa.act.fastwash.service;

import cn.edu.buaa.act.fastwash.entity.DataSetEntity;
import cn.edu.buaa.act.fastwash.repository.DataSetConfigRepository;
import cn.edu.buaa.act.fastwash.service.api.IDataSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DataSetServiceImpl implements IDataSetService {

    @Autowired
    private DataSetConfigRepository dataSetConfigRepository;

    @Override
    public DataSetEntity insertDataSet(DataSetEntity dataSetEntity) {
        dataSetEntity = dataSetConfigRepository.insert(dataSetEntity);
        return dataSetEntity;
    }

    @Override
    public boolean dataSetExist(String dataSetName) {
        return dataSetConfigRepository.findDataSetEntityByDataSetName(dataSetName) != null;
    }

    @Override
    public List<DataSetEntity> findDataSets() {
        return dataSetConfigRepository.findAll();
    }
}
