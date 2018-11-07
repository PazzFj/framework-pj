package com.pazz.framework.cache.exception;

import com.pazz.framework.exception.GeneralException;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 14:25
 * @description: key存在，value为空
 */
public class ValueIsBlankException extends GeneralException {

    private static final long serialVersionUID = -8352370080877923393L;

    public ValueIsBlankException(String msg) {
        super(msg);
    }
}
