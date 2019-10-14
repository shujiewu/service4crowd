package cn.edu.buaa.act.auth.common.exception;

import cn.edu.buaa.act.common.msg.BaseResponse;

import static cn.edu.buaa.act.auth.common.constatns.CommonConstants.TOKEN_ERROR_CODE;

public class TokenErrorResponse extends BaseResponse {
    public TokenErrorResponse(String message) {
        super(TOKEN_ERROR_CODE, message);
    }
}