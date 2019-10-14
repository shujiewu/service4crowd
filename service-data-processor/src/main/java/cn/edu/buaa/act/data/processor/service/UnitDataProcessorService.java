package cn.edu.buaa.act.data.processor.service;

import cn.edu.buaa.act.common.constant.CommonConstants;
import cn.edu.buaa.act.common.msg.PlayLoadMessage;
import cn.edu.buaa.act.data.entity.ServiceResultEntity;
import cn.edu.buaa.act.data.entity.UnitEntity;
import cn.edu.buaa.act.data.processor.channel.DataProcessorNotifyChannel;
import cn.edu.buaa.act.data.processor.common.Constraint;
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

import java.util.*;
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
public class UnitDataProcessorService {

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


    public static Optional<Integer> StringToInt(String s){
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    @Async("asyncExecutor")
    public CompletableFuture<String> doTaskSelection(PlayLoadMessage<Map> message) throws InterruptedException {
        log.info(Thread.currentThread().getName() + "Do TaskSelection");
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(message.getMessage()));

        JSONObject jsonState = jsonObject.getJSONObject("state");
        int budget = jsonState.getIntValue("budget");
        int Ntask = jsonState.getIntValue("Ntask");
        List<Integer> point =jsonState.getJSONArray("point").toJavaList(Integer.class);
        int maxRedun =jsonState.getIntValue("maxRedun");

        System.out.println(1);

        double range = jsonObject.getDoubleValue("alpha");
        Map<String, Integer> workers = new HashMap<>();
        Map<String, Integer> items = new HashMap<>();
        jsonState.getJSONObject("workerMap").forEach((key,value)->{
            workers.put(key,(Integer)value);
        });
        jsonState.getJSONObject("itemMap").forEach((key,value)->{
            items.put(key,(Integer)value);
        });
        System.out.println(2);
        List<Double> F= jsonObject.getJSONArray("F").toJavaList(Double.class);
        HashMap<Integer, String> flippedWorkers = (HashMap<Integer, String>) workers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        HashMap<Integer, String> flippedItems = (HashMap<Integer, String>) items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        Map<String,Double> unitToF= new HashMap<>();
        for(int j=0;j<F.size();j++){
            unitToF.put(flippedItems.get(j+1),F.get(j));
        }
        System.out.println("unitToF:"+unitToF.size());

        long Nrange = Math.round(range*Ntask);
        Nrange =Math.max(1,Nrange);
        Map<String,Integer> pointValid = new HashMap<>(point.size());
        for(int k=0;k<point.size();k++){
            if(point.get(k)<maxRedun){
                pointValid.put(flippedItems.get(k+1),1);
            }
            else {
                pointValid.put(flippedItems.get(k+1),0);
            }
        }

        int Nvalid = pointValid.values().stream().mapToInt(value->value).sum();

        if(budget<Nrange){
            Nrange = budget;
        }
        Nrange = Math.min(Nvalid,Nrange);

        List<String> unitSelection= new ArrayList<>((int)Nrange);

        //这里将map.entrySet()转换成list
        List<Map.Entry<String,Double>> list = new ArrayList<>(unitToF.entrySet());
        //然后通过比较器来实现排序
        //升序排序
        Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        int get = 0;

        System.out.println(Nrange+"xx");
        System.out.println(budget+"tt");
        System.out.println(Nvalid+"!1");
        for(Map.Entry<String,Double> mapping:list) {
            if(get>=Nrange){
                break;
            }
            if(pointValid.get(mapping.getKey())==1){
                get++;
                unitSelection.add(mapping.getKey());
                // point.set(items.get(mapping.getKey()),point.get(items.get(mapping.getKey()))+1);
            }
        }

        budget =budget-(int)Nrange;
        jsonState.put("budget",budget);
        // jsonState.put("point",point);
        jsonObject.put("budget",budget);
        jsonObject.put("state",jsonState);
        jsonObject.put("unitSelection",unitSelection);
//        System.out.println(unitSelection.size()+"asa");
//        System.out.println(Nrange+"xx");
//        System.out.println(Ntask+"tt");
//        System.out.println(Nvalid+"!1");
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("TaskSelection",jsonObject);
        message.setMessage(jsonObject);
        message.setComplete(true);
        message.setServiceResultId(serviceResultEntity.getId());
        message.setTaskType(CommonConstants.MACHINE_TASK);
        dataProcessorNotifyChannel.output().send(MessageBuilder.withPayload(message).build());
        return CompletableFuture.completedFuture("Do UpdateState");
    }


