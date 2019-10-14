package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wsj
 * 元数据
 */
@Getter
@Setter
public class MetaEntity {
    private String id;
    private String userName;
    private String userId;
    private List<String> jobId;

    private String metaName;
    private List<Object> data;
    private List<String> dataId;
    private List<String> fileId;
    private List<String> header;
    private String type;
    private Date createTime;

    private Map<String,String> answerEntityId;

    // private Map<String,String> goldLabels;
}
