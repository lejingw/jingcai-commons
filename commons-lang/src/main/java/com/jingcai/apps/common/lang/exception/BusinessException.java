package com.jingcai.apps.common.lang.exception;

/**
 * Created by lejing on 15/7/14.
 */
public class BusinessException extends RuntimeException {
    private String code;
    private String message;

    public BusinessException(BusinessMessage businessMessage) {
        this.code = businessMessage.getCode();
        this.message = businessMessage.getMessage();
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}