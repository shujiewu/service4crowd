package cn.edu.buaa.act.fastwash.service;

import cn.edu.buaa.act.fastwash.entity.DataSetEntity;
import cn.edu.buaa.act.fastwash.entity.Image;
import cn.edu.buaa.act.fastwash.repository.DataSetConfigRepository;
import cn.edu.buaa.act.fastwash.service.api.IDataSetService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static io.undertow.server.handlers.ForwardedHandler.BY;

@Service
public class DataSetServiceImpl implements IDataSetService {

    @Autowired
    private DataSetConfigRepository dataSetConfigRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

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

    @Override
    public DataSetEntity findDataSet(String dataSetName) {
        return dataSetConfigRepository.findDataSetEntityByDataSetName(dataSetName);
    }

    @Override
    public Image findImage(String dataSetName, String imageId) {
        //封装查询条件
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(Criteria.where("dataSetName").is(dataSetName)));
        operations.add(Aggregation.unwind("images"));
        operations.add(Aggregation.match(Criteria.where("images._id").is(imageId)));

        //创建管道查询对象
        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<JSONObject> reminds = mongoTemplate.aggregate(aggregation, "dataSetConfig", JSONObject.class);
        List<JSONObject> mappedResults = reminds.getMappedResults();
        if (mappedResults.size() > 0) {
            Image image = JSONObject.parseObject(mappedResults.get(0).getJSONObject("images").toJSONString(), Image.class);
            image.setDataSetName(dataSetName);
            return image;
        }
        return null;
    }
}
