package com.pazz.framework.cache.exception;

import com.pazz.framework.exception.GeneralException;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 14:25
 * @description: key不存在异常
 */
public class KeyIsNotFoundException extends GeneralException {

    private static final long serialVersionUID = 963978060707131593L;

    public KeyIsNotFoundException(String msg) {
        super(msg);
    }
}
