package cn.edu.buaa.act.fastwash.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ImageEntity {
    private String id;
    private String imageId;
    private String status;
    //class->iteration->list
    private Map<String,Map<String,List<Annotation>>> annotations;
}
