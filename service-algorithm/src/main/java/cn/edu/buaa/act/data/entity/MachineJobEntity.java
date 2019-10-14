package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class MachineJobEntity {
    private List<String> dataId;
    private String jobId;
    private Date createTime;
    private Date endTime;
    private String status;
    private String userId;
    private String id;
    private String activityId;
    private String processInstanceId;
    private String serviceName;
    private String serviceResultId;
}
