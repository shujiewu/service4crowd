package cn.edu.buaa.act.mlflow.handler;


import cn.edu.buaa.act.common.exception.BaseException;
import cn.edu.buaa.act.common.msg.BaseResponse;
import cn.edu.buaa.act.mlflow.exception.ExperimentAlreadyExistedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;


@ControllerAdvice
@ResponseBody
public class ExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(ExperimentAlreadyExistedException.class)
    public BaseResponse userInvalidExceptionHandler(HttpServletResponse response, ExperimentAlreadyExistedException ex) {
        return new BaseResponse(401, ex.getMessage());
    }
}
