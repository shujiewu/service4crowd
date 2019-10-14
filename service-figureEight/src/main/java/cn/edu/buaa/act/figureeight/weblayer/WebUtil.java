package cn.edu.buaa.act.figureeight.weblayer;

import cn.edu.buaa.act.figureeight.exception.CrowdFlowerException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class WebUtil {

    public String urlReader(URL crowdFlower)
    {
        String jsonInput = "";

        try
        {
            URLConnection yc = crowdFlower.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
            {
                jsonInput = jsonInput + inputLine;
            }

            in.close();

            trapException(jsonInput);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (CrowdFlowerException e)
        {
            e.printStackTrace();
        }

        return jsonInput;
    }

    public void trapException(String output) throws CrowdFlowerException
    {

        try
        {
            JSONObject error = new JSONObject(output);

            if (error.has("error"))
            {
                throw new CrowdFlowerException(error.get("error").toString());
            }

        }
        catch (JSONException e)
        {
            //ignore
        }
    }

    public void trapException(int output) throws CrowdFlowerException
    {
        if ((output != 200) && (output != 202))
        {
            throw new CrowdFlowerException(String.format("Did not receive correct response code. Received %s", output));
        }
    }

    public String urlTransform(
            String baseurl,
            Map<String, String> param)
    {
        String url = baseurl;

        for (String key : param.keySet())
        {
            url = url + "&" + key + "=" + param.get(key);
        }

        //System.out.println(url);
        return url;
    }

    public String urlTransform(
            String baseurl,
            String morph)
    {
        return String.format("%s%s", baseurl, morph);
    }

    public String readResponse(HttpResponse response)
    {
        String output = "";

        HttpEntity entity = response.getEntity();

        try
        {
            trapException(response.getStatusLine().getStatusCode());
        }
        catch (CrowdFlowerException e1)
        {
            e1.printStackTrace();
        }

        InputStream instream;
        try
        {
            instream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    instream));

            // do something useful with the response
            output = output + reader.readLine();
            instream.close();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return output;
    }

    public List<JSONObject> readResponseFile(HttpResponse response)
    {

        HttpEntity entity = response.getEntity();
        try {
            trapException(response.getStatusLine().getStatusCode());
        }
        catch (CrowdFlowerException e1)
        {
            e1.printStackTrace();
        }
        List<JSONObject> output=new ArrayList<>();
        InputStream instream;
        try
        {
            instream = entity.getContent();
            // ZipFile zf = new ZipFile();
            ZipInputStream zin = new ZipInputStream(instream);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                } else {
                    System.err.println("file - " + ze.getName() + " : "
                            + ze.getSize() + " bytes");
                    long size = ze.getSize();
                    // byte[] data = ReadZipFile.getByte(zin);
                    int count;
                    byte data[] = new byte[2048];

//                    FileOutputStream fos = new
//                            FileOutputStream("D:/"+ze.getName());
//                    BufferedOutputStream dest = new
//                            BufferedOutputStream(fos, 2048);
//                    while ((count = zin.read(data, 0, 2048)) != -1)
//                    {
//                        dest.write(data, 0, count);
//                    }
//                    dest.flush();
//                    dest.close();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            zin));
                    String line = null;
                    while((line = reader.readLine()) != null) {
                        try {
                            output.add(new JSONObject(line));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //System.out.println(output);
                    return output;
                }
            }
            zin.closeEntry();
            instream.close();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    instream));
//
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            // do something useful with the response
//            // output.write(buffer, 0, r);
//            instream.close();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return output;
    }


    public static List<NameValuePair> convertAttributesToNameValuePair(Map<String, String> aAttributes)
    {
        List<NameValuePair> myNameValuePairs = new ArrayList<>();

        for (Map.Entry<String, String> myEntry : aAttributes.entrySet())
        {
            myNameValuePairs.add(new BasicNameValuePair(myEntry.getKey(), myEntry.getValue()));
        }
        return myNameValuePairs;
    }

}
