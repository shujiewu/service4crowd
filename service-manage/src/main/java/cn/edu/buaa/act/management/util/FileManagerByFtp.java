package cn.edu.buaa.act.management.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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

    @Value("${ftp.dest:/home/wsj/service4crowd/service}")
    private String dictionary;
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

    /**
     * Description: 向FTP服务器上传文件
     * @param path FTP服务器保存目录
     * @param filename 上传到FTP服务器上的文件名
     * @param input 输入流
     * @return 成功返回true，否则返回false
     */
    public Map<String,Object> uploadFile(String path, String filename, InputStream input, Boolean force) {
        Map<String,Object> result = new HashMap<>();
        boolean success = false;
        result.put("success",success);
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host,port);//连接FTP服务器
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(userName,password);//登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                result.put("message","连接失败");
                return result;
            }
            //不是强制创建并且已经存在
            if(ftp.changeWorkingDirectory(path)&&!force){
                result.put("message","文件已经存在");
                return result;
            }
            if(!ftp.changeWorkingDirectory(path)){
                ftp.changeWorkingDirectory(dictionary);
                String[] pah = path.split("/");
                // 分层创建目录
                for (String pa : pah) {
                    // System.out.println(pa);
                    //不能是中文目录
                    ftp.makeDirectory(pa);
                    // 切到到对应目录
                    ftp.changeWorkingDirectory(pa);
                }
                // ftp.changeWorkingDirectory(path);
            }
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            if(ftp.storeFile(new String(filename.getBytes("UTF-8"),"iso-8859-1"), input)){
                result.put("success",true);
                result.put("path",dictionary+path);
                result.put("message","存储成功");
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
        return result;
    }
  
    /** 
     * FTP下载单个文件测试 
     */  
    public String fileDownloadByFtp(String path, String filename) {
        System.out.println(111);
        FTPClient ftpClient = new FTPClient();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ftpClient.connect(host,port);
            ftpClient.login(userName,password);

//            ftpClient.connect("192.168.3.188", 21);
//            ftpClient.login("wsj", "shujie1127");
            // fos = new FileOutputStream("E:/test/test_back_081901.sql");  
            baos = new ByteArrayOutputStream();
            ftpClient.setBufferSize(1024);

            if(!ftpClient.changeWorkingDirectory(path)){
                ftpClient.changeWorkingDirectory(dictionary);
                String[] pah = path.split("/");
                for (String pa : pah) {
                    ftpClient.changeWorkingDirectory(pa);
                }
                // ftp.changeWorkingDirectory(path);
            }
            // 设置文件类型（二进制）
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            Boolean code = ftpClient.retrieveFile(filename, baos);
            return baos.toString("gbk");
        } catch (IOException e) {  
            e.printStackTrace();  
            throw new RuntimeException("FTP客户端出错！", e);  
        } finally {  
            IOUtils.closeQuietly(baos);
            try {  
                ftpClient.disconnect();  
            } catch (IOException e) {  
                e.printStackTrace();  
                throw new RuntimeException("关闭FTP连接发生异常！", e);  
            }  
        }  
    }  
  
//    public static void main(String[] args) {
//        FileManagerByFtp fileManagerByFtp = new FileManagerByFtp();
//        fileManagerByFtp.fileDownloadByFtp("/home/wsj/service4crowd/service/ALGORITHM/ttweb1/v1/","config.json");
//        //fileDownloadByFtp();
//    }
}  