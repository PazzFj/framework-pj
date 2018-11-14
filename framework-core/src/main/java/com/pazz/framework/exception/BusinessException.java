package com.pazz.framework.exception;

import java.io.Serializable;

/**
 * @author: 彭坚
 * @create: 2018/11/7 14:12
 * @description: 业务异常基类
 */
public class BusinessException extends RuntimeException implements Serializable, IException {

    private static final long serialVersionUID = 1573938151665058611L;
    protected String errCode;
    private String nativeMsg;
    private Object[] arguments;

    public String getErrorCode() {
        return this.errCode;
    }

    public String getNativeMessage() {
        return this.nativeMsg;
    }

    public Object[] getErrorArguments() {
        return this.arguments;
    }

    public void setErrorArguments(Object... args) {
        this.arguments = args;
    }

    public BusinessException() {
    }

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BusinessException(String code, String msg) {
        super(msg);
        this.errCode = code;
    }

    public BusinessException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.errCode = code;
    }

    public BusinessException(String code, String msg, String natvieMsg) {
        super(msg);
        this.errCode = code;
        this.nativeMsg = natvieMsg;
    }

    public BusinessException(String code, String msg, String nativeMsg,
                             Throwable cause) {
        super(msg, cause);
        this.errCode = code;
        this.nativeMsg = nativeMsg;
    }

    public BusinessException(String code, Object... args) {
        this.errCode = code;
        this.arguments = args;
    }

    public BusinessException(String code, String msg, Object... args) {
        super(msg);
        this.errCode = code;
        this.arguments = args;
    }

}
