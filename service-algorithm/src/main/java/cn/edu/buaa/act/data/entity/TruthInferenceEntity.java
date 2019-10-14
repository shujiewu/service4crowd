package cn.edu.buaa.act.data.entity;


import cn.edu.buaa.act.common.util.ServiceProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author wsj
 */
@Getter
@Setter
public class TruthInferenceEntity extends BaseAlgorithmEntity implements Comparable<TruthInferenceEntity>{

    private String taskType;
    private String taskModel;
    private String workerModel;
    private String technique;
    private String implType;

    private List<ServiceProperty> inputProperty;
    private List<ServiceProperty> outputProperty;

    @Override
    public int compareTo(TruthInferenceEntity that) {
        int i = this.getMethod().compareTo(that.getMethod());
        if (i == 0) {
            i = this.getTag().compareTo(that.getTag());
        }
        if (i == 0) {
            i = this.getVersion().compareTo(that.getVersion());
        }
        return i;
    }

    @Override
    public String toString() {
        return "TruthInferenceEntity{" + "name='" + getMethod()+"}";
    }
    //private List<String> fileName;
}
