package com.pazz.framework.log.exception;

import com.pazz.framework.exception.GeneralException;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 16:58
 * @description: Sender异常
 */
public class SenderException extends GeneralException {
    private static final long serialVersionUID = 479055979762346281L;

    public SenderException(final String msg) {
        super(msg);
    }

    public SenderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SenderException(final Throwable cause) {
        super(cause);
    }
}
