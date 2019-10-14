package cn.edu.buaa.act.data.vo;

import cn.edu.buaa.act.data.vo.Judgement;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author wsj
 * FigureEight平台原始数据
 */
@Getter
@Setter
public class FigureEightResult {
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
    private List<Judgement> judgeMentList;
}
