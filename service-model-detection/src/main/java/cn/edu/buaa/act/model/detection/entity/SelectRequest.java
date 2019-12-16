package cn.edu.buaa.act.model.detection.entity;

import cn.edu.buaa.act.fastwash.data.TrainingItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SelectRequest {
    private String inferenceId;
    private Boolean async;

    private String modelConfig;
    private String modelPkl;
    private String activeMethodName;

    private String dataSetName;
    private int selectNum;
    private List<String> imageIdList;
}
