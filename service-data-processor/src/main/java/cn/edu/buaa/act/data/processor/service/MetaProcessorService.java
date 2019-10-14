package cn.edu.buaa.act.data.processor.service;

import cn.edu.buaa.act.common.constant.CommonConstants;
import cn.edu.buaa.act.common.msg.PlayLoadMessage;
import cn.edu.buaa.act.data.entity.AnswerEntity;
import cn.edu.buaa.act.data.entity.ServiceResultEntity;
import cn.edu.buaa.act.data.entity.UnitEntity;
import cn.edu.buaa.act.data.processor.channel.DataProcessorNotifyChannel;
import cn.edu.buaa.act.data.processor.common.Constraint;
import cn.edu.buaa.act.data.processor.service.api.IServiceResult;
import cn.edu.buaa.act.data.vo.Label;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MetaProcessorService
 *
 * @author wsj
 * @date 2018/10/25
 */
@Service
@Slf4j
public class MetaProcessorService {

    @Autowired
    private DataProcessorNotifyChannel dataProcessorNotifyChannel;

    @Autowired
    private ServiceResultImpl serviceResult;

    //    @Async("asyncExecutor")
//    public CompletableFuture<String> doFilter(PlayLoadMessage<JSONObject> message) throws InterruptedException {
//        log.info(Thread.currentThread().getName() + "Do Filter");
//        JSONObject jsonObject =message.getMessage();
//        log.info(message.getMessage().toJSONString());
//        List<Constraint> constraintList = new ArrayList<>();
//        List<String> constraint = jsonObject.getJSONArray("Constraint").toJavaList(String.class);
//        constraint.stream().forEach(cons->{
//            Constraint constraint1 = new Constraint(cons);
//            constraintList.add(constraint1);
//        });
//        List<UnitEntity> unitListId = jsonObject.getJSONArray("UnitData").toJavaList(UnitEntity.class);
//        message.setComplete(true);
//        dataProcessorNotifyChannel.output().send(MessageBuilder.withPayload(message).build());
//        return CompletableFuture.completedFuture("Do Filter Complete");
//    }


    @Async("asyncExecutor")
    public CompletableFuture<String> doJoinLabel(PlayLoadMessage<Map> message) throws InterruptedException {
        log.info(Thread.currentThread().getName() + "Do doJoinLabel");
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(message.getMessage()));
        log.info(jsonObject.toJSONString());

        List<UnitEntity> unitList = JSONArray.parseArray(jsonObject.getString("UnitDataList")).toJavaList(UnitEntity.class);
        List<Label> labelList = JSONArray.parseArray(jsonObject.getString("labelList")).toJavaList(Label.class);
        String jobId = jsonObject.getString("jobId");
        unitList.forEach(unitEntity -> {
            if(unitEntity.getLabelMap()==null){
                unitEntity.setLabelMap(new HashMap<>());
            }
            List<Label> labelList1 = labelList.stream().filter(label -> label.getItem().equals(((Map) unitEntity.getData()).get("_unit_id"))).collect(Collectors.toList());
            log.info(String.valueOf(labelList1.size()));
            unitEntity.getLabelMap().put(jobId,labelList1);
        });
        jsonObject.put("UnitDataList",unitList);
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("MetaDataJoin",jsonObject);
        message.setMessage(jsonObject);
        message.setComplete(true);
        message.setServiceResultId(serviceResultEntity.getId());
        message.setTaskType(CommonConstants.MACHINE_TASK);
        System.out.println(JSON.toJSONString(message));
        dataProcessorNotifyChannel.output().send(MessageBuilder.withPayload(message).build());
        return CompletableFuture.completedFuture("Do doJoinLabel Complete");
    }


    @Async("asyncExecutor")
    public CompletableFuture<String> doFilter(PlayLoadMessage<Map> message) throws InterruptedException {
        log.info(Thread.currentThread().getName() + "Do Filter");
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(message.getMessage()));
//        log.info(jsonObject.toJSONString());

        List<Constraint> constraintList = new ArrayList<>();
        List<String> constraint = JSONArray.parseArray(jsonObject.getString("Constraint")).toJavaList(String.class);
        constraint.stream().forEach(cons -> {
            Constraint constraint1 = new Constraint(cons);
            constraintList.add(constraint1);
        });
        List<UnitEntity> unitList = JSONArray.parseArray(jsonObject.getString("UnitDataList")).toJavaList(UnitEntity.class);
