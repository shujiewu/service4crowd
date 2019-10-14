package cn.edu.buaa.act.auth.center.service;

import cn.edu.buaa.act.auth.center.vo.UserInfo;
import org.springframework.stereotype.Service;

public interface UserService {
    public UserInfo validate(String username, String password);
}
