
package cn.edu.buaa.act.workflow.model;

import lombok.Getter;
import lombok.Setter;
import org.activiti.engine.identity.Group;

@Getter
@Setter
public class GroupRepresentation {
  
  protected String id;
  protected String name;
  protected String type;
  
  public GroupRepresentation() {
  }
  
  public GroupRepresentation(Group group) {
    setId(group.getId());
    setName(group.getName());
    setType(group.getType());
  }
}
