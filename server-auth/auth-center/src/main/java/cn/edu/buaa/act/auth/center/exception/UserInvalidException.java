package cn.edu.buaa.act.auth.center.exception;


import cn.edu.buaa.act.auth.center.constant.Constants;
import cn.edu.buaa.act.common.exception.BaseException;

public class UserInvalidException extends BaseException {
    public UserInvalidException(String message) {
        super(message, Constants.EX_USER_PASS_INVALID_CODE);
    }
}
