package cn.edu.buaa.act.fastwash.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TaskItemEntity {
    private String id;
    private String dataSetName;
    private String imageId;
    private String fileName;

    // Maybe unsure
    private String classId;

    private List<String> workerList;
    private List<String> updateTime;
    private int iterations;

    private String status;

    //timeToAnnList
    private Map<String,List<Annotation>> annotations;
    private String lastUpdateTime;
}
