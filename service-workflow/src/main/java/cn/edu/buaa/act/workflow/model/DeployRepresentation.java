//package cn.edu.buaa.act.workflow.model;
//
//import cn.edu.buaa.act.workflow.domain.AbstractModel;
//
//import java.util.Date;
//
///**
// * @author wsj
// */
//public class DeployRepresentation{
//
//  protected AppDefinitionRepresentation appDefinition;
//  protected boolean publish;
//  protected Boolean force;
//
//  public AppDefinitionRepresentation getAppDefinition() {
//    return appDefinition;
//  }
//
//  public void setAppDefinition(AppDefinitionRepresentation appDefinition) {
//    this.appDefinition = appDefinition;
//  }
//
//  public boolean isPublish() {
//    return publish;
//  }
//
//  public void setPublish(boolean publish) {
//    this.publish = publish;
//  }
//
//  public Boolean getForce() {
//    return force;
//  }
//
//  public void setForce(Boolean force) {
//    this.force = force;
//  }
//
//  class AppDefinitionRepresentation{
//
//    private String id;
//    private String name;
//    private String key;
//    private String description;
//    private Integer version;
//    private Date created;
//
//    public AppDefinitionRepresentation() {
//      // Empty constructor for Jackson
//    }
//    public AppDefinitionRepresentation(AbstractModel model) {
//      this.id = model.getId();
//      this.name = model.getName();
//      this.key = model.getKey();
//      this.description = model.getDescription();
//      this.version = model.getVersion();
//      this.created = model.getCreated();
//    }
//
//    public String getId() {
//      return id;
//    }
//
//    public void setId(String id) {
//      this.id = id;
//    }
//
//    public String getName() {
//      return name;
//    }
//
//    public void setName(String name) {
//      this.name = name;
//    }
//
//    public String getKey() {
//      return key;
//    }
//
//    public void setKey(String key) {
//      this.key = key;
//    }
//
//    public String getDescription() {
//      return description;
//    }
//
//    public void setDescription(String description) {
//      this.description = description;
//    }
//
//    public Integer getVersion() {
//      return version;
//    }
//
//    public void setVersion(Integer version) {
//      this.version = version;
//    }
//
//    public Date getCreated() {
//      return created;
//    }
//
//    public void setCreated(Date created) {
//      this.created = created;
//    }
//  }
//}