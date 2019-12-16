package cn.edu.buaa.act.management.entity;

public class DockerVo {

  private Integer images;
  private Integer  cpus;
  private Long totalMem;
  public Integer getImages() {
    return images;
  }
  public void setImages(Integer images) {
    this.images = images;
  }
  public Integer getCpus() {
    return cpus;
  }
  public void setCpus(Integer cpus) {
    this.cpus = cpus;
  }
  public Long getTotalMem() {
    return totalMem;
  }
  public void setTotalMem(Long totalMem) {
    this.totalMem = totalMem;
  }
  @Override
  public String toString() {
    return "DockerVo [images=" + images + ", cpus=" + cpus + ", totalMem=" + totalMem + "]";
  }
}