package com.pazz.framework.log.exception;

import com.pazz.framework.exception.GeneralException;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 16:53
 * @description: 日志缓冲初始化异常
 */
public class BufferedInitException extends GeneralException {
    private static final long serialVersionUID = -5280792305029168833L;

    public BufferedInitException(String msg) {
        super(msg);
    }
}
