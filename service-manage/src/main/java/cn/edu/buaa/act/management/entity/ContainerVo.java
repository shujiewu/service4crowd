package cn.edu.buaa.act.management.entity;

public class ContainerVo {

  private String name;//容器名
  private String id;//容器id

  private String imageName;//镜像名
  private int innerPort;//端口映射的容器port
  private int pubPort;//端口映射的主机端口
  
  private String accessUrl;//后期拼接而成
  
  private Integer cpuShare;//cpu限额
  private Long memLimit;//内存最大
  
  private String status;//容器状态
  
  private String createTime;//创建时间

  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public String getId() {
    return id;
  }


  public void setId(String id) {
    this.id = id;
  }


  public String getStatus() {
    return status;
  }


  public void setStatus(String status) {
    this.status = status;
  }


  public String getImageName() {
    return imageName;
  }


  public void setImageName(String imageName) {
    this.imageName = imageName;
  }


  public int getInnerPort() {
    return innerPort;
  }


  public void setInnerPort(int innerPort) {
    this.innerPort = innerPort;
  }


  public int getPubPort() {
    return pubPort;
  }


  public void setPubPort(int pubPort) {
    this.pubPort = pubPort;
  }


  public String getAccessUrl() {
    return accessUrl;
  }


  public void setAccessUrl(String accessUrl) {
    this.accessUrl = accessUrl;
  }


  public Integer getCpuShare() {
    return cpuShare;
  }


  public void setCpuShare(Integer cpuShare) {
    this.cpuShare = cpuShare;
  }


  public Long getMemLimit() {
    return memLimit;
  }


  public void setMemLimit(Long memLimit) {
    this.memLimit = memLimit;
  }


  public String getCreateTime() {
    return createTime;
  }


  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }


  @Override
  public String toString() {
    return "ContainerVO [name=" + name + ", id=" + id + ", imageName=" + imageName + ", innerPort="
        + innerPort + ", pubPort=" + pubPort + ", accessUrl=" + accessUrl + ", cpuShare=" + cpuShare
        + ", memLimit=" + memLimit + ", status=" + status + ", createTime=" + createTime + "]";
  }
}