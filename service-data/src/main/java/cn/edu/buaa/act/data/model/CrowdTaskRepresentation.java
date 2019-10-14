package cn.edu.buaa.act.data.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author wsj
 * 人类任务元数据
 */
@Setter
@Getter
public class CrowdTaskRepresentation {
    private Date createTime;
    private String status;
    private String taskId;
    private Date completeTime;
    private String jobType;
    private String serviceResultId;
    private String serviceName;
}
