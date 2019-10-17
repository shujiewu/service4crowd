package cn.edu.buaa.act.fastwash.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TrainingItem {
    private String id;

    private String dataSetName;
    private String imageId;
    private List<Tag> tagList;
    //private Date lastUpdatedTime;
    //groundTruth or crowd
    private String type;
}
