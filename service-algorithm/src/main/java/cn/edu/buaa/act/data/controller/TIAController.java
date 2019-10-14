package cn.edu.buaa.act.data.controller;


import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.common.util.ServiceProperty;
import cn.edu.buaa.act.data.common.Constants;
import cn.edu.buaa.act.data.entity.TruthInferenceEntity;
import cn.edu.buaa.act.data.exception.SubmitInvalidException;
import cn.edu.buaa.act.data.service.TruthInferenceServiceImpl;
import cn.edu.buaa.act.data.service.api.ITruthInferenceService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.Exceptions;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author wsj
 */
@RestController
@RequestMapping("/algorithm")
public class TIAController {

    @Autowired
    ITruthInferenceService truthInferenceService;

    /**
     * @description 根据id查找汇聚算法
     * @date 2018/9/20
     * @param algorithmId
     * @return cn.edu.buaa.act.data.entity.TruthInferenceEntity
     */
    @RequestMapping(value = "/truthInference/{algorithmId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> getTruthInferenceAlgorithm(@PathVariable String algorithmId) throws Exception {
        return new ResponseEntity<Object>(truthInferenceService.queryTruthInferenceEntityById(algorithmId),HttpStatus.OK);
    }

    /**
     * @description 提交算法
     * @date 2018/9/20
     * @param file
     * @return cn.edu.buaa.act.data.entity.TruthInferenceEntity
     */
    @RequestMapping(value = "/truthInference", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> submitTruthInference(MultipartFile file) throws Exception{
        Map map = new HashMap();
        if(file != null) {
            String packageName = file.getOriginalFilename();
            if (packageName != null && packageName.matches(".*\\.zip")) {                //是zip压缩文件
                try {
                    ZipInputStream zs = new ZipInputStream(file.getInputStream(), Charset.forName("GBK"));
                    BufferedInputStream bs = new BufferedInputStream(zs);
                    ZipEntry ze;
                    byte[] fileByte = null;
                    TruthInferenceEntity truthInferenceEntity = null;
                    while ((ze = zs.getNextEntry()) != null) {                    //获取zip包中的每一个zip file entry
                        String fileName = ze.getName();                            //pictureName
                        fileByte = new byte[(int) ze.getSize()];                    //读一个文件大小
                        bs.read(fileByte, 0, (int) ze.getSize());
                        if ("config.json".equals(fileName)) {
                            truthInferenceEntity = decodeConfig(fileByte);
                        }
                    }
                    if (truthInferenceEntity != null) {
                        //truthInferenceService.insertTruthInferenceEntity(truthInferenceEntity);
                    } else {
                        throw new SubmitInvalidException("json文件解析异常");
                    }
                    bs.close();
                    zs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        map.put("success",true);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

//    @RequestMapping(value = "/getTruthInference", method = RequestMethod.GET, produces = "application/json")
//    public TableResultResponse<TruthInferenceEntity> getTruthInferencePage(@RequestParam("page")int page, @RequestParam("limit")int limit) {
//        DataPageable dataPageable= new DataPageable();
//        List<Sort.Order> orders = new ArrayList<Sort.Order>();
//        orders.add(new Sort.Order(Sort.Direction.DESC, "createTime"));
//        dataPageable.setSort(new Sort(orders));
//        dataPageable.setPagesize(limit);
//        dataPageable.setPagenumber(page);
//        Page<TruthInferenceEntity> result = truthInferenceService.queryPageByUser(BaseContextHandler.getUserID(),dataPageable);
//        return new TableResultResponse<TruthInferenceEntity>(result.getTotalElements(),result.getContent());
//    }


    @RequestMapping(value = "/truthInference/all", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<TruthInferenceEntity> getTruthInferenceAll() {
        List<TruthInferenceEntity> result = truthInferenceService.queryAllByUser(BaseContextHandler.getUserID());
        return new TableResultResponse<TruthInferenceEntity>(result.size(),result);
    }

//    @RequestMapping(value = "/createTruthInference", method = RequestMethod.POST)
//    public ResponseEntity<Object> createCrowdTask(MultipartFile file,HttpServletRequest request){
//
//    }

    private TruthInferenceEntity decodeConfig(byte [] data){
        TruthInferenceEntity truthInferenceEntity = new TruthInferenceEntity();
        JSONObject jsonObject=new JSONObject();
        String str = null;
        try {
            str = new String(data,"GBK");
            jsonObject = JSONObject.parseObject(str);
            truthInferenceEntity.setMethod((String)jsonObject.get("Method"));
            truthInferenceEntity.setImplType((String)jsonObject.get("Implementation Type"));
            truthInferenceEntity.setTaskModel((String)jsonObject.get("Task Modeling"));
            truthInferenceEntity.setTechnique((String)jsonObject.get("Techniques"));
            truthInferenceEntity.setWorkerModel((String)jsonObject.get("Worker Modeling"));
            truthInferenceEntity.setTaskType((String)jsonObject.get("Task Types"));

            JSONArray propertyList = jsonObject.getJSONArray("Properties");
            List<ServiceProperty> inputProperties = new ArrayList<>();
            List<ServiceProperty> outputProperties = new ArrayList<>();
            for (int i = 0; i < propertyList.size(); i++)
            {
                JSONObject property = propertyList.getJSONObject(i);
                ServiceProperty serviceProperty = new ServiceProperty();
                serviceProperty.setDescription(property.getString("description"));
                serviceProperty.setName(property.getString("name"));
                serviceProperty.setType(property.getString("type"));
                serviceProperty.setValue(property.getString("value"));
                if(Constants.SERVICE_TYPE_INPUT.equals(serviceProperty.getType())){
                    inputProperties.add(serviceProperty);
                }
                else if(Constants.SERVICE_TYPE_OUTPUT.equals(serviceProperty.getType())){
                    outputProperties.add(serviceProperty);
                }
            }
            truthInferenceEntity.setInputProperty(inputProperties);
            truthInferenceEntity.setOutputProperty(outputProperties);
            truthInferenceEntity.setUserId(BaseContextHandler.getUserID());
            truthInferenceEntity.setCreateTime(new Date());
            return truthInferenceEntity;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
