package cn.edu.buaa.act.data.handler;


import cn.edu.buaa.act.common.exception.BaseException;
import cn.edu.buaa.act.common.msg.BaseResponse;
import cn.edu.buaa.act.data.exception.SubmitInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;


@ControllerAdvice
@ResponseBody
public class ExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);
    @org.springframework.web.bind.annotation.ExceptionHandler(SubmitInvalidException.class)
    public BaseResponse userInvalidExceptionHandler(HttpServletResponse response, SubmitInvalidException ex) {
        logger.info("处理提交异常");
        response.setStatus(200);
        logger.error(ex.getMessage(),ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }
}
