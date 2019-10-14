package cn.edu.buaa.act.mlflow.controller;

import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.mlflow.config.Properties;
import cn.edu.buaa.act.mlflow.domain.ExperimentEntity;
import cn.edu.buaa.act.mlflow.domain.RunInfoEntity;
import cn.edu.buaa.act.mlflow.model.Label;
import cn.edu.buaa.act.mlflow.service.ExperimentService;
import cn.edu.buaa.act.mlflow.service.RunInfoService;
import cn.edu.buaa.act.mlflow.service.impl.ExecuteService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import lombok.extern.slf4j.Slf4j;
import org.mlflow.api.proto.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.mlflow.api.proto.Service.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.edu.buaa.act.mlflow.config.Properties.ANSWER_FILE_PATH;

/**
 * AlgorithmController
 *
 * @author wsj
 * @date 2018/10/22
 */

@Slf4j
@RestController
@RequestMapping(value = "/algorithm")
public class AlgorithmController {
    private static final Logger logger = LoggerFactory.getLogger(AlgorithmController.class);
    private static final String SEPARATOR = ":";
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Map> test(@RequestParam("name") String name, @RequestParam("type") String value) {
        Map<String, String> test = new HashMap<>();
        test.put("name", name);
        test.put("er", value);
        return new ResponseEntity<Map>(test, HttpStatus.CREATED);
    }


    @Autowired
    private ExperimentService experimentService;

    @Autowired
    private RunInfoService runInfoService;

    @Autowired
    private ExecuteService executeService;

