package cn.edu.buaa.act.fastwash.entity;

import cn.edu.buaa.act.fastwash.data.Classification;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author wsj
 * 项目
 */
@Getter
@Setter
@Document(collection = "projectConfig")
public class ProjectEntity {
    private String id;
    private String name;
    private String type;
    private String dataSetName;
    private List<Classification> classification;
    private List<JSONObject> properties;
    private String userId;
    private String status;
    private int total;
    private int done;
    private int run;
    private Date createTime;
    private Date endTime;
    private List<String> imageId;
}
