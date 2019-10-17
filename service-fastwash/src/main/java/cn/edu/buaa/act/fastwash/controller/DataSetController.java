package cn.edu.buaa.act.fastwash.controller;


import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.fastwash.common.ReadFile;
import cn.edu.buaa.act.fastwash.entity.DataSetEntity;
import cn.edu.buaa.act.fastwash.service.api.IDataSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dataSet")
public class DataSetController {
    @Autowired
    IDataSetService dataSetService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse listDataSet() throws Exception {
//        if(!dataSetService.dataSetExist("voc_2007_trainval")){
//            dataSetService.insertDataSet(ReadJsonFile.ReadFile("D:\\data\\VOC2007\\annotations"));
//        }
        if(!dataSetService.dataSetExist("voc_2007_test")){
            DataSetEntity dataSetEntity = ReadFile.readDataSet("D:\\data\\VOC2007\\annotations\\voc_2007_test.json");
            dataSetEntity.setDataSetName("voc_2007_test");
            dataSetService.insertDataSet(dataSetEntity);
        }
        List<DataSetEntity> dataSetEntities = dataSetService.findDataSets();
        return new ObjectRestResponse<>().data(dataSetEntities).success(true);
    }

//    @RequestMapping(value = "/list/all", method = RequestMethod.GET, produces = "application/json")
//    public ResponseEntity<Object> createProject(@PathVariable String projectName, @RequestParam("page") int page, @RequestParam("limit") int limit) throws Exception {
//        DataPageable dataPageable = new DataPageable();
//        List<Sort.Order> orders = new ArrayList<Sort.Order>();
//        orders.add(new Sort.Order(Sort.Direction.DESC, "createTime"));
//        dataPageable.setSort(new Sort(orders));
//        dataPageable.setPagesize(limit);
//        dataPageable.setPagenumber(page);
//        return new ResponseEntity<Object>(projectService.projectExist(projectName), HttpStatus.OK);
//    }
}
