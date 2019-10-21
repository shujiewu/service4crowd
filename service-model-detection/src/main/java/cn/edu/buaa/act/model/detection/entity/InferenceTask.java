package cn.edu.buaa.act.model.detection.entity;


import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document(collection = "modelInferenceTask")
public class InferenceTask {
    private String id;
    private String userId;
    private String projectName;
    private String dataSetName;
    private List<String> imageIdList;
    private Date createTime;
    private Date endTime;
    private String status;
    private JSONObject inferenceResult;
}
