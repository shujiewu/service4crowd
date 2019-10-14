package cn.edu.buaa.act.mlflow.service.impl;

import cn.edu.buaa.act.mlflow.config.Properties;
import cn.edu.buaa.act.mlflow.controller.MlFlowController;
import cn.edu.buaa.act.mlflow.util.FileManagerByFtp;
import cn.edu.buaa.act.mlflow.util.RemoteShellExecutor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static cn.edu.buaa.act.mlflow.config.Constants.DATA_PATH;
import static cn.edu.buaa.act.mlflow.config.Constants.STORE_PATH;

/**
 * ExecuteService
 *
 * @author wsj
 * @date 2018/10/14
 */
@Service
public class ExecuteService {

    @Autowired
    private FileManagerByFtp fileManagerByFtp;

    private static final Logger logger = LoggerFactory.getLogger(ExecuteService.class);


    @Async("asyncExecutor1")
    public CompletableFuture<Map> executeUploadDataFile(Map<String,String> path,String taskId) throws InterruptedException{
        return CompletableFuture.completedFuture(fileManagerByFtp.uploadMutiDataFile(path,taskId));
    }

    @Async("asyncExecutor1")
    public CompletableFuture<String> executeUploadAlgorithmShell(Map<String,Object> properties) throws InterruptedException {
        logger.info(Thread.currentThread().getName() + "executeUploadShell");
        String experimentId = properties.get(Properties.EXPERIMENT_ID).toString();
        String runId = properties.get(Properties.RUN_ID).toString();

        Map<String,Object> servicePara = (Map)properties.get(Properties.PARAM);
        String path = properties.get(Properties.ALGORITHM_PATH).toString();
        StringBuffer stringBuffer =new StringBuffer();
        stringBuffer.append("export PATH=/home/wsj/anaconda2/bin:$PATH:\n");
        stringBuffer.append("mlflow run "+STORE_PATH+path);
        stringBuffer.append(" ");
        stringBuffer.append("--experiment-id ");
        stringBuffer.append(Integer.valueOf(experimentId));
        stringBuffer.append(" ");
        stringBuffer.append("--run-id ");
        stringBuffer.append(runId);
        stringBuffer.append(" ");
        stringBuffer.append("-P ");
        stringBuffer.append("taskid=");
        stringBuffer.append(servicePara.get("taskId"));
        servicePara.remove("taskId");
        stringBuffer.append(" ");
        stringBuffer.append("-P ");
        stringBuffer.append("method=");
        stringBuffer.append(properties.get(Properties.ALGORITHM_METHOD));
        stringBuffer.append(" ");
        stringBuffer.append("-P ");
        stringBuffer.append("command=");
        StringBuilder command = new StringBuilder();
        command.append("\"");
        command.append("python method.py");
        servicePara.forEach((key,value)->{
            if(key.equals(Properties.FILE_PATH)){
                Map<String,String> file = (Map)value;
                file.forEach((name,serverPath)->{
                    command.append(" ");
                    command.append("--");
                    command.append(name);
                    command.append("=");
                    command.append(serverPath);
                });
            }
            else {
                command.append(" ");
                command.append("--");
                command.append(key);
                command.append("=");
                command.append(value);
            }
        });
        command.append("\"");
        stringBuffer.append(command);



        System.out.println(stringBuffer.toString());
        ByteArrayInputStream stream = new ByteArrayInputStream(stringBuffer.toString().getBytes());
        boolean flag = fileManagerByFtp.uploadFile(path, runId+".sh", stream);/* /home/wsj/service4crowd/service */
//        RemoteShellExecutor executor = new RemoteShellExecutor("192.168.3.188", "wsj", "shujie1127");
//        try {
//            executor.exec("sh /home/wsj/service4crowd/service);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return CompletableFuture.completedFuture(path+"/"+runId+".sh");
    }



