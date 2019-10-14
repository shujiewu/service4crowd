package cn.edu.buaa.act.data.controller;

import cn.edu.buaa.act.auth.client.annotation.IgnoreUserToken;
import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.data.common.Constant;
import cn.edu.buaa.act.data.common.DataPageable;
import cn.edu.buaa.act.data.entity.MetaEntity;
import cn.edu.buaa.act.data.entity.ServiceResultEntity;
import cn.edu.buaa.act.data.entity.UnitEntity;
import cn.edu.buaa.act.data.service.IMetaService;
import cn.edu.buaa.act.data.service.IServiceResult;
import cn.edu.buaa.act.data.service.IUnitService;
import cn.edu.buaa.act.data.service.impl.ServiceResultImpl;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping(value = "/data")
@RestController
public class MetaController {

    @Autowired
    IMetaService metaService;

    @Autowired
    IUnitService unitService;

    @IgnoreUserToken
    @RequestMapping(value = "/metaData/{dataName}/load", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> loadMeta(@PathVariable String dataName,@RequestParam("userId") String userId) {
        Map map = new HashMap<>();
        System.out.println(userId);
        MetaEntity result = metaService.findByName(dataName, userId);
        if (result != null) {
            List<UnitEntity> unitEntityList = unitService.findUnitByIdList(result.getDataId());
            map.put("success", true);
            map.put("MetaData", result);
            map.put("UnitDataList", unitEntityList);
            map.put("MetaDataId", result.getId());
            List<String> unitDataListId = new ArrayList<>();
            unitEntityList.forEach(unit -> {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(unit.getData()));
                if (jsonObject.get("_unit_id") != null) {
                    unitDataListId.add(jsonObject.get("_unit_id").toString());
                }
            });
            map.put("UnitDataListId", unitDataListId);
        } else {
            map.put("success", false);
        }
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("LoadMetaData", map);
        map.put("serviceResultId", serviceResultEntity.getId());
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }


    @RequestMapping(value = "/meta/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> createMeta(@RequestBody MetaEntity metaEntity) {
        Map map = new HashMap<>();

        String mm = new Gson().toJson(metaEntity.getData());
        List<UnitEntity> unitEntityList = new ArrayList<>();
        for (Object data : metaEntity.getData()) {
            UnitEntity unitEntity = new UnitEntity();
            unitEntity.setData(data);
            if(((Map)data).get("_unit_id")!=null){
                unitEntity.setUnitId(((Map)data).get("_unit_id").toString());
            }
            if(((Map)data).get("golden")!=null){
                unitEntity.setGoldLabel(((Map)data).get("golden").toString());
            }
            unitEntity.setState(Constant.UNIT_STATE_NEW);
            unitEntityList.add(unitEntity);
        }
        unitEntityList = unitService.insertUnits(unitEntityList);
        metaEntity.setData(null);


        List<String> dataId = new ArrayList<>();
        // String [] dataId=new String[unitEntityList.size()];
        for (int i = 0; i < unitEntityList.size(); i++) {
            dataId.add(unitEntityList.get(i).getId());
            // dataId[i]= unitEntityList.get(i).getId();
        }
        metaEntity.setCreateTime(new Date());
        metaEntity.setDataId(dataId);
        metaEntity.setUserId(BaseContextHandler.getUserID());
        metaEntity.setUserName(BaseContextHandler.getUsername());
        // metaEntity.setId(111L);
        metaEntity = metaService.insertEntity(metaEntity);

        map.put("success", true);
        map.put("data", metaEntity);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/validateMetaName", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> validateMetaName(@RequestParam("metaName") String metaName) {
        Map map = new HashMap<>();
        map.put("success", metaService.isMetaNameExist(metaName));
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteMeta", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> deleteMeta(@RequestParam("metaId") String metaId) {
        Map map = new HashMap<>();
        map.put("success", metaService.deleteMeta(metaId));
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteData", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> deleteData(@RequestParam("metaId") String metaId, @RequestParam("dataId") String dataId) {
        Map map = new HashMap<>();
        map.put("success", unitService.deleteData(metaId, dataId));
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }


    @RequestMapping(value = "/getMeta", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<MetaEntity> getMetaList() {
        Map map = new HashMap<>();
        map.put("success", true);
        List<MetaEntity> result = metaService.queryAllByUser("1");
        return new TableResultResponse<>(result.size(), result);
    }

    @Autowired
    IServiceResult serviceResult;

    @RequestMapping(value = "/getMetaDataByName", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map> getMetaData(@RequestParam("metaDataName") String metaDataName, @RequestParam("userId") String userId) {
        Map map = new HashMap<>();
        MetaEntity result = metaService.findByName(metaDataName, userId);
        if (result != null) {
            List<UnitEntity> unitEntityList = unitService.findUnitByIdList(result.getDataId());
            map.put("success", true);
            map.put("MetaData", result);
            map.put("UnitDataList", unitEntityList);
            map.put("MetaDataId", result.getId());
            List<String> unitDataListId = new ArrayList<>();
            unitEntityList.forEach(unit -> {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(unit.getData()));
                if (jsonObject.get("_unit_id") != null) {
                    unitDataListId.add(jsonObject.get("_unit_id").toString());
                }
            });
            map.put("UnitDataListId", unitDataListId);
        } else {
            map.put("success", false);
        }
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("LoadMetaData", map);
        map.put("serviceResultId", serviceResultEntity.getId());
        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }


    @RequestMapping(value = "/getMetaPage", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<MetaEntity> getMetaPage(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        DataPageable dataPageable = new DataPageable();
        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order(Sort.Direction.DESC, "createTime"));
        dataPageable.setSort(new Sort(orders));
//每页显示条数
        dataPageable.setPagesize(limit);
//当前页
        dataPageable.setPagenumber(page);
        System.out.println(BaseContextHandler.getUserID());
        Page<MetaEntity> result = metaService.queryPageByUser(BaseContextHandler.getUserID(), dataPageable);
        return new TableResultResponse<>(result.getTotalElements(), result.getContent());
    }

    @RequestMapping(value = "/getData", method = RequestMethod.POST, produces = "application/json")
    public TableResultResponse<UnitEntity> getUnitList(@RequestBody String[] unitId) {

        List<UnitEntity> result = unitService.findUnitByIdList(Arrays.asList(unitId));
        return new TableResultResponse<>(result.size(), result);
    }
}
