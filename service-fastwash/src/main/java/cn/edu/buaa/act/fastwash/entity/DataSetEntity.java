package cn.edu.buaa.act.fastwash.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "dataSetConfig")
public class DataSetEntity {
    private String id;
    private String type;
    private String dataSetName;
    private List<Category> categories;
    private List<Image> images;
    private List<JSONObject> annotations;
}
