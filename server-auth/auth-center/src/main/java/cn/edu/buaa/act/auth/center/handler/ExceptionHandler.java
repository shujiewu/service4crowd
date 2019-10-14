package cn.edu.buaa.act.auth.center.handler;


import cn.edu.buaa.act.auth.center.exception.ClientInvalidException;
import cn.edu.buaa.act.auth.center.exception.UserInvalidException;
import cn.edu.buaa.act.auth.common.constatns.CommonConstants;
import cn.edu.buaa.act.common.exception.BaseException;
import cn.edu.buaa.act.common.msg.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;


/**
 * @author wsj
 */
@ControllerAdvice
@ResponseBody
public class ExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

//    @org.springframework.web.bind.annotation.ExceptionHandler(ClientTokenException.class)
//    public BaseResxponse clientTokenExceptionHandler(HttpServletResponse response, ClientTokenException ex) {
//        response.setStatus(403);
//        logger.error(ex.getMessage(),ex);
//        return new BaseResponse(ex.getStatus(), ex.getMessage());
//    }
//
//    @org.springframework.web.bind.annotation.ExceptionHandler(UserTokenException.class)
//    public BaseResponse userTokenExceptionHandler(HttpServletResponse response, UserTokenException ex) {
//        response.setStatus(200);
//        logger.error(ex.getMessage(),ex);
//        return new BaseResponse(ex.getStatus(), ex.getMessage());
//    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserInvalidException.class)
    public BaseResponse userInvalidExceptionHandler(HttpServletResponse response, UserInvalidException ex) {
        response.setStatus(200);
        logger.error(ex.getMessage(),ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ClientInvalidException.class)
    public BaseResponse clientInvalidExceptionHandler(HttpServletResponse response, ClientInvalidException ex) {
        response.setStatus(200);
        logger.error(ex.getMessage(),ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BaseException.class)
    public BaseResponse baseExceptionHandler(HttpServletResponse response, BaseException ex) {
        logger.error(ex.getMessage(),ex);
        response.setStatus(500);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public BaseResponse otherExceptionHandler(HttpServletResponse response, Exception ex) {
        response.setStatus(500);
        logger.error(ex.getMessage(),ex);
        return new BaseResponse(CommonConstants.EX_OTHER_CODE, ex.getMessage());
    }

}
