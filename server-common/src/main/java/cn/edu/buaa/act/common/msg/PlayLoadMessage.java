package cn.edu.buaa.act.common.msg;

import java.io.Serializable;

public class PlayLoadMessage<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String activityId;
    private boolean complete;
    private String processInstanceId;
    private String userId;
    private String serviceResultId;
    private String taskType;   //机器还是人类任务
    private String taskId; //task的id

    private String serviceName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    private T message;

    public boolean isComplete() {
        return complete;
    }

    public String getActivityId() {
        return activityId;
    }

    public T getMessage() {
        return message;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    public String getServiceResultId() {
        return serviceResultId;
    }

    public void setServiceResultId(String serviceResultId) {
        this.serviceResultId = serviceResultId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}