    @Async("asyncExecutor")
    public CompletableFuture<String> doUpdateState(PlayLoadMessage<Map> message) throws InterruptedException {
        log.info(Thread.currentThread().getName() + "Do UpdateState");
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(message.getMessage()));

        JSONObject jsonState = jsonObject.getJSONObject("state");
        List<Label> labelList = JSONArray.parseArray(jsonObject.getString("labelList")).toJavaList(Label.class);

        //第一次
        if(jsonState.getJSONArray("L")==null){
            JSONArray L = new JSONArray();


            //id to index
            Map<String, Integer> workers = new HashMap<String, Integer>();
            Map<String, Integer> items = new HashMap<String, Integer>();

            labelList.forEach(label -> {
                if(!workers.containsKey(label.getWorker()))
                    workers.put(label.getWorker(), workers.size()+1);
                if(!items.containsKey(label.getItem()))
                    items.put(label.getItem(), items.size()+1);
            });
            HashMap<Integer, String> flippedWorkers = (HashMap<Integer, String>) workers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            HashMap<Integer, String> flippedItems = (HashMap<Integer, String>) items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            int [][] lijMatrix = new int[items.size()][workers.size()];
            float [][] confidenceMatrix = new float[items.size()][workers.size()];
            labelList.forEach(label -> {
                int workerIndex = workers.get(label.getWorker());
                int itemIndex = items.get(label.getItem());
                int l =StringToInt(label.getAnswer()).orElse(0);
                lijMatrix[itemIndex-1][workerIndex-1] = l==0? new Random(47).nextInt(149)+1:l;
                int c =StringToInt((label.getResponse().get("please_indicate_your_confidence_in_giving_the_answer").split(" "))[0]).orElse(0);
                confidenceMatrix[itemIndex-1][workerIndex-1] = c==0? (new Random(3).nextInt(5)+1)/5f:(c+1)/5f;
            });

            int[] point = new int [items.size()];
            Arrays.fill(point,labelList.size()/items.size());

            jsonState.put("L",lijMatrix);
            jsonState.put("confidence",confidenceMatrix);
            jsonState.put("point",point);

            Integer budget = jsonState.getInteger("budget");
            budget = budget-labelList.size();
            jsonState.put("budget",budget);

            jsonState.put("workerMap",workers);
            jsonState.put("itemMap",items);
        }
        else {

            System.out.println("新增"+labelList.size());
            Map<String, Integer> workers = new HashMap<>();
            Map<String, Integer> items = new HashMap<>();
            jsonState.getJSONObject("workerMap").forEach((key,value)->{
                workers.put(key,(Integer)value);
            });
            jsonState.getJSONObject("itemMap").forEach((key,value)->{
                items.put(key,(Integer)value);
            });
            HashMap<Integer, String> flippedWorkers = (HashMap<Integer, String>) workers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            HashMap<Integer, String> flippedItems = (HashMap<Integer, String>) items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

            List<JSONArray> L= jsonState.getJSONArray("L").toJavaList(JSONArray.class);
            List<JSONArray> confidence= jsonState.getJSONArray("confidence").toJavaList(JSONArray.class);
            List<Integer> point = jsonState.getJSONArray("point").toJavaList(Integer.class);



            labelList.forEach(label -> {
                if(!workers.containsKey(label.getWorker())){
                    workers.put(label.getWorker(), workers.size()+1);
                    L.stream().forEach(jsonArray -> {
                        jsonArray.add(0);
                    });
                    confidence.stream().forEach(jsonArray -> {
                        jsonArray.add(0);
                    });
                }
                if(!items.containsKey(label.getItem())){
                    items.put(label.getItem(), items.size()+1);
                    L.add(new JSONArray(workers.size()));
                    confidence.add(new JSONArray(workers.size()));
                    point.add(0);
                }
            });

            labelList.forEach(label -> {
                int workerIndex = workers.get(label.getWorker());
                int itemIndex = items.get(label.getItem());
                int l =StringToInt(label.getAnswer()).orElse(0);

                L.get(itemIndex-1).set(workerIndex-1,l==0? new Random(47).nextInt(149)+1:l);
                int c =StringToInt((label.getResponse().get("please_indicate_your_confidence_in_giving_the_answer").split(" "))[0]).orElse(0);
                confidence.get(itemIndex-1).set(workerIndex-1,c==0? (new Random(3).nextInt(5)+1)/5f:(c+1)/5f);
                point.set(itemIndex-1,point.get(itemIndex-1)+1);
            });

            jsonState.put("L",L);
            jsonState.put("confidence",confidence);
            jsonState.put("point",point);

            //筛选时候已经减去
//            Integer budget = jsonState.getInteger("budget");
//            budget = budget-labelList.size();
//            jsonState.put("budget",budget);
            jsonState.put("workerMap",workers);
            jsonState.put("itemMap",items);
        }

