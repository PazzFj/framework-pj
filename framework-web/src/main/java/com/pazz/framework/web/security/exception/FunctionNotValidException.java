package com.pazz.framework.web.security.exception;

import com.pazz.framework.exception.GeneralException;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:11
 * @description: 功能无效异常
 */
public class FunctionNotValidException extends GeneralException {


    private static final long serialVersionUID = 4272651544154079393L;

    public static final String ERROR_CODE = "ERROR.SECURITY.NOTVALID";

    public static final String MESSAGE = "Function is not valid";

    public FunctionNotValidException() {
        super(MESSAGE);
        super.errCode = ERROR_CODE;
    }

    public FunctionNotValidException(String message, Throwable cause) {
        super(message, cause);
        super.errCode = ERROR_CODE;
    }

    public FunctionNotValidException(String msg) {
        super(msg);
        super.errCode = ERROR_CODE;
    }

    public FunctionNotValidException(Throwable cause) {
        super(cause);
        super.errCode = ERROR_CODE;
    }

}