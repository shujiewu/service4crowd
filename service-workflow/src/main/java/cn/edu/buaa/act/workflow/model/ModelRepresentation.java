package cn.edu.buaa.act.workflow.model;

import cn.edu.buaa.act.workflow.domain.AbstractModel;
import cn.edu.buaa.act.workflow.domain.Model;
import cn.edu.buaa.act.workflow.domain.ModelHistory;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import static cn.edu.buaa.act.workflow.common.Constant.MODEL_STATUS_UNPUBLISH;

/**
 * @author wsj
 */
@Getter
@Setter
public class ModelRepresentation{

  protected String id;
  protected String name;
  protected String key;
  protected String description;
  protected String createdBy;
  protected String lastUpdatedBy;
  protected Date lastUpdated;
  protected boolean latestVersion;
  protected int version;
  protected String comment;
  protected Integer modelType;
  protected String status;
  protected String processInstanceId;

  public ModelRepresentation(AbstractModel model) {
    initialize(model);
  }

  public ModelRepresentation() {

  }
  public void initialize(AbstractModel model) {
    this.id = model.getId();
    this.name = model.getName();
    this.key = model.getKey();
    this.description = model.getDescription();
    this.createdBy = model.getCreatedBy();
    this.lastUpdated = model.getLastUpdated();
    this.version = model.getVersion();
    this.lastUpdatedBy = model.getLastUpdatedBy();
    this.comment = model.getComment();
    this.modelType = model.getModelType();
    this.processInstanceId =model.getProcessInstanceID();
    if(model.getStatus()==null) {
      this.status = MODEL_STATUS_UNPUBLISH;
    } else {
      this.status = model.getStatus();
    }
    if (model instanceof Model) {
      this.setLatestVersion(true);
    } else if (model instanceof ModelHistory) {
      this.setLatestVersion(false);
    }
  }
  public Model toModel() {
    Model model = new Model();
    model.setName(name);
    model.setDescription(description);
    return model;
  }

  public void updateModel(Model model) {
    model.setDescription(this.description);
    model.setName(this.name);
  }
}
