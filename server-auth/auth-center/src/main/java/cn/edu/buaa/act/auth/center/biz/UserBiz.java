package cn.edu.buaa.act.auth.center.biz;

import cn.edu.buaa.act.auth.center.constant.Constants;
import cn.edu.buaa.act.auth.center.dao.UserMapper;
import cn.edu.buaa.act.auth.center.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author wsj
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserBiz extends BaseBiz<UserMapper,User> {

    @Override
    public void insertSelective(User entity) {
        String password = new BCryptPasswordEncoder(Constants.PW_ENCORDER).encode(entity.getPassword());
        entity.setPassword(password);
        super.insertSelective(entity);
    }

    public User getUserByUsername(String username){
        User user = new User();
        user.setUsername(username);
        return mapper.selectOne(user);
    }
}
