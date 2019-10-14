package cn.edu.buaa.act.mlflow.util;

import cn.edu.buaa.act.mlflow.config.FTPConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static cn.edu.buaa.act.mlflow.config.Constants.DATA_PATH;
import static cn.edu.buaa.act.mlflow.config.Constants.STORE_PATH;

@Component
public class FileManagerByFtp {
    @Value("${ftp.host:192.168.3.188}")
    private String host;
    @Value("${ftp.port:21}")
    private int port;
    @Value("${ftp.userName:wsj}")
    private String userName;
    @Value("${ftp.password:shujie1127}")
    private String password;
    /** 
     * FTP上传单个文件测试 
     */  
    public static void fileUploadByFtp() {  
        FTPClient ftpClient = new FTPClient();  
        FileInputStream fis = null;  
  
        try {  
            ftpClient.connect("192.168.3.188");
            ftpClient.login("wsj", "shujie1127");
  
            File srcFile = new File("G:\\微信图片_20180301200837.jpg");
            fis = new FileInputStream(srcFile);  
            // 设置上传目录  
            ftpClient.changeWorkingDirectory("/home/wsj/");
            ftpClient.setBufferSize(1024);  
            ftpClient.setControlEncoding("GBK");  
            // 设置文件类型（二进制）  
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.storeFile("微信图片_20180301200837.jpg", fis);
        } catch (IOException e) {  
            e.printStackTrace();  
            throw new RuntimeException("FTP客户端出错！", e);  
        } finally {  
            IOUtils.closeQuietly(fis);  
            try {  
                ftpClient.disconnect();  
            } catch (IOException e) {  
                e.printStackTrace();  
                throw new RuntimeException("关闭FTP连接发生异常！", e);  
            }  
        }  
    }

    public Map<String,String> uploadMutiDataFile(Map<String,String> originPath,String taskId) {
        boolean success = false;
        Map<String,String> resultPath = new HashMap<>();
        FTPClient ftp = new FTPClient();
        if(originPath.size()==0){
            return resultPath;
        }
        try {
            int reply;
            ftp.connect(host,port);//连接FTP服务器
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(userName,password);//登录
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return new HashMap<>();
            }
            originPath.forEach((type,path)->{
                FileInputStream stream = null;
                File file = new File(path);
                try {
                    stream = new FileInputStream(file);
                    //这里path适应了data和service
                    String newPath =DATA_PATH+"/"+taskId +"/"+ file.getName();
                    if(!ftp.changeWorkingDirectory(newPath)){
                        ftp.changeWorkingDirectory(DATA_PATH);
                        ftp.makeDirectory(taskId);
                        ftp.changeWorkingDirectory(taskId);
                    }
                    ftp.enterLocalPassiveMode();
                    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                    if(ftp.storeFile(new String(file.getName().getBytes("UTF-8"),"iso-8859-1"), stream)){
                        resultPath.put(type,newPath);
                    }
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return resultPath;
    }


    public boolean uploadDataFile(String path, String filename, InputStream input) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host,port);//连接FTP服务器
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(userName,password);//登录
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }

            //这里path适应了data和service
            if(!ftp.changeWorkingDirectory(DATA_PATH+path)){
                ftp.changeWorkingDirectory(DATA_PATH);
                String[] pah = path.split("/");
                // 分层创建目录
                for (String pa : pah) {
                    ftp.makeDirectory(pa);
                    // 切到到对应目录
                    ftp.changeWorkingDirectory(pa);
                }
                // ftp.changeWorkingDirectory(path);
            }
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            if(ftp.storeFile(new String(filename.getBytes("UTF-8"),"iso-8859-1"), input)){
                success = true;
            }
            input.close();
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }
    /**
     * Description: 向FTP服务器上传文件
     * @param path FTP服务器保存目录
     * @param filename 上传到FTP服务器上的文件名
     * @param input 输入流
     * @return 成功返回true，否则返回false
     */
    public boolean uploadFile(String path, String filename, InputStream input) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host,port);//连接FTP服务器
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(userName,password);//登录
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }

            //这里path适应了data和service
            if(!ftp.changeWorkingDirectory(path)){
                ftp.changeWorkingDirectory(STORE_PATH);
                String[] pah = path.split("/");
                // 分层创建目录
                for (String pa : pah) {
                    ftp.makeDirectory(pa);
                    // 切到到对应目录
                    ftp.changeWorkingDirectory(pa);
                }
                // ftp.changeWorkingDirectory(path);
            }
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            if(ftp.storeFile(new String(filename.getBytes("UTF-8"),"iso-8859-1"), input)){
                success = true;
            }
            input.close();
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }
  
    /** 
     * FTP下载单个文件测试 
     */  
    public void fileDownloadByFtp() {
        FTPClient ftpClient = new FTPClient();  
        FileOutputStream fos = null;
        try {  
            ftpClient.connect("192.85.1.9");  
            ftpClient.login("zhangzhenmin", "62672000");
            String remoteFileName = "/home/zhangzhenmin/test_back_081901.sql";  
            // fos = new FileOutputStream("E:/test/test_back_081901.sql");  
            fos = new FileOutputStream("H:/test/test_back_081901.sql");  
            ftpClient.setBufferSize(1024);  
            // 设置文件类型（二进制）  
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            Boolean code = ftpClient.retrieveFile(remoteFileName, fos);
            System.out.println(code);
        } catch (IOException e) {  
            e.printStackTrace();  
            throw new RuntimeException("FTP客户端出错！", e);  
        } finally {  
            IOUtils.closeQuietly(fos);  
            try {  
                ftpClient.disconnect();  
            } catch (IOException e) {  
                e.printStackTrace();  
                throw new RuntimeException("关闭FTP连接发生异常！", e);  
            }  
        }  
    }  
  
//    public static void main(String[] args) {
//        try {
//            FileInputStream in=new FileInputStream(new File("G:\\test.txt"));
//            boolean flag = uploadFile("192.168.3.188", 21, "wsj", "shujie1127", "/home/wsj/mlflowshell/version1/vv", "test.txt", in);
//            System.out.println(flag);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        //fileDownloadByFtp();
//    }
}  