package cn.edu.buaa.act.figureeight.weblayer;



import cn.edu.buaa.act.figureeight.exception.CrowdFlowerException;
import cn.edu.buaa.act.figureeight.exception.MalformedCrowdURLException;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author wsj
 */
@Service
public class WebCall {

    @Autowired
    protected WebUtil theWebUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebCall.class);

    public  JSONObject getJob(String URL) throws MalformedCrowdURLException
    {
        JSONObject json = null;
        URL crowdFlower;
        try {
            crowdFlower = new URL(URL);

            json = new JSONObject(theWebUtil.urlReader(crowdFlower));

            return json;

        }
        catch (MalformedURLException e1) {

            throw new MalformedCrowdURLException(URL);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return json;

    }

    public  String createJob(String URL, List<NameValuePair> attributes) {
        return create(URL, attributes);
    }

    @SuppressWarnings("deprecation")
    public  void upload(String absolutePath, String URL, String type)
    {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost post = new HttpPost(URL);
            post.setEntity(new FileEntity(new File(absolutePath),
                    type));
            LOGGER.info("HTTP POST @ url - " + URL);
            HttpResponse response = httpClient.execute(post);
            System.out.println(response.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void uploadURI(String URL, List<NameValuePair> attributes) {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            String output = "";
            HttpPost post = new HttpPost(URL);
            post.setEntity(new UrlEncodedFormEntity(attributes));
            LOGGER.info("HTTP POST @ url - " + URL);
            output = theWebUtil.readResponse(httpClient.execute(post));
            theWebUtil.trapException(output);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (CrowdFlowerException e)
        {
            e.printStackTrace();
        }

    }

    public JSONArray getJobs(String URL)
    {
        JSONArray json = null;
        URL crowdFlower;
        try {
            crowdFlower = new URL(URL);
            URLConnection yc = crowdFlower.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String jsonInput = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                jsonInput = jsonInput + inputLine;
            }
            in.close();

            json = new JSONArray(jsonInput);

        }
        catch (MalformedURLException e1)
        {
            e1.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return json;
    }
    public  JSONObject getUnits(String url)
    {
        JSONObject json = null;
        URL crowdFlower;
        try
        {
            crowdFlower = new URL(url);

            json = new JSONObject(theWebUtil.urlReader(crowdFlower));

            return json;

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e1)
        {
            e1.printStackTrace();
        }
        return json;
    }
    /**
     * 下载文件
     *
     * @param url
     */
    @Web
    public List<JSONObject> httpDownloadFile(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        List<JSONObject> output = new ArrayList<>();
        HttpGet get = new HttpGet(url);
        try
        {
            LOGGER.info("HTTP GET @ url - {}",
                    url);
            output = theWebUtil.readResponseFile(httpClient.execute(get));
            // theWebUtil.trapException(output);
        }
        catch (ClientProtocolException e)
        {
            LOGGER.error(e.toString());
        }
        catch (IOException e)
        {
            LOGGER.error(e.toString());
        }
        return output;
    }

    @Web
    public String get(String url)
    {
        HttpClient httpClient = new DefaultHttpClient();
        String output = "";

        HttpGet get = new HttpGet(url);

        try
        {
            LOGGER.info("HTTP GET @ url - {}", url);
            output = theWebUtil.readResponse(httpClient.execute(get));
            theWebUtil.trapException(output);

        }
        catch (ClientProtocolException e)
        {
            LOGGER.error(e.toString());
        }
        catch (IOException e)
        {
            LOGGER.error(e.toString());
        }
        catch (CrowdFlowerException e)
        {
            LOGGER.error(e.toString());
        }

        return output;
    }

    public String create(
            String url,
            List<NameValuePair> attributes)
    {
        HttpClient httpClient = new DefaultHttpClient();

        String output = "";

        try
        {
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(attributes));

            LOGGER.info("HTTP POST @ url - {}",
                        url);

            output = theWebUtil.readResponse(httpClient.execute(post));
            theWebUtil.trapException(output);

        }
        catch (MalformedURLException e)
        {
            LOGGER.error(e.toString());
        }
        catch (IOException e)
        {
            LOGGER.error(e.toString());
        }
        catch (CrowdFlowerException e)
        {
            LOGGER.error(e.toString());
        }

        return output;
    }

    public void update(
            String url,
           Collection<NameValuePair> attributes)
    {
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPut put = new HttpPut(url);
            put.setEntity(new UrlEncodedFormEntity(new ArrayList<>(attributes)));

            LOGGER.info("HTTP PUT @ url - " + url);

            theWebUtil.readResponse(httpClient.execute(put));

        }
        catch (ClientProtocolException e)
        {
            LOGGER.error(e.toString());
        }
        catch (IOException e)
        {
            LOGGER.error(e.toString());
        }

    }

    public void put(String url)
    {
        HttpClient httpClient = new DefaultHttpClient();

        try
        {

            HttpPut put = new HttpPut(url);

            LOGGER.info("HTTP PUT @ url - " + url);

            System.out.println(httpClient.execute(put));

        }
        catch (ClientProtocolException e)
        {
            LOGGER.error(e.toString());
        }
        catch (IOException e)
        {
            LOGGER.error(e.toString());
        }

    }

    public JSONObject getMeta(String url) throws MalformedCrowdURLException
    {
        URL crowdFlower;

        try
        {
            crowdFlower = new URL(url);

            LOGGER.info("Reading url - " + url);

            return new JSONObject(theWebUtil.urlReader(crowdFlower));
        }
        catch (MalformedURLException e1)
        {

            throw new MalformedCrowdURLException(url);
        }
        catch (JSONException e)
        {
            LOGGER.error(e.toString());

        }
        return null;

    }

    public void delete(String url)
    {
        HttpClient httpClient = new DefaultHttpClient();
        try
        {
            String output;
            HttpDelete delete = new HttpDelete(url);
            LOGGER.info("HTTP DELETE @ url - " + url);
            HttpResponse response = httpClient.execute(delete);
            output = theWebUtil.readResponse(response);
            theWebUtil.trapException(output);
        }
        catch (MalformedURLException e)
        {
            LOGGER.error(e.toString());
        }
        catch (IOException e)
        {
            LOGGER.error(e.toString());
        }
        catch (CrowdFlowerException e)
        {
            LOGGER.error(e.toString());
        }
    }
}
