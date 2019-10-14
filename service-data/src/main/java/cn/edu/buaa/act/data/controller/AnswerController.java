package cn.edu.buaa.act.data.controller;

import cn.edu.buaa.act.auth.client.annotation.IgnoreUserToken;
import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.data.common.DataPageable;
import cn.edu.buaa.act.data.entity.AnswerEntity;
import cn.edu.buaa.act.data.entity.MetaEntity;
import cn.edu.buaa.act.data.entity.ServiceResultEntity;
import cn.edu.buaa.act.data.model.AnswerStatRepresentation;
import cn.edu.buaa.act.data.service.IAnswerService;
import cn.edu.buaa.act.data.service.IMetaService;
import cn.edu.buaa.act.data.service.impl.ServiceResultImpl;
import cn.edu.buaa.act.data.vo.Label;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

/**
 * @author wsj
 */
@RequestMapping(value = "/data")
@RestController
public class AnswerController {
    private static final Logger log = LoggerFactory.getLogger(AnswerController.class);

    @Autowired
    IAnswerService answerService;

    @Autowired
    IMetaService metaService;

//    @IgnoreUserToken
//    @RequestMapping(value = "/answerData/{dataName}/load", method = RequestMethod.GET, produces = "application/json")
//    public ResponseEntity<Map> getAnswerData(@PathVariable("dataName") String dataName) {
//        log.info("LoadAnswerData start");
//        Map map=new HashMap<>();
//        AnswerEntity result = answerService.queryByName(dataName);
//        if(result!=null){
//            map.put("success",true);
//            map.put("AnswerData",result);
//            map.put("AnswerDataId",result.getId());
//        }
//        else {
//            map.put("success",false);
//        }
//        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("LoadAnswerData",map);
//        map.put("serviceResultId",serviceResultEntity.getId());
//        log.info("LoadAnswerData Complete");
//        return new ResponseEntity<Map>(map, HttpStatus.OK);
//    }

