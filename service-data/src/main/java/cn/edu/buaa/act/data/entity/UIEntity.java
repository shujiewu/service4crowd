package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author wsj
 * 界面数据
 */
@Getter
@Setter
public class UIEntity {
    private String id;
    private String userName;
    private String userId;
    private List<String> jobId;

    private Date createTime;
    private Date lastUpdateTime;
    private String title;
    private String instruction;
    private String cml;
    private String description;

    private String status;
}
