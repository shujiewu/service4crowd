package cn.edu.buaa.act.fastwash.controller;


import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.fastwash.data.Box;
import cn.edu.buaa.act.fastwash.data.Classification;
import cn.edu.buaa.act.fastwash.data.Tag;
import cn.edu.buaa.act.fastwash.entity.Category;
import cn.edu.buaa.act.fastwash.entity.DataSetEntity;
import cn.edu.buaa.act.fastwash.entity.DecomposeRequest;
import cn.edu.buaa.act.fastwash.entity.ImageToClass;
import cn.edu.buaa.act.fastwash.feign.IDataCoreService;
import cn.edu.buaa.act.fastwash.service.api.IDataSetService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/annotation")
public class DecomposeController {

    private static Logger logger = LoggerFactory.getLogger(DecomposeController.class);

    @Autowired
    private IDataSetService dataSetService;
    @Autowired
    private IDataCoreService iDataCoreService;
    @RequestMapping(value = "/task/decompose", method = RequestMethod.POST)
    public ResponseEntity<Map> executeDecompose(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        logger.info(JSONObject.toJSONString(request.get("parameter")));
        DecomposeRequest decomposeRequest = JSONObject.parseObject(JSONObject.toJSONString(request.get("parameter")),DecomposeRequest.class);
        if(decomposeRequest.getSimulate()){
            String dataSetName = decomposeRequest.getDataSetName();
            List<String> imageIdList = decomposeRequest.getImageIdList();
            Map<String,ImageToClass> imageToClassMap = new HashMap<>();
            imageIdList.forEach(imageId->{
                ImageToClass imageToClass = new ImageToClass();
                imageToClass.setImageId(imageId);
                imageToClass.setDataSetName(dataSetName);
                imageToClass.setClassificationList(new TreeSet<>(new Comparator<Classification>() {
                    @Override
                    public int compare(Classification newClass, Classification oldClass) {
                        if (newClass.getId().equals(oldClass.getId())) {
                            return 0;
                        }
                        // 2.年龄的比较
                        if (Integer.parseInt(newClass.getId()) >= Integer.parseInt(oldClass.getId())) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }));
                imageToClassMap.put(imageId,imageToClass);
            });

            DataSetEntity dataSetEntity = dataSetService.findDataSet(dataSetName);
            Map<String,List<Classification>> groundTruthMap = new HashMap<>();
            Map<String,Category> categoryMap = new HashMap<>();
            dataSetEntity.getCategories().forEach(category -> {
                categoryMap.put(category.getId(),category);
            });
            dataSetEntity.getAnnotations().forEach(annotation->{
                String imageId = annotation.getString("image_id");
                String categoryId = annotation.getString("category_id");

                Classification classification = new Classification();
                classification.setId(categoryId);
                classification.setValue(categoryMap.get(categoryId).getName());
                if(imageToClassMap.containsKey(imageId)){
                    imageToClassMap.get(imageId).getClassificationList().add(classification);
                }
            });
            result.put("imageToClass",imageToClassMap.values());
            result.put("success",true);
            ResponseEntity<Map> responseEntity = iDataCoreService.insertServiceResult(result);
            Object serviceResultId = responseEntity.getBody().get("serviceResultId");
            result.put("serviceResultId",serviceResultId);
            return new ResponseEntity<Map>(result, HttpStatus.OK);
        }
        result.put("success",true);
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }
}
