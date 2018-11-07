package com.pazz.framework.cache.exception;

import com.pazz.framework.exception.GeneralException;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 14:26
 * @description: key存在，value为null
 */
public class ValueIsNullException extends GeneralException {

    private static final long serialVersionUID = 9035085457927187841L;

    public ValueIsNullException(String msg) {
        super(msg);
    }
}
