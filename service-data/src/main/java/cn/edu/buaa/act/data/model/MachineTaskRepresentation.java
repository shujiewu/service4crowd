package cn.edu.buaa.act.data.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author wsj
 * 机器任务元数据
 */
@Setter
@Getter
public class MachineTaskRepresentation {
    private String status;
    private String taskId;
    private String serviceName;
    private Date completeTime;
    private Date createTime;
    private String serviceResultId;
}
