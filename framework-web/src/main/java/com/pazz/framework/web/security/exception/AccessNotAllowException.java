package com.pazz.framework.web.security.exception;

import com.pazz.framework.exception.GeneralException;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:10
 * @description: 访问拒绝异常
 */
public class AccessNotAllowException extends GeneralException {


    private static final long serialVersionUID = 8319129261389543262L;

    public static final String ERROR_CODE = "ERROR.SECURITY.NOTALLOW";

    public static final String MESSAGE = "Method not allow access";

    public AccessNotAllowException() {
        this(MESSAGE);
        super.errCode = ERROR_CODE;
    }

    public AccessNotAllowException(String msg) {
        super(msg);
        super.errCode = ERROR_CODE;
    }

}