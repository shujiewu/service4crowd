package cn.edu.buaa.act.data.processor.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * LabelStruct
 *
 * @author wsj
 * @date 2018/11/3
 */
@Getter
@Setter
public class LabelStruct {
    private String ResponseId;
    private String TaskIdx;
    private String WorkerIdx;
    private double Confidence;
}
