package cn.edu.buaa.act.model.detection.entity;


import cn.edu.buaa.act.fastwash.data.TrainingItem;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrainingRequest {
    private String trainingId;
    private Boolean async;

    private String modelName;
    private int gpuNum;
    private int maxIter;
    private double weightDecay;
    private List<Integer> steps;
    private List<Double> lrs;
    private boolean simulate;

    private double gamma;
    private double baseLR;

    private String dataSetName;
    private List<String> imageIdList;
    private List<TrainingItem> trainingItemList;
}
