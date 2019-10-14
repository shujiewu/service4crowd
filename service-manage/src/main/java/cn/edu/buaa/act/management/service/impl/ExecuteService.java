package cn.edu.buaa.act.management.service.impl;

import cn.edu.buaa.act.management.util.FileManagerByFtp;
import cn.edu.buaa.act.management.util.RemoteShellExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    @Value("${ftp.dest:/home/wsj/service4crowd/service}")
    private String dictionary;


    private static final Logger logger = LoggerFactory.getLogger(ExecuteService.class);
    public Map<String,Object> executeUploadMetaData(String dest, String fileName,InputStream inputStream,Boolean force, Boolean unzip) throws InterruptedException {
        logger.info(Thread.currentThread().getName() + "executeUploadShell");
        Map<String,Object> result= fileManagerByFtp.uploadFile(dest, fileName,inputStream,force);
        if((Boolean) result.get("success")&&unzip){
            RemoteShellExecutor executor = new RemoteShellExecutor("192.168.3.188", "wsj", "shujie1127");
            Integer statusCode = -1;
            try {
                statusCode= executor.exec("unzip -o -d"+dictionary+dest+" "+dictionary+dest+fileName);
                if(statusCode!=0){
                    result.put("success",false);
                    result.put("message","解压失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String config = fileManagerByFtp.fileDownloadByFtp(dest,"config.json");
            if(config!=null){
                result.put("configuration",config);
            }
            //读取config,返回配置文件
        }
        return result;
    }
//    @Async("asyncExecutor1")
//    public CompletableFuture<String> executeAlgorithm(String shPath) throws InterruptedException {
//        logger.info(Thread.currentThread().getName() + " executeAlgorithm");
//        RemoteShellExecutor executor = new RemoteShellExecutor("192.168.3.188", "wsj", "shujie1127");
//        Integer statusCode = -1;
//        try {
//            statusCode= executor.exec("sh /home/wsj/service4crowd/service"+shPath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return CompletableFuture.completedFuture(shPath.substring(shPath.lastIndexOf("/")+1,shPath.lastIndexOf(".sh")));
//    }
}
