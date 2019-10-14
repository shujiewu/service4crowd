package cn.edu.buaa.act.data.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wsj
 * 汇聚结果展示
 */
@Getter
@Setter
public class AggregateResultRepresentation {
    private String id;
    private String predictResult;
    private String truth;
    private Boolean correct;
}
