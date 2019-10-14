package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wsj
 * 汇聚结果
 */
@Getter
@Setter
public class AggregateResultEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    private String id;
    private String userId;
    private String activityId;
    private String processInstanceId;
    private double createTime;
    private double duration;
    private double endTime;

    private double accuracy;
    private double fscore;

    private Map<String,Object> resultIndex = new HashMap<>();
    private String taskType;
    private String dataType;
    private String dataId;
    private String method;

    private Map<String,String> resultPredict= new HashMap<>();
    private Map<String,String> resultTruth = new HashMap<>();

    private Map<String,String> workerModel = new HashMap<>();
    private Map<String,String> taskModel = new HashMap<>();
}
