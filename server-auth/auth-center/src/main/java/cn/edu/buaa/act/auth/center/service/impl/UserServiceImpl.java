package cn.edu.buaa.act.auth.center.service.impl;

import cn.edu.buaa.act.auth.center.biz.UserBiz;
import cn.edu.buaa.act.auth.center.constant.Constants;
import cn.edu.buaa.act.auth.center.exception.UserInvalidException;
import cn.edu.buaa.act.auth.center.model.User;
import cn.edu.buaa.act.auth.center.service.UserService;
import cn.edu.buaa.act.auth.center.vo.UserInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl
 *
 * @author wsj
 * @date 2018/9/9
 */

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserBiz userBiz;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(Constants.PW_ENCORDER);

    @Override
    public UserInfo validate(String username, String password){
        UserInfo info = new UserInfo();
        User user = userBiz.getUserByUsername(username);
        if (user!=null&&encoder.matches(password, user.getPassword())) {
            BeanUtils.copyProperties(user, info);
            info.setId(user.getId().toString());
        }
        return info;
    }
}
