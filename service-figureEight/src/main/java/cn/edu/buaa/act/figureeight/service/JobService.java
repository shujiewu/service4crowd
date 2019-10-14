package cn.edu.buaa.act.figureeight.service;

import cn.edu.buaa.act.figureeight.exception.NullAPIKeyException;
import cn.edu.buaa.act.figureeight.model.Channel;
import cn.edu.buaa.act.figureeight.model.Job;
import cn.edu.buaa.act.figureeight.model.Judgment;
import cn.edu.buaa.act.figureeight.model.Unit;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * JobService
 *
 * @author wsj
 * @date 2018/10/18
 */
public interface JobService {
    public Job create(Job aJob,String apiKey);

    public void upload(Job aJob,String apiKey,String absolutePath, String contentType) throws NullAPIKeyException;
    public void upload(String aJobId,String apiKey, String absolutePath, String contentType, Map<String, String> param);
    public void upload(Job aJob,String apiKey,String url);
    public void upload(Job aJob,String apiKey,String absolutePath, String contentType, boolean async) throws NullAPIKeyException;

    public void update(Job aJob,String apiKey) throws NullAPIKeyException;

    public List<Unit> getJobUnits(String aJobId,String apiKey);
    public Unit getUnit(String aJobId, String aUnitId,String apiKey) throws JSONException;

    public void addRemoveGold(String aJobId, String apiKey,Map<String, String> param);

    public List<Job> getJobs(String apiKey);

    public List<Judgment> getJudgments(String aJobId,String apiKey);
    public Judgment getJudgment(String aJobId, String aJudgmentId,String apiKey);

    public Job getJob(String aJobId,String apiKey) throws NullAPIKeyException;

    public JSONObject getUnitsStatus(String aJobId,String apiKey);
    public String status(String aJobId,String apiKey);

    public void pause(String aJobId,String apiKey);
    public void resume(String aJobId,String apiKey);
    public void cancel(String aJobId,String apiKey);
    public void delete(String aJobId,String apiKey);

    public Channel getChannels(String aJobId,String apiKey);
    public void setChannels(String aJobId, String apiKey,List<String> channels);

    public void bulkSplit(String aJobId, String on, String with,String apiKey);
    public JSONObject legend(String aJobId,String apiKey);
    public void setPayPerAssignment(Job aJob, String pay,String apiKey);
}
