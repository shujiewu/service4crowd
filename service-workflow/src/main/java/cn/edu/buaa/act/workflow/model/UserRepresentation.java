package cn.edu.buaa.act.workflow.model;

import lombok.Getter;
import lombok.Setter;
import org.activiti.engine.identity.User;

import java.util.ArrayList;
import java.util.List;


/**
 * @author wsj
 */
@Getter
@Setter
public class UserRepresentation {
  
  protected String id;
  protected String firstName;
  protected String lastName;
  protected String email;
  protected String fullName;
  protected List<GroupRepresentation> groups = new ArrayList<GroupRepresentation>();
  public UserRepresentation() {
    
  }

  public UserRepresentation(String userId) {
    if(userId!=null){
      setId(userId);
    }
  }
  public UserRepresentation(User user) {
  	if(user!=null){
  		setId(user.getId());
  		setFirstName(user.getFirstName());
  		setLastName(user.getLastName());
  		setFullName( (user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""));
  		setEmail(user.getEmail());
  	}
  }
}
