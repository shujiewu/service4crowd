package cn.edu.buaa.act.mlflow.controller;

import cn.edu.buaa.act.common.msg.BaseResponse;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.mlflow.config.Properties;
import cn.edu.buaa.act.mlflow.domain.ExperimentEntity;
import cn.edu.buaa.act.mlflow.domain.RunInfoEntity;
import cn.edu.buaa.act.mlflow.exception.NoExperimentException;
import cn.edu.buaa.act.mlflow.repository.RunInfoRepository;
import cn.edu.buaa.act.mlflow.service.ExperimentService;
import cn.edu.buaa.act.mlflow.service.RunInfoService;
import cn.edu.buaa.act.mlflow.service.impl.ExecuteService;
import cn.edu.buaa.act.mlflow.service.impl.ExperimentServiceImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.mlflow.api.proto.Service.*;
import org.mlflow.tracking.MlflowClient;
import org.mlflow_project.google.protobuf.Descriptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

/**
 * MlFlowController
 *
 * @author wsj
 * @date 2018/10/9
 */
@RestController
public class MlFlowController {

    private static final Logger logger = LoggerFactory.getLogger(MlFlowController.class);

    @Autowired
    private MlflowClient mlflowClient;

    @Autowired
    private ExperimentService experimentService;

    @Autowired
    private RunInfoService runInfoService;

    @Autowired
    private ExecuteService executeService;

    private static final String SEPARATOR = ":";

