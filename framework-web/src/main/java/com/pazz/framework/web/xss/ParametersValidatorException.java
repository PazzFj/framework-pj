package com.pazz.framework.web.xss;

import com.pazz.framework.exception.GeneralException;

public class ParametersValidatorException extends GeneralException {

    private static final long serialVersionUID = -7792624164333288395L;

    public ParametersValidatorException(String msg) {
        super(msg);
    }

    public ParametersValidatorException(Throwable cause) {
        super(cause);
    }

    public ParametersValidatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
