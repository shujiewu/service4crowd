package cn.edu.buaa.act.fastwash.delay;

import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Message implements Delayed {
    private String taskId;
    private String projectName; // 消息内容
    private Map<String,Object> request;
    private long executeTime;// 延迟时长，这个是必须的属性因为要按照这个判断延时时长。
    private long delayTime;
    public String getTaskId() {
        return taskId;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setExecuteTime(long delayTime) {
        this.executeTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getExecuteTime() {
        return executeTime;
    }  
  
    public Message(String id, String projectName, Map<String,Object> request, long delayTime) {
        this.taskId = id;
        this.projectName = projectName;
        this.request = request;
        this.delayTime = delayTime;
        this.executeTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
    }
    // 自定义实现比较方法返回 1 0 -1三个参数  
//    @Override
//    public int compareTo(Delayed delayed) {
//        Message msg = (Message) delayed;
//        return Integer.valueOf(this.taskId) > Integer.valueOf(msg.taskId) ? 1
//                : (Integer.valueOf(this.taskId) < Integer.valueOf(msg.taskId) ? -1 : 0);
//    }
    @Override
    public int compareTo(Delayed o) {
        Message o1 = (Message) o;
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
    // 延迟任务是否到时就是按照这个方法判断如果返回的是负数则说明到期否则还没到期  
    @Override  
    public long getDelay(TimeUnit unit) {  
        return unit.convert(this.executeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }
}  