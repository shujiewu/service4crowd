package cn.edu.buaa.act.fastwash.entity;

import cn.edu.buaa.act.fastwash.data.Classification;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ImageToClass {
    private String imageId;
    private String dataSetName;
    private Set<Classification> classificationList;
}
