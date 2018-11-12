package com.pazz.framework.log.exception;

import com.pazz.framework.exception.GeneralException;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 16:30
 * @description: 日志缓冲状态异常
 */
public class BufferedStateException extends GeneralException {

    private static final long serialVersionUID = -5620265944987934559L;

    public BufferedStateException(String str) {
        super(str);
    }
}