    public void createRun(String processInstanceId,String taskId, String serviceName, String version,Map<String,Object> serviceProperties){
        String userId= "1";
        String experimentName = userId+SEPARATOR+processInstanceId;
        ExperimentEntity experimentEntity = experimentService.findByExperimentName(experimentName);
        if(experimentEntity==null){
            logger.info(experimentName+"not exists");
            experimentEntity=experimentService.insertByExperimentName(experimentName);
        }
        CreateRun createRun = CreateRun.newBuilder().setExperimentId(experimentEntity.getExperimentId())
                .setRunName(taskId+"-"+serviceName)
                .setSourceType(Service.SourceType.PROJECT)
                .setUserId(userId)
                .setStartTime(System.currentTimeMillis())
                .setEntryPointName("main")
                .setSourceVersion(serviceName+SEPARATOR+version)
                .build();
        RunInfoEntity runInfoEntity = runInfoService.createRunWithRunName(createRun,taskId+"-"+serviceName);

        //内部属性
        Map<String,Object> property = new HashMap<>();
        property.put(Properties.EXPERIMENT_ID,String.valueOf(experimentEntity.getExperimentId()));
        property.put(Properties.RUN_ID,runInfoEntity.getRunUuid());
        property.put(Properties.ALGORITHM_PATH,"/ALGORITHM/"+serviceName+"/"+version);//
        property.put(Properties.ALGORITHM_METHOD,serviceName+SEPARATOR+version);
        try {
            executeService.executeUploadDataFile((Map)serviceProperties.get(Properties.FILE_PATH),taskId).thenApply((serverPath)->{
                try {
                    serviceProperties.put(Properties.FILE_PATH,serverPath);
                    property.put(Properties.PARAM,serviceProperties);
                    return  executeService.executeUploadAlgorithmShell(property).thenApply((path) -> {
                          try {
                              return executeService.executeAlgorithm(path).thenAccept(t->{
                                  runInfoService.update(t);
                              });
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                              return null;
                          }
                      });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // public String writeCSV()



    //对于写配置文件的人来说需要写多个
    @RequestMapping(value = "/{algorithmName}/{version}", method = RequestMethod.POST)
    public ResponseEntity<Map> executeAlgorithm(@PathVariable String algorithmName, @PathVariable("version") String version,@RequestBody MultiValueMap<String,Object> body) {
        Map<String, Object> varMap = body.toSingleValueMap();
        Map<String, Object> result = new HashMap<>();
        logger.info(algorithmName + version);



        String processInstanceId = varMap.get("processInstanceId").toString();
        String taskId = varMap.get("taskId").toString();


        Map<String,String> filePathMap = new HashMap<>();

        File rootPath =  new File("D://servicedata/temp/"+taskId+"/");

        if(varMap.containsKey("answerData")){
            if(!rootPath.exists()) {
                rootPath.mkdirs();
            }
            Object answerData= varMap.get("answerData");
            if(answerData!=null&& StringUtils.hasText(answerData.toString())){
                JSONArray answerDataList = JSONArray.parseArray(answerData.toString());
                String csvFilePath = rootPath.getAbsolutePath()+"/answerData.csv";
                CsvWriter csvWriter = new CsvWriter(csvFilePath, ',', Charset.forName("UTF-8"));
                try {
                    String[] headers = new String[]{"question","worker","answer"};
                    csvWriter.writeRecord(headers);
                    List<Label> labelList = answerDataList.toJavaList(Label.class);
                    labelList.forEach(label -> {
                        try {
                            csvWriter.writeRecord(new String[]{label.getItem(),label.getWorker(),label.getAnswer()});
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    csvWriter.close();
                    log.info("--------ANSWER DATA CSV文件已经写入--------");
                    filePathMap.put("answerdata",csvFilePath);
                    varMap.remove("answerData");
                } catch (IOException e) {
                    csvWriter.close();
                    e.printStackTrace();
                }
            }
        }

        if(varMap.containsKey("truthData")){
            if(!rootPath.exists()) {
                rootPath.mkdirs();
            }
            Object truthData= varMap.get("truthData");
            if(truthData!=null&& StringUtils.hasText(truthData.toString())){
                JSONObject truthDataList = JSONObject.parseObject(truthData.toString());
                String csvFilePath = rootPath.getAbsolutePath()+"/truthData.csv";
                CsvWriter csvWriter = new CsvWriter(csvFilePath, ',', Charset.forName("UTF-8"));
                try {
                    String[] headers = new String[]{"question","truth"};
                    csvWriter.writeRecord(headers);

                    truthDataList.forEach((key,value)->{
                        try {
                            csvWriter.writeRecord(new String[]{key,value.toString()});
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    csvWriter.close();
                    log.info("--------TRUTH DATA CSV文件已经写入--------");
                    filePathMap.put("truthdata",csvFilePath);
                    varMap.remove("truthData");
                } catch (IOException e) {
                    csvWriter.close();
                    e.printStackTrace();
                }
            }
        }
        if(varMap.containsKey("state")){
            filePathMap.put("state",varMap.get("state").toString());
            System.out.println(varMap.get("state"));
            varMap.remove("state");
        }

//        if(varMap.containsKey("state")){
//            if(!rootPath.exists()) {
//                rootPath.mkdirs();
//            }
//            Object truthData= varMap.get("state");
//            try {
//
//                File file = new File(rootPath.getAbsolutePath()+"/state.txt");
//                // if file doesnt exists, then create it
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
//                FileWriter fw = new FileWriter(file.getAbsoluteFile());
//                BufferedWriter bw = new BufferedWriter(fw);
//                bw.write(JSON.toJSONString(truthData));
//                bw.close();
//                filePathMap.put("state",file.getAbsolutePath());
//                varMap.remove("state");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        // varMap.remove("taskId");
        varMap.remove("serviceName");
        varMap.remove("processInstanceId");
        varMap.remove("algorithmName");
        varMap.remove("version");
        varMap.remove("userId");
        //也许是多个文件，如真值和回答值

        //加入上传的文件
        varMap.put(Properties.FILE_PATH,filePathMap);
        createRun(processInstanceId,taskId,algorithmName,version,varMap);
        result.put("success", true);
        result.put("algorithmName", algorithmName);
        return new ResponseEntity<Map>(result, HttpStatus.CREATED);
    }






//    //对于写配置文件的人来说需要写多个
//    @RequestMapping(value = "/{algorithmName}/{version}/1", method = RequestMethod.POST)
//    public ResponseEntity<Map> executeAlgorithm2(@PathVariable String algorithmName, @PathVariable("version") String version,@RequestBody MultiValueMap<String,Object> body) {
//        Map<String, Object> varMap = body.toSingleValueMap();
//        Map<String, Object> result = new HashMap<>();
//        logger.info(algorithmName + version);
//
//
//
//        String processInstanceId = varMap.get("processInstanceId").toString();
//        String taskId = varMap.get("taskId").toString();
//
//        JSONArray unitDataList = JSONArray.parseArray(varMap.get("UnitDataList").toString());
//
//
//
//
//        // varMap.remove("taskId");
//        varMap.remove("serviceName");
//        varMap.remove("processInstanceId");
//        varMap.remove("UnitDataList");
//        varMap.remove("algorithmName");
//        varMap.remove("version");
//        varMap.remove("userId");
//        //也许是多个文件，如真值和回答值
//
//
//
//        String csvFilePath = "D://servicedata/temp/"+taskId+".csv";
//        CsvWriter csvWriter = new CsvWriter(csvFilePath, ',', Charset.forName("UTF-8"));
//        try {
//            String[] headers = new String[]{"question","worker","answer"};
//            csvWriter.writeRecord(headers);
//            for(int i = 0; i<unitDataList.size();i++){
//                JSONObject unit = unitDataList.getJSONObject(i);
//                JSONObject labelMap = unit.getJSONObject("labelMap");
//                labelMap.forEach((key,value)->{
//                    List<Label> labelList = labelMap.getJSONArray(key).toJavaList(Label.class);
//                    labelList.forEach(label -> {
//                        try {
//                            csvWriter.writeRecord(new String[]{label.getItem(),label.getWorker(),label.getAnswer()});
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                });
//            }
//            csvWriter.close();
//            log.info("--------CSV文件已经写入--------");
//        } catch (IOException e) {
//            csvWriter.close();
//            e.printStackTrace();
//        }
//        //加入上传的文件
//        varMap.put(ANSWER_FILE_PATH,csvFilePath);
//        createRun(processInstanceId,taskId,algorithmName,version,varMap);
//        result.put("success", true);
//        result.put("algorithmName", algorithmName);
//        return new ResponseEntity<Map>(result, HttpStatus.CREATED);
//    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Map> test3(@RequestBody Map<String, Object> jsonObject) {
        Map<String, String> test = new HashMap<>();
        // test.put("name",(String) BODY.ge("test"));
        return new ResponseEntity<Map>(test, HttpStatus.CREATED);
    }
}
