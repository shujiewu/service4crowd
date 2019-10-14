package cn.edu.buaa.act.figureeight.model;

import org.json.*;
public class Result {

    private JSONObject json;
    private JSONObject text;
    private String judgments = "";
    private String agg = "";
    private String confidence = "";

    public Result(String result)
    {
        try
        {
            json = new JSONObject(result);
            judgments = json.get("judgments").toString();
            text = new JSONObject(json.get("text").toString());
            agg = text.get("agg").toString();
            confidence = text.get("confidence").toString();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public String getAggregation()
    {
        return agg;
    }

    public String getConfidence()
    {
        return confidence;
    }

    public String getJudgments()
    {
        return judgments;
    }
}
