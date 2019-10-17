package cn.edu.buaa.act.fastwash.entity;


import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DataItemEntity {
    private String id;
    private String dataSetName;
    private String imageId;
    private String fileName;
    private int width;
    private int height;
    private boolean hasGroundTruth;
    private String status;
    private String lastUpdateTime;
    //class->iteration->list
    private Map<String,Map<String,List<Annotation>>> annotations;
}
