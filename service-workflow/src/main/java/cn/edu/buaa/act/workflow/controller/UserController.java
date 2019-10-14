package cn.edu.buaa.act.workflow.controller;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.workflow.exception.BadRequestException;
import cn.edu.buaa.act.workflow.exception.ConflictingRequestException;
import cn.edu.buaa.act.workflow.exception.NotFoundException;
import cn.edu.buaa.act.workflow.exception.NotPermittedException;
import cn.edu.buaa.act.workflow.model.CreateUserRepresentation;
import cn.edu.buaa.act.workflow.model.GroupRepresentation;
import cn.edu.buaa.act.workflow.model.UserRepresentation;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * UserController
 * 用户在系统注册的同时也要向activti引擎注册
 * @author wsj
 * @date 2018/10/6
 */
@RequestMapping(value = "/workflow")
@RestController
public class UserController {
    @Autowired
    protected IdentityService identityService;

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public User createNewUser(@RequestBody CreateUserRepresentation userRepresentation) {
        System.out.println(userRepresentation.getFirstName());
        if(StringUtils.isBlank(userRepresentation.getId()) || StringUtils.isBlank(userRepresentation.getFirstName())) {
            throw new BadRequestException("Id, first name are required");
        }
        if (userRepresentation.getId() != null && identityService.createUserQuery().userId(userRepresentation.getId()).count() > 0) {
            throw new ConflictingRequestException("User already registered", "ACCOUNT.SIGNUP.ERROR.ALREADY-REGISTERED");
        }
        User user = identityService.newUser(userRepresentation.getId() != null ? userRepresentation.getId() : userRepresentation.getEmail());
        user.setFirstName(userRepresentation.getFirstName());
        user.setLastName(userRepresentation.getLastName());
        user.setEmail(userRepresentation.getEmail());
        user.setPassword(userRepresentation.getPassword());
        identityService.saveUser(user);
        return user;
    }
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET, produces = "application/json")
    public UserRepresentation getUser(@PathVariable String userId, HttpServletResponse response) {
        User user = identityService.createUserQuery().userId(userId).singleResult();
        if (user == null) {
            throw new NotFoundException("User with id: " + userId + " does not exist or is inactive");
        }
        if (!user.getId().equals(BaseContextHandler.getUserID())) {
            throw new NotPermittedException("Can only get user details for authenticated user");
        }
        return new UserRepresentation(user);
    }
}