    @IgnoreUserToken
    @RequestMapping(value = "/answerData/{dataName}/load", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map> getLabelAndTruth(@PathVariable("dataName") String dataName) {
        log.info("LoadAnswerData start");
        Map map=new HashMap<>();
        AnswerEntity result = answerService.queryByName(dataName);
        if(result!=null){
            map.put("success",true);
            map.put("answerDataId",result.getId());
            map.put("answerData",result.getLabelList());
            map.put("truthData",result.getGoldLabels());
            map.put("dataType",result.getDataType());
        }
        else {
            map.put("success",false);
        }
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("LoadAnswerData",map);
        map.put("serviceResultId",serviceResultEntity.getId());
        log.info("LoadAnswerData Complete");
        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }


    @IgnoreUserToken
    @RequestMapping(value = "/answerData/{answerDataId}/dataStat", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map> answerDataStat(@PathVariable String answerDataId) {
        Map map=new HashMap<>();
        AnswerStatRepresentation result = answerService.insertDataStat(answerDataId);
        if(result!=null){
            map.put("success",true);
            map.put("answerStatistics",result);
        }
        else {
            map.put("success",false);
        }
        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("answerStatistics",map);
        map.put("serviceResultId",serviceResultEntity.getId());
        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }


    @IgnoreUserToken
    @RequestMapping(value = "/insertAnswerEntity", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> insertAnswerEntity(@RequestBody Map<String, Object> params){
        // AnswerEntity answerEntity = (AnswerEntity)params.get("answerEntity");
        String answerEntityName = (String)params.get("answerEntityName");
        AnswerEntity answerEntity = new AnswerEntity();
        // answerEntity.setLabelList(labelList);
        answerEntity.setCreateTime(new Date());
        answerEntity.setName(answerEntityName);
        answerEntity.setUserId("1");
        answerEntity.setDataType("continuous");
        //answerEntity.setHasGold(true);
        //answerEntity.setGoldLabels(ReadCSV());

        answerEntity= answerService.insertAnswerEntity(answerEntity);
        Map map=new HashMap<>();
        if(answerEntity.getId()!=null){
            String metaDataId = (String)params.get("metaDataId");

            //这里可能不是通过meta数据发的任务
            MetaEntity metaEntity = metaService.findById(metaDataId);
            String key = (String)params.get("key");
            System.out.println(metaEntity);
            if(metaEntity.getAnswerEntityId()!=null){
                metaEntity.getAnswerEntityId().put(key,answerEntity.getId());
            } else{
                metaEntity.setAnswerEntityId(new HashMap<>());
                metaEntity.getAnswerEntityId().put(key,answerEntity.getId());
            }
            metaService.insertEntity(metaEntity);
            map.put("success",true);
            map.put("answerEntityId",answerEntity.getId());
            System.out.println("创建answer"+answerEntity.getId());
        }
        else {
            map.put("success",false);
        }
        // map.put("data",metaEntity);
        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }
    @IgnoreUserToken
    @RequestMapping(value = "/insertLabelList", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> insertLabelList(@RequestBody Map<String, Object> request){
        String answerEntityId = (String)request.get("answerEntityId");
        AnswerEntity answerEntity = answerService.queryById(answerEntityId);

        if(answerEntity.getLabelList()!=null){
            answerEntity.getLabelList().addAll((List<Label>)request.get("labelList"));
        }
        else{
            answerEntity.setLabelList(new ArrayList<>());
            answerEntity.getLabelList().addAll((List<Label>)request.get("labelList"));
        }
        // System.out.println("现在有几个回答"+answerEntity.getLabelList().size());
        answerService.insertAnswerEntity(answerEntity);
        return null;
    }

    @RequestMapping(value = "/createSimData", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> createMeta(@RequestBody Map<String, Object> params) {
        List<Label> labelList = new ArrayList<>();
        Map<String,String> goldValue = new HashMap<>();
        String answerData=new Gson().toJson(params.get("answerData"));
        JSONArray jsonArray =JSONArray.parseArray(answerData);
        jsonArray.forEach(jsonValue->{
            JSONObject jsonObject =JSONObject.parseObject(jsonValue.toString());
            labelList.add(new Label(jsonObject.getString("worker"),jsonObject.getString("question"),jsonObject.getString("answer")));
            // log.info(jsonObject.toJSONString());
        });

        String gtData=new Gson().toJson(params.get("gtData"));
        JSONArray jsonArrayGT =JSONArray.parseArray(gtData);
        jsonArrayGT.forEach(jsonValue->{
            JSONObject jsonObject =JSONObject.parseObject(jsonValue.toString());
            goldValue.put(jsonObject.getString("question"),jsonObject.getString("truth"));
            // log.info(jsonObject.toJSONString());
        });
        AnswerEntity simulatorEntity = new AnswerEntity();
        simulatorEntity.setUserId(BaseContextHandler.getUserID());
        simulatorEntity.setDataType(params.get("dataType").toString());
        simulatorEntity.setLabelList(labelList);
        simulatorEntity.setName(params.get("simName").toString());
        simulatorEntity.setCreateTime(new Date());
        simulatorEntity.setHasGold(goldValue.size()>0);
        simulatorEntity.setGoldLabels(goldValue);

        answerService.insertAnswerEntity(simulatorEntity);
        Map map=new HashMap<>();
        map.put("success",true);
        // map.put("data",metaEntity);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }


    @RequestMapping(value = "/deleteAnswerData", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> deleteAnswerData(@RequestParam("answerId")String answerId) {
        answerService.deleteAnswerEntity(answerId);
        Map map=new HashMap<>();
        map.put("success",true);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/getSimPage", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<AnswerEntity> getMetaPage(@RequestParam("page")int page, @RequestParam("limit")int limit) {
        DataPageable dataPageable= new DataPageable();

        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "createTime"));
        dataPageable.setSort(new Sort(orders));
        dataPageable.setPagesize(limit);
        dataPageable.setPagenumber(page);
        Page<AnswerEntity> result = answerService.queryPageByUser(BaseContextHandler.getUserID(),dataPageable);
        // System.out.println(result.getContent().get(0).getLabelList().size());
        return new TableResultResponse<AnswerEntity>(result.getTotalElements(),result.getContent());
    }


    @RequestMapping(value = "/getAnswerData", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<AnswerEntity> getMetaList() {
        Map map=new HashMap<>();
        map.put("success",true);
        List<AnswerEntity> result = answerService.queryAllByUser(BaseContextHandler.getUserID());
        return new TableResultResponse<>(result.size(),result);
    }


    @Autowired
    ServiceResultImpl serviceResult;











    @RequestMapping(value = "/getSimReport/{simId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> getSimReport(@PathVariable("simId") String simId)
    {
        Map map=new HashMap<>();
        map.put("success",true);
        // map.put("data",metaEntity);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @IgnoreUserToken
    @RequestMapping(value = "/getAnswerType", method = RequestMethod.GET, produces = "application/json")
    public String getAnswerType(@RequestParam("answerEntityId") String answerEntityId)
    {
        return answerService.queryAnswerEntityType(answerEntityId);
    }

    @RequestMapping(value = "/getDataStat/{dataId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> getDataStat(@PathVariable("dataId") String dataId)
    {
        Map map=new HashMap<>();
        map.put("success",true);
        // map.put("data",metaEntity);
        AnswerEntity answerEntity = answerService.queryById(dataId);
        AnswerStatRepresentation answerStatRepresentation = new AnswerStatRepresentation();
        if(answerEntity!=null && answerEntity.getAnswerStatRepresentation()!=null){
            answerStatRepresentation = answerEntity.getAnswerStatRepresentation();
            // log.info("this is answer");
        }


//        Set<String> workers = new HashSet<>();
//        Set<String> items = new HashSet<>();
//        Map<String,Integer> classes = new HashMap<>();
//        List<Integer> workerPerTask=new ArrayList<>();
//        List<Integer> taskPerWorker=new ArrayList<>();
//        List<Double> taskConsistency =new ArrayList<>();
//        // log.info("111");
//        if(answerEntity.getDataType().equals("continuous")){
//            answerEntity.getLabelList().forEach(label -> {
//                workers.add(label.getWorker());
//                items.add(label.getItem());
//            });
//
//            items.forEach(item->{
//                List<Label> answerOfItem = answerEntity.getLabelList().stream().filter(ml -> ml.getItem().equals(item)).collect(Collectors.toList());
//                List<Double> answerValue = new ArrayList<>();
//                for(Label label : answerOfItem){
//                    answerValue.add(Double.parseDouble(label.getAnswer()));
//                }
//                double consistency = 0.0;
//                int size = answerOfItem.size();
//
//
//                double mid = 0;
//                Collections.sort(answerValue);
//                if(size%2==0) mid = (answerValue.get(size/2)+answerValue.get(size/2+1))/2; else mid = answerValue.get(size/2);
//
//                for (int i = 0;i<answerValue.size();i++){
//                    consistency += Math.pow((answerValue.get(i)-mid),2);
//                }
//                taskConsistency.add(Math.sqrt(consistency/size));
//                workerPerTask.add(size);
//            });
//            System.out.println(taskConsistency.stream().mapToDouble(x->x).average());
//        }
//        else
//        {
//            // log.info("222");
//            answerEntity.getLabelList().forEach(label -> {
//                workers.add(label.getWorker());
//                items.add(label.getItem());
//                if(!classes.containsKey(label.getAnswer()))
//                    classes.put(label.getAnswer(), classes.size());
//            });
//
//            items.forEach(item->{
//                List<Label> answerOfItem = answerEntity.getLabelList().stream().filter(ml -> ml.getItem().equals(item)).collect(Collectors.toList());
//                int [] count =new int[classes.size()];
//                for(Label label : answerOfItem){
//                    for(String labelClass:classes.keySet()){
//                        if(label.getAnswer().equals(labelClass)){
//                            count[classes.get(labelClass)] ++;
//                            break;
//                        }
//                    }
//                }
//                double consistency = 0.0;
//                int size = answerOfItem.size();
//                for (int i = 0;i<count.length;i++){
//                    if(count[i]>0){
//                        // System.out.print(((double)count[i]/size)+ " ");
//                        // System.out.print(Math.log((double) count[i]/size) / Math.log(size));
//                        consistency = consistency + ((double)count[i]/size)*(Math.log((double)count[i]/size) / Math.log(size));
//                    }
//                }
//                // System.out.println(size);
//                if(consistency!=0)
//                    consistency=-consistency;
//                taskConsistency.add(consistency);
//                workerPerTask.add(size);
//            });
//            // .info("333");
//            answerStatRepresentation.setClassTotal(classes.size());
//        }
//        answerStatRepresentation.setTaskConsistency(taskConsistency);
//        List<Double> quality =new ArrayList<>();
//        workers.forEach(worker->{
//            List<Label> answerOfWorker = answerEntity.getLabelList().stream().filter(ml -> ml.getWorker().equals(worker)).collect(Collectors.toList());
//            int size = answerOfWorker.size();
//            taskPerWorker.add(size);
//            if(answerEntity.getHasGold()){
//                if (answerEntity.getDataType().equals("continuous")){
//                    //List<Double> answerValue = new ArrayList<>();
//                    //List<Double> groundTruth = new ArrayList<>();
//                    double value = 0.0;
//                    for(Label label : answerOfWorker){
//                        // answerValue.add(Double.parseDouble(label.getAnswer()));
//                        //groundTruth.add(Double.parseDouble(answerEntity.getGoldLabels().get(label.getItem())));
//                        double answerValue =Double.parseDouble(label.getAnswer());
//                        double groundTruth = Double.parseDouble(answerEntity.getGoldLabels().get(label.getItem()));
//                        value +=Math.pow(answerValue-groundTruth,2);
//                    }
//                    value = Math.sqrt(value/size);
//                    quality.add(value);
//                }
//                else {
//                    int correct =0;
//                    for(Label label : answerOfWorker){
//                        if(label.getAnswer().equals(answerEntity.getGoldLabels().get(label.getItem())))
//                            correct++;
//                    }
//                    quality.add((double)correct/size);
//                }
//            }
//        });
//
//        answerStatRepresentation.setWorkerQuality(quality);
//        answerStatRepresentation.setTaskPerWorker(taskPerWorker);
//        answerStatRepresentation.setWorkerPerTask(workerPerTask);
//        answerStatRepresentation.setAnswerTotal(answerEntity.getLabelList().size());
//        answerStatRepresentation.setWorkerTotal(workers.size());
//        answerStatRepresentation.setTaskTotal(items.size());
//        answerStatRepresentation.setDataName(answerEntity.getName());
//        answerStatRepresentation.setDataType(answerEntity.getDataType());
//        answerStatRepresentation.setDataId(answerEntity.getId());

        map.put("answerStat",answerStatRepresentation);
        map.put("answerEntity",answerEntity);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @IgnoreUserToken
    @RequestMapping(value = "/filterMetaData", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> filterMetaData(@RequestBody Map<String, Object> params) {
        Map response = new HashMap();
        //MetaEntity metaEntity = metaService.findById((String) params.get("MetaDataId"));
        // List<UnitEntity> result = unitService.findUnitByIdList(Arrays.asList(unitId));
        // String answerEntityId = metaEntity.getAnswerEntityId().get((String) params.get("process_id"));
        String answerEntityId = (String) params.get("answerEntityId");
        AnswerEntity answerEntity = answerService.queryById(answerEntityId);
        List<Label> labelList = answerEntity.getLabelList();

        Map<String,List<String>> itemToResponse = new HashMap<>();
        labelList.forEach(label -> {
            if(!itemToResponse.containsKey(label.getItem())){
                itemToResponse.put(label.getItem(),new ArrayList<>(Arrays.asList(label.getAnswer())));
            }else {
                itemToResponse.get(label.getItem()).add((label.getAnswer()));
            }
        });
        List<String> result = new ArrayList<>();
        itemToResponse.forEach((key,value)->{
            int total=0;
            for(int i =0;i<value.size();i++){
                total+= Integer.parseInt(value.get(i));
            }
            double avg = total/value.size();
            double mae = 0.0;
            for(int i =0;i<value.size();i++){
                mae+= Math.abs(Integer.parseInt(value.get(i))-avg);
            }
            double maeAvg = mae/value.size();
            if(maeAvg>Integer.parseInt((String)params.get("MeanAbsoluteError"))){
                result.add(key);
            }
            System.out.println(maeAvg);
        });



        response.put("UnitDataListId",result);
        response.put("UnitDataListCount",result.size());

        ServiceResultEntity serviceResultEntity = serviceResult.insertServiceResult("FilterMetaData",response);
        response.put("serviceResultId",serviceResultEntity.getId());
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }


    public static Map<String, String> ReadCSV(){
        File csv = new File("D:\\meta2truth.csv");  // CSV文件路径
        Map<String, String> goldLabels = new HashMap<>();
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        try {
            List<String> allString = new ArrayList<>();
            while ((line = br.readLine()) != null)  //读取到的内容给line变量
            {
                everyLine = line;
                String[] ground = everyLine.split(",");
                goldLabels.put(ground[0],ground[1]);
//                System.out.println(everyLine);
//                allString.add(everyLine);
            }
            //System.out.println("csv表格中所有行数："+allString.size());
            br.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return goldLabels;
    }
}
