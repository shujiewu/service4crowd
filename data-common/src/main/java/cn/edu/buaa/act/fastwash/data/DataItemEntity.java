package cn.edu.buaa.act.fastwash.data;


import lombok.Getter;
import lombok.Setter;

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
    private boolean hasMachineInferenceTruth;
    private boolean hasCrowdInferenceTruth;
    private String status;
    private String lastUpdateTime;
    //class->iteration->list
    private Map<String,Map<String,List<Annotation>>> annotations;

    private List<String> updateTime;

    //class->userIdList
    //private Map<String,List<String>> workerList;
}
