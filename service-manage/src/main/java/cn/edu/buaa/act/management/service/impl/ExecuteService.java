package cn.edu.buaa.act.management.service.impl;

import cn.edu.buaa.act.management.util.FileManagerByFtp;
import cn.edu.buaa.act.management.util.RemoteShellExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
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
    @Value("${storage.dest:/home/LAB/wusj/service4crowd/service}")
    private String dictionary;


    private static final Logger logger = LoggerFactory.getLogger(ExecuteService.class);


    public static void writeToLocal(String dictionary, String fileName, InputStream input) throws IOException {
        mkdirs(dictionary);
        String destination = dictionary + new String(fileName.getBytes("UTF-8"),"iso-8859-1");
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        input.close();
        downloadFile.close();
    }

    public static void mkdirs(String dictionary){
        File dir = new File(dictionary);
        if (!dir.exists()) {// 判断目录是否存在
            //dir.mkdir();
            dir.mkdirs();  //多层目录需要调用mkdirs
        }
    }


    public static void execCommand(String cmd) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmd,null,null);
        InputStream stderr =  proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stderr,"GBK");
        BufferedReader br = new BufferedReader(isr);
        String line="";
        logger.info("exec command");
        while ((line = br.readLine()) != null) {
            logger.info(line);
        }
    }

    public static String readFileByBytes(String fileName) throws IOException {
        File file = new File(fileName);
        InputStream in = null;
        StringBuilder sb = new StringBuilder();
        if (file.isFile() && file.exists()) { //判断文件是否存在
            byte[] tempbytes = new byte[1024];
            int byteread = 0;
            in = new FileInputStream(file);
            while ((byteread = in.read(tempbytes)) != -1) {
                String str = new String(tempbytes, 0, byteread);
                sb.append(str);
            }
            return sb.toString();
        } else {
            logger.info("找不到指定的文件，请确认文件路径是否正确");
            return null;
        }
    }

    public Map<String,Object> executeUploadMetaData(String dest, String fileName,InputStream inputStream,Boolean force, Boolean unzip) throws InterruptedException {
        logger.info(Thread.currentThread().getName() + "executeUploadShell");
        Map<String,Object> result= new HashMap<>();
        try {
            writeToLocal(dictionary+dest,fileName,inputStream);
            if(unzip){
                execCommand("unzip -o -d"+dictionary+dest+" "+dictionary+dest+fileName);
                result.put("success",true);
                String config = readFileByBytes(dictionary+dest+"config.json");
                if(config!=null){
                    result.put("configuration",config);
                }
            }else{
                result.put("success",true);
            }
        } catch (IOException e) {
            result.put("success",false);
            e.printStackTrace();
        }
        return result;
    }

    public Map<String,Object> executeUploadMetaDataByFTP(String dest, String fileName,InputStream inputStream,Boolean force, Boolean unzip) throws InterruptedException {
        logger.info(Thread.currentThread().getName() + "executeUploadShell");
        Map<String,Object> result= fileManagerByFtp.uploadFile(dest, fileName,inputStream,force);
        if((Boolean) result.get("success")&&unzip){
            RemoteShellExecutor executor = new RemoteShellExecutor("192.168.3.117", "wsj", "shujie1127");
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