//        List<JSONObject> unitData = unitList.parallelStream().map(unitEntity -> {
//            return JSONObject.parseObject(JSON.toJSONString(unitEntity.getData()));
//        }).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(constraint));
        // System.out.println(unitList);
        for(Constraint cons : constraintList){
            switch (cons.getOperator()) {
                case Constraint.OPERATOR_EQUAL:
                    unitList = unitList.parallelStream().filter(unitEntity -> {
                        JSONObject unit = JSONObject.parseObject(JSON.toJSONString(unitEntity.getData()));
                        if(StringUtils.hasText(unit.getString(cons.getField()))){
                            return unit.getString(cons.getField()).equals(cons.getParameter());
                        }
                        else {
                            return false;
                        }
                    }).collect(Collectors.toList());
                    // unitData = unitData.parallelStream().filter(unit->unit.getString(cons.getField()).equals(cons.getParameter())).collect(Collectors.toList());
                    break;
                case Constraint.OPERATOR_NOT_EQUAL:
                    unitList = unitList.parallelStream().filter(unitEntity -> {
                        JSONObject unit = JSONObject.parseObject(JSON.toJSONString(unitEntity.getData()));
                        if(StringUtils.hasText(unit.getString(cons.getField()))){
                            return !unit.getString(cons.getField()).equals(cons.getParameter());
                        }
                        else {
                            return false;
                        }
                    }).collect(Collectors.toList());
                    //unitData = unitData.parallelStream().filter(unit->!unit.getString(cons.getField()).equals(cons.getParameter())).collect(Collectors.toList());
                    break;
                case Constraint.OPERATOR_CONTAINS:
                    unitList = unitList.parallelStream().filter(unitEntity -> {
                        JSONObject unit = JSONObject.parseObject(JSON.toJSONString(unitEntity.getData()));
                        if(StringUtils.hasText(unit.getString(cons.getField()))){
                            return unit.getString(cons.getField()).contains(cons.getParameter());
                        }
                        else {
                            return false;
                        }
                    }).collect(Collectors.toList());
                    //unitData = unitData.parallelStream().filter(unit->!unit.getString(cons.getField()).contains(cons.getParameter())).collect(Collectors.toList());
                    break;
                case Constraint.OPERATOR_GREATER_THAN:
                    unitList = unitList.parallelStream().filter(unitEntity -> {
                        JSONObject unit = JSONObject.parseObject(JSON.toJSONString(unitEntity.getData()));
                        return unit.getDoubleValue(cons.getField())>unit.getDoubleValue(cons.getParameter());
                    }).collect(Collectors.toList());
                    //unitData = unitData.parallelStream().filter(unit->unit.getDoubleValue(cons.getField())>unit.getDoubleValue(cons.getParameter())).collect(Collectors.toList());
                    break;
                case Constraint.OPERATOR_LESS_THAN:
                    unitList = unitList.parallelStream().filter(unitEntity -> {
                        JSONObject unit = JSONObject.parseObject(JSON.toJSONString(unitEntity.getData()));
                        return unit.getDoubleValue(cons.getField())<unit.getDoubleValue(cons.getParameter());
                    }).collect(Collectors.toList());
                    //unitData = unitData.parallelStream().filter(unit->unit.getDoubleValue(cons.getField())<unit.getDoubleValue(cons.getParameter())).collect(Collectors.toList());
                    break;
                case Constraint.OPERATOR_GREATER_THAN_OR_EQUAL:
                    unitList = unitList.parallelStream().filter(unitEntity -> {
                        JSONObject unit = JSONObject.parseObject(JSON.toJSONString(unitEntity.getData()));
                        return unit.getDoubleValue(cons.getField())>=unit.getDoubleValue(cons.getParameter());
                    }).collect(Collectors.toList());
                    //unitData = unitData.parallelStream().filter(unit->unit.getDoubleValue(cons.getField())>=unit.getDoubleValue(cons.getParameter())).collect(Collectors.toList());
                    break;
                case Constraint.OPERATOR_LESS_THAN_OR_EQUAL:
                    unitList = unitList.parallelStream().filter(unitEntity -> {
                        JSONObject unit = JSONObject.parseObject(JSON.toJSONString(unitEntity.getData()));
                        return unit.getDoubleValue(cons.getField())<=unit.getDoubleValue(cons.getParameter());
                    }).collect(Collectors.toList());
                    //unitData = unitData.parallelStream().filter(unit->unit.getDoubleValue(cons.getField())<=unit.getDoubleValue(cons.getParameter())).collect(Collectors.toList());
                    break;
                default:
                    break;
            }
            // System.out.println(JSON.toJSONString(unitList));

        }
        System.out.println(JSON.toJSONString(unitList));

//        Map map=new HashMap<>();
//        map.put("success",true);
//        map.put("UnitData",unitList);

        jsonObject.put("UnitDataList",unitList);
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("MetaDataFilter",jsonObject);
        message.setMessage(jsonObject);
        message.setComplete(true);
        message.setServiceResultId(serviceResultEntity.getId());
        message.setTaskType(CommonConstants.MACHINE_TASK);
        System.out.println(JSON.toJSONString(message));
        dataProcessorNotifyChannel.output().send(MessageBuilder.withPayload(message).build());
        return CompletableFuture.completedFuture("Do Filter Complete");
    }
}
