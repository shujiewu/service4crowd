package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


/**
 * @author wsj
 * 人类任务的元数据
 */
@Getter
@Setter
public class CrowdTaskEntity {
    private List<String> dataId;
    private String jobId;
    private Date createTime;
    private Date endTime;
    private String status;
    private String userId;
    private String id;
    private String activityId;
    private String processInstanceId;

    private String jobType;
    private String serviceResultId;

    private String serviceName;
    private String taskId;
}
