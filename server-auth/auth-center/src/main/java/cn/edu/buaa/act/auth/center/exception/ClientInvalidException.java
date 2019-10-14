package cn.edu.buaa.act.auth.center.exception;


import cn.edu.buaa.act.auth.center.constant.Constants;
import cn.edu.buaa.act.common.constant.CommonConstants;
import cn.edu.buaa.act.common.exception.BaseException;

public class ClientInvalidException extends BaseException {
    public ClientInvalidException(String message) {
        super(message, Constants.EX_CLIENT_INVALID_CODE);
    }
}
