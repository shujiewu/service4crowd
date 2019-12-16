package cn.edu.buaa.act.test;

import cn.edu.buaa.act.fastwash.data.Annotation;
import cn.edu.buaa.act.fastwash.data.Box;
import cn.edu.buaa.act.fastwash.data.Classification;
import cn.edu.buaa.act.test.model.CrowdAnnotationTask;
import cn.edu.buaa.act.test.model.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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
import java.util.*;

public class GetThread extends Thread {
    private final  CloseableHttpClient httpClient;
    private final String password;
    private final String userName;
    public GetThread(String userName, String password) {
        this.httpClient = HttpClientBuilder.create().build();
        this.userName = userName;
        this.password = password;
    }
    /**
     * Executes the GetMethod and prints some status information.
     */
    @Override
    public void run() {
        String token = doLogin(httpClient,userName,password);
        String projectName = "test9_5";
        Set<String> classIds = new HashSet<>();
        for(int i = 1;i<=20;i++){
            classIds.add(String.valueOf(i));
        }
        int count;
        System.out.println("start");
        while (classIds.size()>0){
            String[] keys = classIds.toArray(new String[0]);
            Random random = new Random(new Date().getTime());
            String randomKey = keys[random.nextInt(keys.length)];
            while (true){
                CrowdAnnotationTask crowdAnnotationTask = doGetImage(httpClient,token,projectName,"voc_2007_test",randomKey);
                if(crowdAnnotationTask.getDetImg()!=null){
                    if(!doLabel(httpClient,crowdAnnotationTask,projectName,token,randomKey)){
                        System.out.println(Thread.currentThread().getId()+"draw failed");
                        break;
                    }else{
                        System.out.println(Thread.currentThread().getId()+"完成一个任务");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    classIds.remove(randomKey);
                    break;
                }
            }
        }
        for(int i = 1;i<=20;i++){
            classIds.add(String.valueOf(i));
        }
        while (classIds.size()>0){
            String[] keys = classIds.toArray(new String[0]);
            Random random = new Random(new Date().getTime());
            String randomKey = keys[random.nextInt(keys.length)];
            while (true){
                CrowdAnnotationTask crowdAnnotationTask = doGetImage(httpClient,token,projectName,"voc_2007_test",randomKey);
                if(crowdAnnotationTask.getDetImg()!=null){
                    if(!doLabel(httpClient,crowdAnnotationTask,projectName,token,randomKey)){
                        System.out.println(Thread.currentThread().getId()+"draw failed");
                        break;
                    }else{
                        System.out.println(Thread.currentThread().getId()+"完成一个任务");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    classIds.remove(randomKey);
                    break;
                }
            }
        }
    }
    private static CrowdAnnotationTask doGetImage(CloseableHttpClient httpClient, String token, String projectName, String dataSetName, String classId){
        URI uri = null;
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("projectName", projectName));
            params.add(new BasicNameValuePair("dataSetName", "dataSetName"));
            params.add(new BasicNameValuePair("classId", classId));
            uri = new URIBuilder().setScheme("http").setHost("10.1.1.63")
                    .setPort(8766).setPath("/api/fastwash/annotation/task/next")
                    .setParameters(params).build();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        // 创建Get请求
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader("Content-Type", "application/json;charset=utf8");
        httpGet.setHeader("Authorization", token);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(responseEntity));
                CrowdAnnotationTask crowdAnnotationTask = jsonObject.getObject("data",CrowdAnnotationTask.class);
                if(!jsonObject.getBoolean("success")){
                    System.out.println(Thread.currentThread().getId()+"失败一次");
                }
                // System.out.println(crowdAnnotationTask.getId());
                // Thread.sleep(3000);
                return crowdAnnotationTask;

            }else{
                return null;
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static boolean doLabel(CloseableHttpClient httpClient, CrowdAnnotationTask crowdAnnotationTask,String projectName,String token,String classId){
        HttpPost httpPost = new HttpPost("http://10.1.1.63:8766/api/fastwash/annotation/task/submit?projectName="+projectName);
        if(crowdAnnotationTask.getItems()==null){
            crowdAnnotationTask.setItems(new ArrayList<>());
        }
        /*
          box: item.box,
          classification: { value: item.class, id: item.classId},
          property: item.prop,
          status: item.status,
          type:"crowdAnnotated"
         */
        Random random = new Random(new Date().getTime());
        int width = crowdAnnotationTask.getDetImg().getWidth();
        int height = crowdAnnotationTask.getDetImg().getHeight();
        int size = random.nextInt(5);
        List<Annotation> annotations = new ArrayList<>();
        for(int i = 0;i<size;i++){
            Annotation annotation = new Annotation();
            annotation.setBox(new Box(random.nextInt(width),random.nextInt(height),random.nextInt(width),random.nextInt(height),0.0));
            annotation.setClassification(new Classification(classId,classId));
            annotation.setProperty(null);
            annotation.setStatus("new");
            annotation.setType("crowdAnnotated");
            annotations.add(annotation);
        }
        crowdAnnotationTask.setItems(annotations);
        String jsonString = JSON.toJSONString(crowdAnnotationTask);
        StringEntity entity = new StringEntity(jsonString, "UTF-8");
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.setHeader("Authorization", token);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            //System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                // System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(responseEntity));
                if(jsonObject.getBoolean("success"))
                    return true;
                else
                    return false;
                //return jsonObject.getString("token");
            }else{
                return false;
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static String doLogin(CloseableHttpClient httpClient, String userName, String password) {

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        // CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Post请求
        HttpPost httpPost = new HttpPost("http://10.1.1.63:8766/api/auth/user/login");
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
            //System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                //System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
                JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(responseEntity));
                return jsonObject.getString("token");
            }else{
                return null;
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}