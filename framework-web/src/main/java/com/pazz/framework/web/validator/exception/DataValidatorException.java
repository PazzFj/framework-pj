package com.pazz.framework.web.validator.exception;

import com.pazz.framework.exception.BusinessException;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:34
 * @description: 数据验证异常
 */
public class DataValidatorException extends BusinessException {
    public DataValidatorException(String msg) {
        super(msg, msg);
    }
}
