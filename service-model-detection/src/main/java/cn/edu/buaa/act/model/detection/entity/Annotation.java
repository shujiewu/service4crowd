package cn.edu.buaa.act.model.detection.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Annotation {
    private String id;
    private String workId;
    private String modelId;
    private String type;
    private Box box;

    private String classification;
    private String property;
    private Date lastUpdatedTime;
    private String lastAnnotation;
}
