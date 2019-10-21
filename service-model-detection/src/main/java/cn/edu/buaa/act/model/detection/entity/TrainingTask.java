package cn.edu.buaa.act.model.detection.entity;


import cn.edu.buaa.act.fastwash.data.TrainingItem;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document(collection = "modelTrainingTask")
public class TrainingTask {
    private String id;
    private String userId;
    private String projectName;
    private String dataSetName;

    private List<String> imageIdList;
    private Date createTime;
    private Date endTime;
    private String status;
    private List<TrainingItem> trainingItemList;

    private JSONObject trainingConfig;
}
