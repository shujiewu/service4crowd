package cn.edu.buaa.act.data.controller;



import cn.edu.buaa.act.auth.client.annotation.IgnoreUserToken;
import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.data.entity.AnswerEntity;
import cn.edu.buaa.act.data.entity.ServiceResultEntity;
import cn.edu.buaa.act.data.entity.UIEntity;
import cn.edu.buaa.act.data.service.IServiceResult;
import cn.edu.buaa.act.data.service.IUIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wsj
 */
@RequestMapping("/data")
@RestController
@Slf4j
public class UIController {
    @Autowired
    private IUIService uiService;

    @Autowired
    private IServiceResult serviceResult;

    @IgnoreUserToken
    @RequestMapping(value = "/taskUI/{id}/load", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map> getAnswerData(@PathVariable("id") String id) {
        log.info("LoadUIName start");
        Map map=new HashMap<>();
        UIEntity result = uiService.queryById(id);
        if(result!=null){
            map.put("success",true);
            map.put("instruction",result.getInstruction());
        }
        else {
            map.put("success",false);
        }
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("LoadUIName",map);
        map.put("serviceResultId",serviceResultEntity.getId());
        log.info("LoadUIName Complete");
        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }



    @RequestMapping(value = "/UI/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> createUI(@RequestBody UIEntity uiEntity) {
        Map map=new HashMap<>();
        uiEntity.setUserId(BaseContextHandler.getUserID());
        uiEntity.setUserName(BaseContextHandler.getUsername());
        uiEntity.setCreateTime(new Date());
        uiEntity.setLastUpdateTime(new Date());
        uiService.insertUi(uiEntity);
        map.put("success",true);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/UI/update", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> updateUI(@RequestBody UIEntity uiEntity) {
        Map map=new HashMap<>();
        map.put("success",true);
        uiEntity.setLastUpdateTime(new Date());
        uiService.updateUI(uiEntity);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/UI/list", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<UIEntity> fetchUIList() {
        Map map=new HashMap<>();
        map.put("success",true);
        List<UIEntity> result= uiService.queryAllByUser(BaseContextHandler.getUserID());
        return new TableResultResponse<>(result.size(),result);
    }

    @RequestMapping(value = "/UI/{Id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> fetchUI(@PathVariable String Id) {
        Map map=new HashMap<>();
        map.put("success",true);
        UIEntity uiEntity = uiService.queryById(Id);
        map.put("data",uiEntity);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/UI/{Id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Object> deleteUI(@PathVariable String Id) {
        Map map=new HashMap<>();
        map.put("success",true);
        uiService.deleteUIById(Id);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }
}
