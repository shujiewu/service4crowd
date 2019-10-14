package cn.edu.buaa.act.figureeight.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * @author wsj
 */
@Getter
@Setter
public class ResultEntity {
    private String id;
    private String job_id;
    private JSONObject data;
    private int judgments_count;
    private String state;
    private double agreement;
    private int missed_count;
    private String gold_pool;
    private String created_at;
    private String updated_at;
    private JSONObject results;
    private List<JudgmentItem> judgmentList;
}
