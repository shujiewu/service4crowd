package cn.edu.buaa.act.data.exception;


import cn.edu.buaa.act.common.exception.BaseException;
import cn.edu.buaa.act.data.common.Constants;

/**
 * @author wsj
 */
public class SubmitInvalidException extends BaseException {
    public SubmitInvalidException(String message) {
        super(message, Constants.EX_USER_PASS_INVALID_CODE);
    }
}
