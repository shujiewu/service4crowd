package cn.edu.buaa.act.data.entity;

import cn.edu.buaa.act.data.model.AnswerStatRepresentation;
import cn.edu.buaa.act.data.vo.Label;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wsj
 * 回答的结果
 */
@Getter
@Setter
public class AnswerEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String userName;
    private String userId;
    private String dataType;
    private List<Label> labelList;
    private Map<String, String> goldLabels;
    private Date createTime;
    private String name;
    private Boolean hasGold;

    private AnswerStatRepresentation answerStatRepresentation;
}