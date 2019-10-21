package cn.edu.buaa.act.fastwash.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Annotation {
    private String id;
    private String workerId;
    private String modelId;

    private String type;
    private Box box;
    private String status;
    private Classification classification;
    private JSONObject property;
    private Date lastUpdatedTime;
    private String lastAnnotationId;
}
