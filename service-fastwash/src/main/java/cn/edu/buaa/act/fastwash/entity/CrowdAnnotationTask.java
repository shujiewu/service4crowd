package cn.edu.buaa.act.fastwash.entity;

import cn.edu.buaa.act.fastwash.data.Annotation;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CrowdAnnotationTask {
    private String id;
    private Image detImg;
    private List<Annotation> items;
    private String createTime;
    private String endTime;
    private String classId;
}
