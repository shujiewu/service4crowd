package cn.edu.buaa.act.auth.center.controller;

import cn.edu.buaa.act.auth.center.exception.UserInvalidException;
import cn.edu.buaa.act.auth.center.service.UserService;
import cn.edu.buaa.act.auth.center.util.JwtAuthenticationResponse;
import cn.edu.buaa.act.auth.center.util.TokenUtil;
import cn.edu.buaa.act.auth.center.vo.UserInfo;
import cn.edu.buaa.act.auth.center.vo.UserPermission;
import cn.edu.buaa.act.auth.common.util.IJWTInfo;
import cn.edu.buaa.act.auth.common.util.JWTInfo;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

/**
 * UserController
 *
 * @author wsj
 * @date 2018/9/8
 */

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @Value("${jwt.token-header}")
    private String tokenHeader;
    @Autowired
    TokenUtil tokenUtill;
    @ApiOperation(value="登录接口", notes="登录接口")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) throws Exception{
        UserInfo info = userService.validate(body.get("username"),body.get("password"));
        if (!StringUtils.isEmpty(info.getId())) {
            return ResponseEntity.ok(new JwtAuthenticationResponse(tokenUtill.generateToken(new JWTInfo(info.getUsername(), info.getId() + "", info.getName()))));
        }
        throw new UserInvalidException("用户不存在或账户密码错误!");
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ObjectRestResponse<String> register(HttpServletRequest request) throws Exception{
//        UserInfo info = userService.validate(body.get("username"),body.get("password"));
//        if (!StringUtils.isEmpty(info.getId())) {
//            return new ObjectRestResponse<>().data(tokenUtill.generateToken(new JWTInfo(info.getUsername(), info.getId() + "", info.getName())));
//        }
//        throw new UserInvalidException("用户不存在或账户密码错误!");\
//        System.out.println(request.getHeader(tokenHeader));
        return new ObjectRestResponse<String>();
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ObjectRestResponse<String> refreshAndGetAuthenticationToken(HttpServletRequest request) throws Exception {
        String oldToken = request.getHeader(tokenHeader);
        String refreshedToken = tokenUtill.generateToken(tokenUtill.getInfoFromToken(oldToken));
        return new ObjectRestResponse<>().data(refreshedToken);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntity<?> getUserPermission(HttpServletRequest request) throws Exception {
        String token = request.getHeader(tokenHeader);
        IJWTInfo userInfo = tokenUtill.getInfoFromToken(token);
        UserPermission userPermission =new UserPermission();
        BeanUtils.copyProperties(userInfo,userPermission);
        userPermission.setElements(new ArrayList<>());
        return ResponseEntity.ok(userPermission);
    }
}
