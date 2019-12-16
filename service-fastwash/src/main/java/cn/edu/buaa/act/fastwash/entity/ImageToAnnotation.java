package cn.edu.buaa.act.fastwash.entity;

import cn.edu.buaa.act.fastwash.data.Annotation;
import cn.edu.buaa.act.fastwash.data.Classification;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ImageToAnnotation {
    private String imageId;
    private String dataSetName;
    private List<Annotation> annotationList;
}
