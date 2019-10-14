package cn.edu.buaa.act.figureeight.model;

import cn.edu.buaa.act.figureeight.constant.JudgementAttribute;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Judgment {

    private String id = "";
    private String theJobId = "";
    private Map<String,String> attributes;
    private static final Logger LOGGER = LoggerFactory.getLogger(Judgment.class);

    public Judgment()
    {
        id = "";
        theJobId = "";
        attributes = new HashMap<>();
    }

    public Judgment(final JSONObject aRawJudgment)
    {
        id = "";
        theJobId = "";
        attributes = new HashMap<>();
        jsonIterate(aRawJudgment);
    }

    public void addProperty(String name, String value)
    {
        if (name.equals("id"))
        {
            id = value;
        }
        if (name.equals("job_id"))
        {
            theJobId = value;
        }
        attributes.put("judgment[" + name + "]", value);
    }

    public void addProperty(JudgementAttribute name, String value)
    {
        addProperty(name.toString(), value);
    }

    public String getProperty(String name)
    {
        return attributes.get("judgment[" + name + "]");
    }

    public String getProperty(JudgementAttribute name)
    {
        return getProperty(name.toString());
    }

    public String getJudgmentId()
    {
        return id;
    }

    public String getJobId()
    {
        return theJobId;
    }

    public Map<String,String> getAttributes()
    {
        return attributes;
    }

    private void jsonIterate(JSONObject json)
    {
        Iterator iterate;
        try
        {
            iterate = json.keys();

            while (iterate.hasNext())
            {
                extractPropertyAndAddAsAttribute(json, iterate);
            }
        }
        catch (JSONException e)
        {
            LOGGER.error("Cannot parse the incoming job details : ", e);
        }
    }
    private void extractPropertyAndAddAsAttribute(final JSONObject json, final Iterator aIterate) throws JSONException
    {
        String key = (String) aIterate.next();
        addProperty(key, json.get(key).toString());
    }
}
