package cn.edu.buaa.act.workflow.model;

import cn.edu.buaa.act.workflow.domain.Template;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author wsj
 */
@Getter
@Setter
public class TemplateRepresentation{

  protected String id;
  protected String name;
  protected String description;
  protected String createdBy;
  protected Date created;
  protected Date updated;
  protected boolean latestVersion;
  protected int version;

  public TemplateRepresentation(Template template) {
    this.id = template.getId();
    this.name = template.getName();
    this.description = template.getDescription();
    this.createdBy = template.getCreatedBy();
    this.updated = template.getUpdated();
    this.version = template.getVersion();
    this.created = template.getCreated();
  }

  public TemplateRepresentation() {
  }
}
