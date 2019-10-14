package cn.edu.buaa.act.figureeight.service;

import cn.edu.buaa.act.figureeight.model.Judgment;

import java.util.List;
import java.util.Map;

/**
 * JudgementService
 *
 * @author wsj
 * @date 2018/10/27
 */
public interface JudgementService {
    void create(Judgment aJudgment,String apiKey);
    Judgment getJudgment(String aJobId, String aJudgmentId,String apiKey);
    String read(Map<String, String> param,String apiKey);
    void update(Judgment aJudgment,String apiKey);
    void delete(String aJobId, String aJudgmentId,String apiKey);
    List<Judgment> getReport(String aJobId,String apiKey);
    List<Judgment> regenerateReport(String aJobId, String apiKey);
}
