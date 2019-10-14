package cn.edu.buaa.act.auth.common.exception;


import cn.edu.buaa.act.auth.common.constatns.CommonConstants;
import cn.edu.buaa.act.common.exception.BaseException;

public class UserTokenException extends BaseException {
    public UserTokenException(String message) {
        super(message, CommonConstants.EX_USER_INVALID_CODE);
    }
}
