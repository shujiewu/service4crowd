package cn.edu.buaa.act.management.config;


import cn.edu.buaa.act.common.msg.BaseResponse;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.management.exception.NoServiceRegistrationException;
import cn.edu.buaa.act.management.exception.ParamException;
import cn.edu.buaa.act.management.exception.ServiceAlreadyDefinedException;
import cn.edu.buaa.act.management.exception.ServiceAlreadyRegisteredException;
import mesosphere.marathon.client.MarathonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
@ResponseBody
public class ExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(ServiceAlreadyRegisteredException.class)
    public BaseResponse userInvalidExceptionHandler(HttpServletResponse response, ServiceAlreadyRegisteredException ex) {
        Map<String,Object> result = new HashMap<>();
        result.put("success",false);
        result.put("message",ex.getMessage());
        return new ObjectRestResponse<>().data(result);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NoServiceRegistrationException.class)
    public BaseResponse noServiceRegistrationException(HttpServletResponse response, NoServiceRegistrationException ex) {
        Map<String,Object> result = new HashMap<>();
        result.put("success",false);
        result.put("message",ex.getMessage());
        return new ObjectRestResponse<>().data(result);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ParamException.class)
    public BaseResponse paramException(HttpServletResponse response, ParamException ex) {
        Map<String,Object> result = new HashMap<>();
        result.put("success",false);
        result.put("message",ex.getMessage());
        return new ObjectRestResponse<>().data(result);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ServiceAlreadyDefinedException.class)
    public BaseResponse serviceAlreadyDefinedException(HttpServletResponse response, ServiceAlreadyDefinedException ex) {
        Map<String,Object> result = new HashMap<>();
        result.put("success",false);
        result.put("message",ex.getMessage());
        return new ObjectRestResponse<>().data(result);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MarathonException.class)
    public BaseResponse serviceAlreadyDefinedException(HttpServletResponse response, MarathonException ex) {
        Map<String,Object> result = new HashMap<>();
        result.put("success",false);
        result.put("message",ex.getMessage());
        return new ObjectRestResponse<>().data(result);
    }
}
