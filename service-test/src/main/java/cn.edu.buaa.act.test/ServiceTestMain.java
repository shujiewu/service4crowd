package cn.edu.buaa.act.test;

import cn.edu.buaa.act.test.model.CrowdAnnotationTask;
import cn.edu.buaa.act.test.model.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ServiceTestMain {
    public static void main(String[] args) throws Exception {
        String userName = "worker";
        String password = "worker";
        int workerNum = 20;

        GetThread[] threads = new GetThread[workerNum];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new GetThread(userName+String.valueOf(i), password+String.valueOf(i));
        }
        for (int j = 0; j < threads.length; j++) {
            threads[j].start();
        }
        for (int j = 0; j < threads.length; j++) {
            threads[j].join();
        }
//        for(int i =0;i<1;i++){
//            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//            String token = doLogin(httpClient,userName+String.valueOf(i),password+String.valueOf(i));
//            doLabel(httpClient,token,"test7_5","voc_2007_test","12");
//        }
        // doLogin();
    }

    private static void doRegister(CloseableHttpClient httpClient, String userName,String password,String type){
        // 创建Post请求
        HttpPost httpPost = new HttpPost("http://10.1.1.63:8766/api/auth/user/register");
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        String jsonString = JSON.toJSONString(user);

        StringEntity entity = new StringEntity(jsonString, "UTF-8");
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
