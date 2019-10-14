package cn.edu.buaa.act.workflow.util;


import cn.edu.buaa.act.common.context.BaseContextHandler;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.UserEntityImpl;

/**
 * @author wsj
 * 尚未完成
 */
public final class SecurityUtils {

  private SecurityUtils() {
  }

  public static String getCurrentUserId() {
    User user= new UserEntityImpl();
    user.setId(BaseContextHandler.getUserID());
    if (user != null) {
      return user.getId();
    }
    return null;
  }
  public static User getCurrentUserObject() {
    User user= new UserEntityImpl();
    user.setId(BaseContextHandler.getUserID());
    return user;
  }
}