        jsonObject.put("budget",jsonState.getInteger("budget"));
        jsonObject.put("state",jsonState);
        jsonObject.remove("labelList");
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("UpdateState",jsonObject);
        message.setMessage(jsonObject);
        message.setComplete(true);
        message.setServiceResultId(serviceResultEntity.getId());
        message.setTaskType(CommonConstants.MACHINE_TASK);
        dataProcessorNotifyChannel.output().send(MessageBuilder.withPayload(message).build());
        return CompletableFuture.completedFuture("Do UpdateState");
    }


    @Async("asyncExecutor")
    public CompletableFuture<String> doAddTruthToState(PlayLoadMessage<Map> message) throws InterruptedException {
        log.info(Thread.currentThread().getName() + "Do AddTruthToState");
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(message.getMessage()));
        System.out.println(0);
        List<UnitEntity> unitList = JSONArray.parseArray(jsonObject.getString("UnitDataList")).toJavaList(UnitEntity.class);
        JSONObject jsonState = jsonObject.getJSONObject("state");

        System.out.println(unitList);
        System.out.println(jsonObject);

        Map<String,String> truthMap = new HashMap<>();
        List<Label> allLabel = new ArrayList<>();
        System.out.println(1);

        Map<String, Integer> items = new HashMap<>();
        jsonState.getJSONObject("itemMap").forEach((key,value)->{
            items.put(key,(Integer)value);
        });
        System.out.println(2);

        HashMap<Integer, String> flippedItems1 = (HashMap<Integer, String>) items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        Map<Integer,String> flippedItems =new TreeMap<>(flippedItems1);
        System.out.println(flippedItems);

        int[] golden = new int[unitList.size()];
        unitList.forEach(unitEntity -> {
            if(unitEntity.getGoldLabel()!=null){
                truthMap.put(((Map)unitEntity.getData()).get("_unit_id").toString(),unitEntity.getGoldLabel());
            }
        });

        flippedItems.forEach((key,value)->{
            golden[key-1] = Integer.parseInt(truthMap.get(value));
        });
        System.out.println(4);
        jsonState.put("GroundTruth",golden);
        jsonObject.put("state",jsonState);
        jsonObject.remove("UnitDataList");
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("GetAnswerAndTruth",jsonObject);
        message.setMessage(jsonObject);
        message.setComplete(true);
        message.setServiceResultId(serviceResultEntity.getId());
        message.setTaskType(CommonConstants.MACHINE_TASK);
        dataProcessorNotifyChannel.output().send(MessageBuilder.withPayload(message).build());
        return CompletableFuture.completedFuture("Do GetTruth Complete");
    }

    @Async("asyncExecutor")
    public CompletableFuture<String> doGetAnswerAndTruth(PlayLoadMessage<Map> message) throws InterruptedException {
        log.info(Thread.currentThread().getName() + "Do GetLabelList");
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(message.getMessage()));
        List<UnitEntity> unitList = JSONArray.parseArray(jsonObject.getString("UnitDataList")).toJavaList(UnitEntity.class);

        Map<String,String> truthMap = new HashMap<>();
        List<Label> allLabel = new ArrayList<>();

        unitList.forEach(unitEntity -> {
            if(unitEntity.getLabelMap()!=null){
                unitEntity.getLabelMap().forEach((jobId,labels)->{
                    allLabel.addAll(labels);
                });
            }
            if(unitEntity.getGoldLabel()!=null){
                truthMap.put(((Map)unitEntity.getData()).get("_unit_id").toString(),unitEntity.getGoldLabel());
            }
        });
        jsonObject.put("answerData",allLabel);
        jsonObject.put("truthData",truthMap);
        jsonObject.remove("UnitDataList");

        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("GetAnswerAndTruth",jsonObject);
        message.setMessage(jsonObject);
        message.setComplete(true);
        message.setServiceResultId(serviceResultEntity.getId());
        message.setTaskType(CommonConstants.MACHINE_TASK);
        dataProcessorNotifyChannel.output().send(MessageBuilder.withPayload(message).build());
        return CompletableFuture.completedFuture("Do GetAnswerAndTruth Complete");
    }


    @Async("asyncExecutor")
    public CompletableFuture<String> doFilter(PlayLoadMessage<Map> message) throws InterruptedException {
        log.info(Thread.currentThread().getName() + "Do Filter");
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(message.getMessage()));
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
