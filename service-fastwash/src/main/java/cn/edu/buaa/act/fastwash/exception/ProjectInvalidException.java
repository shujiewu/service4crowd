package cn.edu.buaa.act.fastwash.exception;



import cn.edu.buaa.act.common.exception.BaseException;
import cn.edu.buaa.act.fastwash.constant.Constants;

public class ProjectInvalidException extends BaseException {
    public ProjectInvalidException(String message) {
        super(message, Constants.PROJECT_INVALID_CODE);
    }
}
