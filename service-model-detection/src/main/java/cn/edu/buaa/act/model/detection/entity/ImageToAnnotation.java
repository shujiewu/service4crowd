package cn.edu.buaa.act.model.detection.entity;

import cn.edu.buaa.act.fastwash.data.Annotation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageToAnnotation {
    private String imageId;
    private String dataSetName;
    private List<Annotation> annotationList;
}
