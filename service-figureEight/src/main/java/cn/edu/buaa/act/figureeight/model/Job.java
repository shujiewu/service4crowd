package cn.edu.buaa.act.figureeight.model;

import cn.edu.buaa.act.common.util.ServiceProperty;
import cn.edu.buaa.act.figureeight.constant.JobAttribute;
import org.slf4j.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.json.*;


/**
 * @author wsj
 */
public class Job {
    private String id;
    private static final Logger LOGGER = LoggerFactory.getLogger(Job.class);
    private Map<String,String> theAttributes;

    public Job(
            final String aId,
            final Map<String,String> aAttributes)
    {
        id = aId;
        theAttributes = aAttributes;
    }

    public Job()
    {
        id = "";
        theAttributes = new HashMap<>();
    }

    public Job(final JSONObject aJson)
    {
        id = "";
        theAttributes = new HashMap<>();
        jsonIterate(aJson);
    }

    public String getId()
    {
        return id;
    }

    public Map<String,String> getAttributes()
    {
        return theAttributes;
    }

    @SuppressWarnings("rawtypes")
    private void jsonIterate(JSONObject json)
    {
        Iterator iterate;
        try
        {

            iterate = json.keys();

            while (iterate.hasNext())
            {
                addAttribute(json, iterate);
            }
        }
        catch (JSONException e)
        {
            LOGGER.error("Cannot parse the incoming job details : ",
                         e);
        }
    }

    private void addAttribute(final JSONObject json, final Iterator aIterate) throws JSONException {
        String key = (String) aIterate.next();
        addProperty(key, json.get(key).toString());
    }

    public void addProperty(String aProperty, String aValue) {
        if (aProperty.equals("id"))
        {
            id = aValue;
        }
        LOGGER.info("Adding aProperty" + aProperty + " to job with id  - {}", id);
        theAttributes.put("job[" + aProperty + "]", aValue);
    }

//    public void addProperties(Map<String,ServiceProperty> servicePropertyMap) {
//        for (Map.Entry<String, ServiceProperty> myEntry : servicePropertyMap.entrySet()) {
//            addProperty(myEntry.getKey(),myEntry.getValue().getValue().toString());
//        }
//    }

    public void addProperties(Map<String,Object> servicePropertyMap) {
        for (Map.Entry<String, Object> myEntry : servicePropertyMap.entrySet()) {
            addProperty(myEntry.getKey(),myEntry.getValue().toString());
        }
    }

    public String getAttribute(String propertyName)
    {
       return theAttributes.get("job[" + propertyName + "]");
    }

    public void addProperty(JobAttribute property, String value)
    {
        addProperty(property.toString(), value);
    }

    public String getAttribute(JobAttribute propertyName)
    {
        return getAttribute(propertyName.toString());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, theAttributes);
    }

    @Override
    public boolean equals(final Object o)
    {
        return Objects.equals(id, ((Job) o).getId()) && Objects.equals(getClass(), o.getClass()) && Objects.equals(theAttributes,
                              ((Job) o).getAttributes());
    }
}