    /**
     * @param experimentName
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, path = "/experiments/{experimentName}",produces = "application/json")
    public ObjectRestResponse<ExperimentEntity> createExperiment(@PathVariable String experimentName) {
        Map result =new HashMap();
        ExperimentEntity experimentEntity;
        if((experimentEntity= experimentService.insertByExperimentName(experimentName))!=null){
            result.put("data",experimentEntity);
            result.put("success",true);
        }
        else{
            result.put("success",false);
        }
        return new ObjectRestResponse<>().data(result);
    }

    /**
     * @param experimentName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, path = "/experiments/{experimentName}",produces = "application/json")
    public ObjectRestResponse<ExperimentEntity> getExperiment(@PathVariable String experimentName) {
        return new ObjectRestResponse<>().data(experimentService.findByExperimentName(experimentName));
    }

    /**
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, path = "/experiments/list/all",produces = "application/json")
    public TableResultResponse<ExperimentEntity> listExperiments() {
        List<ExperimentEntity> experimentList = experimentService.findAll();
        return new TableResultResponse<>(experimentList.size(),experimentList);
    }

    /**
     * @param userId
     * @param pageable
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,path = "/experiments/list",produces = "application/json")
    public TableResultResponse<ExperimentEntity> getExperimentPage(@RequestParam String userId,Pageable pageable) {
        Page<ExperimentEntity> experimentEntities = experimentService.findByUserId(userId,pageable);
        return new TableResultResponse<>(experimentEntities.getTotalElements(),experimentEntities.getContent());
    }

    //还没完成
    @RequestMapping(method = RequestMethod.GET,path = "/experiments/{experimentId}/info",produces = "application/json")
    public ObjectRestResponse<GetExperiment.Response> getExperimentInfo(@PathVariable Long experimentId) {
        GetExperiment.Response response = mlflowClient.getExperiment(experimentId);
        return new ObjectRestResponse<>().data(response);
    }

    /**
     * @param experimentId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, path = "/experiments/{experimentId}/runs/create",produces = "application/json")
    public ObjectRestResponse<RunInfoEntity> createRun(@PathVariable Long experimentId) {
        CreateRun createRun = CreateRun.newBuilder().setExperimentId(experimentId)
                .setSourceType(SourceType.PROJECT)
                .setUserId("wsj")
                .setSourceName("file:///home/wsj/mlflow/mlflow/examples/sklearn_elasticnet_wine")
                .setStartTime(System.currentTimeMillis())
                .setEntryPointName("main")
                .setSourceVersion("1")
                .addTags(RunTag.newBuilder().setKey("-P").setValue("alpha=0.7").build())
                .build();
        return new ObjectRestResponse<>().data(runInfoService.createRun(createRun));
    }



    @RequestMapping(method = RequestMethod.POST, path = "/experiments/{registerName}/{version}/runs/{runName}",produces = "application/json")
    public TableResultResponse<RunInfoEntity> createRunByName(@PathVariable String registerName,@PathVariable String version,@PathVariable String runName,@RequestBody Map<String,String> properties) {
        String userId= "1";
        String experimentName = userId+SEPARATOR+registerName+SEPARATOR+version;
        ExperimentEntity experimentEntity = experimentService.findByExperimentName(experimentName);
        if(experimentEntity==null){
            logger.info(experimentName+"not exists");
            experimentEntity=experimentService.insertByExperimentName(experimentName);
        }
        Integer count = properties.get("count") != null ? Integer.valueOf(properties.get("count")) :1;
        List<RunInfoEntity> runInfoEntityList = new ArrayList<>(count);

        List<Map<String,Object>> propertyList = new ArrayList<>(count);
        System.out.println(properties.get("property"));
        if(count>1){
            String property = properties.get("property");
            JSONArray jsonArray = JSONArray.parseArray(property);
            for(int i = 0;i<jsonArray.size();i++){
                propertyList.add((Map)jsonArray.get(i));
            }
        }
        else {
            propertyList.add((Map)JSONObject.parseObject(properties.get("property")));
        }

        for(int i=1;i<=count;i++){
            CreateRun createRun = CreateRun.newBuilder().setExperimentId(experimentEntity.getExperimentId())
                    .setRunName(runName+"-"+i)
                    .setSourceType(SourceType.PROJECT)
                    .setUserId("wsj")
                    .setSourceName("file:///home/wsj/mlflow/mlflow/examples/sklearn_elasticnet_wine")
                    .setStartTime(System.currentTimeMillis())
                    .setEntryPointName("main")
                    .setSourceVersion(registerName+SEPARATOR+version)
                    //.addTags(RunTag.newBuilder().setKey("-P").setValue("alpha=0.7").build())
                    .build();
            RunInfoEntity runInfoEntity = runInfoService.createRunWithRunName(createRun,runName+"-"+i);
            runInfoEntityList.add(runInfoEntity);
            Map<String,String> property = new HashMap<>();
            property.put(Properties.EXPERIMENT_ID,String.valueOf(experimentEntity.getExperimentId()));
            property.put(Properties.RUN_ID,runInfoEntity.getRunUuid());
            property.put(Properties.PARAM,propertyList.get(i-1).get(Properties.PARAM).toString());
            property.put(Properties.ALGORITHM_PATH,"/ALGORITHM/"+registerName+"/"+version);//
            try {
                executeService.executeUploadShell(property).thenApply((path) -> {
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
            }
        }
        return new TableResultResponse<>(runInfoEntityList.size(),runInfoEntityList);
    }

    /**
     * @param runUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, path = "/runs/{runUuid}/terminate",produces = "application/json")
    public ObjectRestResponse<RunInfoEntity> terminateRun(@PathVariable String runUuid) {
        return new ObjectRestResponse<>().data(runInfoService.terminateRun(runUuid));
    }

    /**
     * @param parameter
     * @param runUuid
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, path = "/runs/{runUuid}/parameter",produces = "application/json")
    public BaseResponse setParameter(@RequestBody Map<String,String> parameter, @PathVariable String runUuid) {
        parameter.forEach((key,value)->{
            mlflowClient.logParam(runUuid, key, value);
        });
        return new BaseResponse();
    }


    /**
     * @param experimentId
     * @param pageable
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, path = "/experiments/{experimentId}/runs/list",produces = "application/json")
    public  TableResultResponse<RunInfoEntity> listRunInfos(@PathVariable Long experimentId,Pageable pageable) {
        List<RunInfoEntity> runInfoEntityList = runInfoService.findByExperimentId(experimentId);
        return new TableResultResponse<>(runInfoEntityList.size(),runInfoEntityList);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/runs/list",produces = "application/json")
    public  TableResultResponse<RunInfoEntity> listAllRunInfo(Pageable pageable,@RequestParam(required = false) String runName, @RequestParam(required = false) String sourceVersion) {
        if(runName==null&&sourceVersion==null){
            Page<RunInfoEntity> runInfoEntityList = runInfoService.findAll(pageable);
            return new TableResultResponse<>(runInfoEntityList.getTotalElements(),runInfoEntityList.getContent());
        }
        else if(runName!=null&&sourceVersion!=null){
            Page<RunInfoEntity> runInfoEntityList = this.runInfoService.findByNameAndSourceVersion(pageable,runName,sourceVersion);
            return new TableResultResponse<>(runInfoEntityList.getTotalElements(),runInfoEntityList.getContent());
        }
        else if(runName!=null){
            Page<RunInfoEntity> runInfoEntityList = this.runInfoService.findByName(pageable,runName);
            return new TableResultResponse<>(runInfoEntityList.getTotalElements(),runInfoEntityList.getContent());
        }
        else {
            Page<RunInfoEntity> runInfoEntityList = this.runInfoService.findBySourceVersion(pageable,sourceVersion);
            return new TableResultResponse<>(runInfoEntityList.getTotalElements(),runInfoEntityList.getContent());
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/runs/refresh",produces = "application/json")
    public  TableResultResponse<RunInfoEntity> refreshAllRunInfo(Pageable pageable){
        Page<RunInfoEntity> runInfoEntityList = runInfoService.refresh(pageable);
        return new TableResultResponse<>(runInfoEntityList.getTotalElements(),runInfoEntityList.getContent());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/runs/{runUuid}",produces = "application/json")
    public Run getRun(@PathVariable String runUuid) {
        Run runInfo = mlflowClient.getRun(runUuid);
        return runInfo;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/runs/{runUuid}/artifacts",produces = "application/json")
    public  ObjectRestResponse<Object> getArtifacts(@PathVariable String runUuid) {
        File runInfo = mlflowClient.downloadArtifacts(runUuid);
        return new ObjectRestResponse<>();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/runs/{runUuid}/artifacts/list",produces = "application/json")
    public  ObjectRestResponse<Object> listArtifacts(@PathVariable String runUuid) {
        List<FileInfo> fileInfos= mlflowClient.listArtifacts(runUuid);
        fileInfos.stream().forEach(fileInfo -> {
            System.out.println(fileInfo);
        });
        return new ObjectRestResponse<>();
    }
}
