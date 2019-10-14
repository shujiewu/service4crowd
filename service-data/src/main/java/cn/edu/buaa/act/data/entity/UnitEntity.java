package cn.edu.buaa.act.data.entity;

import cn.edu.buaa.act.data.vo.Label;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author wsj
 * 一行数据
 */
@Getter
@Setter
public class UnitEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String unitId;
    private String state;
    private String id;
    private Object data;

    private String goldLabel;

    private Map<String,List<Label>> labelMap; // job对label
}