    @Async("asyncExecutor1")
    public CompletableFuture<String> executeUploadShell(Map<String,String> properties) throws InterruptedException {
        logger.info(Thread.currentThread().getName() + "executeUploadShell");
        String experimentId = properties.get(Properties.EXPERIMENT_ID);
        String runId = properties.get(Properties.RUN_ID);
        String param = properties.get(Properties.PARAM);
        String path = properties.get(Properties.ALGORITHM_PATH);
        Map<String,Object> paramMap = new HashMap<>();
        if(StringUtils.hasText(param)){
            JSONArray jsonArray = JSONArray.parseArray(param);
            for (Object aJsonArray : jsonArray) {
                JSONObject jsonObject =JSONObject.parseObject(aJsonArray.toString());
//                String[] strings = ((String) aJsonArray).split(":", 2);
//                Assert.isTrue(strings.length == 2, "Invalid environment variable declared: " + (String) aJsonArray);
                //paramMap.put(strings[0],strings[1]);
                jsonObject.forEach((key,value)->{
                    paramMap.put(key,value);
                });
            }
        }
        StringBuffer stringBuffer =new StringBuffer();
        stringBuffer.append("export PATH=/home/wsj/anaconda2/bin:$PATH:\n");
        stringBuffer.append("mlflow run "+STORE_PATH+path);
        stringBuffer.append("--experiment-id ");
        stringBuffer.append(Integer.valueOf(experimentId));
        stringBuffer.append(" ");
        stringBuffer.append("--run-id ");
        stringBuffer.append(runId);
        stringBuffer.append(" ");
        paramMap.forEach((key,value)->{
            stringBuffer.append("-P ");
            stringBuffer.append(key+"="+value+" ");
        });
        System.out.println(stringBuffer.toString());
        ByteArrayInputStream stream = new ByteArrayInputStream(stringBuffer.toString().getBytes());
        boolean flag = fileManagerByFtp.uploadFile(path, runId+".sh", stream);/* /home/wsj/service4crowd/service */
//        RemoteShellExecutor executor = new RemoteShellExecutor("192.168.3.188", "wsj", "shujie1127");
//        try {
//            executor.exec("sh /home/wsj/service4crowd/service);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return CompletableFuture.completedFuture(path+"/"+runId+".sh");
    }

    @Async("asyncExecutor1")
    public CompletableFuture<String> executeAlgorithm(String shPath) throws InterruptedException {
        logger.info(Thread.currentThread().getName() + " executeAlgorithm");
        RemoteShellExecutor executor = new RemoteShellExecutor("192.168.3.188", "wsj", "shujie1127");
        Integer statusCode = -1;
        try {
            statusCode= executor.exec("sh " +STORE_PATH+shPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(shPath.substring(shPath.lastIndexOf("/")+1,shPath.lastIndexOf(".sh")));
    }



    @Async("asyncExecutor1")
    public CompletableFuture<String> executeUploadConfig(Map<String,String> properties) throws InterruptedException {
        logger.info(Thread.currentThread().getName() + "executeUploadConfig");
        String experimentId = properties.get(Properties.EXPERIMENT_ID);
        String runId = properties.get(Properties.RUN_ID);
        String param = properties.get(Properties.PARAM);
        String path = properties.get(Properties.ALGORITHM_PATH);
        Map<String,Object> paramMap = new HashMap<>();
        System.out.println(param);
        if(StringUtils.hasText(param)){
            JSONArray jsonArray = JSONArray.parseArray(param);
            for (Object aJsonArray : jsonArray) {
                JSONObject jsonObject =JSONObject.parseObject(aJsonArray.toString());
                jsonObject.forEach((key,value)->{
                    paramMap.put(key,value);
                });
            }
        }
        StringBuffer stringBuffer =new StringBuffer();
        stringBuffer.append("export PATH=/home/wsj/anaconda2/bin:$PATH:\n");
        stringBuffer.append("mlflow run "+STORE_PATH+path);
        stringBuffer.append("--experiment-id ");
        stringBuffer.append(Integer.valueOf(experimentId));
        stringBuffer.append(" ");
        stringBuffer.append("--run-id ");
        stringBuffer.append(runId);
        stringBuffer.append(" ");
        paramMap.forEach((key,value)->{
            stringBuffer.append("-P ");
            stringBuffer.append(key+"="+value+" ");
        });
        ByteArrayInputStream stream = new ByteArrayInputStream(stringBuffer.toString().getBytes());
        boolean flag = fileManagerByFtp.uploadFile(path, runId+".sh", stream);/* /home/wsj/service4crowd/service */
        return CompletableFuture.completedFuture(path+"/"+runId+".sh");
    }
}
