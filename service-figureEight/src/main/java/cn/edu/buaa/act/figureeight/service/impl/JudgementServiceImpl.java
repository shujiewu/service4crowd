package cn.edu.buaa.act.figureeight.service.impl;

import cn.edu.buaa.act.figureeight.constant.Constants;
import cn.edu.buaa.act.figureeight.model.Judgment;
import cn.edu.buaa.act.figureeight.service.JudgementService;
import cn.edu.buaa.act.figureeight.weblayer.WebCall;
import cn.edu.buaa.act.figureeight.weblayer.WebUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JudgementServiceImpl
 *
 * @author wsj
 * @date 2018/10/27
 */
@Service
public class JudgementServiceImpl implements JudgementService {

    @Autowired
    private WebUtil theWebUtil;

    @Autowired
    private WebCall theWebCall;

    @Override
    public void create(Judgment aJudgment, String apiKey) {
        String augURL = theWebUtil.urlTransform(Constants.URL, "/" + aJudgment.getJobId() + "/judgments.json?key=" + apiKey);
        theWebCall.create(augURL,WebUtil.convertAttributesToNameValuePair(aJudgment.getAttributes()));
    }

    @Override
    public Judgment getJudgment(String aJobId, String aJudgmentId, String apiKey) {
        String augURL = theWebUtil.urlTransform(Constants.URL, "/" + aJobId + "/judgments/" + aJudgmentId + ".json?key=" + apiKey);
        try {
            return new Judgment(new JSONObject(theWebCall.get(augURL)));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String read(Map<String, String> param, String apiKey) {
        return null;
    }

    @Override
    public void update(Judgment aJudgment, String apiKey) {
        String myMorph = String.format("/%s/judgments/%s.json?key=%s", aJudgment.getJobId(), aJudgment.getJudgmentId(), apiKey);
        String augURL = theWebUtil.urlTransform(Constants.URL, myMorph);
        theWebCall.update(augURL, WebUtil.convertAttributesToNameValuePair(aJudgment.getAttributes()));
    }

    @Override
    public void delete(String aJobId, String aJudgmentId, String apiKey) {
        String myMorph = "/" + aJobId + "/judgments/" + aJudgmentId + ".json?key=" + apiKey;
        String augURL = theWebUtil.urlTransform(Constants.URL, myMorph);
        theWebCall.delete(augURL);
    }

    @Override
    public List<Judgment> getReport(String aJobId, String apiKey) {
        String augURL = theWebUtil.urlTransform(Constants.URL, "/"+ aJobId + ".csv?type=json&key=" + apiKey);
        List<JSONObject> result = theWebCall.httpDownloadFile(augURL);
        System.out.println(result.size());
        List<Judgment> judgments = new ArrayList<>(result.size());
        result.parallelStream().forEach(jsonObject -> {
            Judgment judgment = new Judgment(jsonObject);
            judgments.add(judgment);
        });
        return judgments;
    }

    //这里没有修改
    @Override
    public List<Judgment> regenerateReport(String aJobId, String apiKey) {
        String augURL = theWebUtil.urlTransform(Constants.URL, "/"+ aJobId + "/regenerate?type=full&key=" + apiKey);
        try {
            JSONObject xx= new JSONObject(theWebCall.get(augURL));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
