package cn.edu.buaa.act.data.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultEntity {
    private String id;
    private String job_id;
    private int judgments_count;
    private String state;
    private double agreement;
    private int missed_count;


    private String gold_pool;
    private String created_at;
    private String updated_at;
    private JSONObject results;
    private List<JudgeMent> judgeMentList;
    //private Map<String,Object> theAttributes;

//    public ResultEntity(final JSONObject aMeta)
//        {
//            id = "";
//            job_id = "";
//            theAttributes = Maps.newHashMap();
//            parse(aMeta);
//        }
//
//    private void parse(final JSONObject aMeta)
//    {
//        Iterator iterate = aMeta.;
//
//        while (iterate.hasNext())
//        {
//            addAttributes(aMeta, iterate);
//        }
//    }
//
//    private void addAttributes(final JSONObject aMeta, final Iterator aIterate)
//    {
//        String key = (String) aIterate.next();
//        try
//        {
//            //addProperty(key, aMeta.get(key).toString());
//            if (key.equals("id"))
//            {
//                id = aMeta.get(key).toString();
//            }
//            if (key.equals("job_id"))
//            {
//                job_id = aMeta.get(key).toString();
//            }
//            if(key.equals("result"))
//            {
//                JSONArray jsonArray =  aMeta.getJSONArray(key);
//                for(int i=0 ; i < jsonArray.length() ;i++)
//                {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    jsonObject.
//                }
//            }
//            theAttributes.put(key, aMeta.get(key));
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//    }
}
