package cn.edu.buaa.act.data.processor.controller;

import cn.edu.buaa.act.data.entity.UnitEntity;
import cn.edu.buaa.act.data.processor.common.Constraint;
import cn.edu.buaa.act.data.service.IUnitService;
import cn.edu.buaa.act.data.service.impl.UnitServiceImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MetaProcessController
 *
 * @author wsj
 * @date 2018/10/25
 */
@RestController
@RequestMapping(value = "/processor")
@Slf4j
public class MetaProcessController {

    /*
    {
        Constraint
        UnitDataListId
        MetaDataId
    }
     */
    @RequestMapping(value = "/metaData/filter", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> loadMeta(@RequestBody Map<String,Object> body){
        Map map = new HashMap<>();
        List<Constraint> constraintList = new ArrayList<>();
        List<String> unitListId = JSONArray.parseArray(body.get("UnitDataListId").toString(),String.class);
        List<String> constraint = (List<String>)body.get("Constraint");//JSONArray.parseArray(body.get("Constraint").toString(),String.class);

        constraint.stream().forEach(cons->{
            Constraint constraint1 = new Constraint(cons);
            constraintList.add(constraint1);
        });
        // List<UnitEntity> unitEntities =iUnitService.findUnitByIdList

        System.out.println(unitListId);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/metaData/map", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> mapMeta(@RequestBody JSONObject body){
        Map map = new HashMap<>();
        List<Constraint> constraintList = new ArrayList<>();
        List<String> unitListId = body.getJSONArray("UnitDataListId").toJavaList(String.class);
        List<String> constraint = body.getJSONArray("Constraint").toJavaList(String.class);//JSONArray.parseArray(body.get("Constraint").toString(),String.class);

        constraint.stream().forEach(cons->{
            Constraint constraint1 = new Constraint(cons);
            constraintList.add(constraint1);
        });
        // List<UnitEntity> unitEntities =iUnitService.findUnitByIdList

        System.out.println(unitListId);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }
}
