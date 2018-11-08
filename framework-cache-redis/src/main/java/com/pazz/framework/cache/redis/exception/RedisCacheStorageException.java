package com.pazz.framework.cache.redis.exception;

import com.pazz.framework.exception.GeneralException;

/**
 * @author: Peng Jian
 * @create: 2018/11/8 10:39
 * @description: 查询参数异常
 */
public class RedisCacheStorageException extends GeneralException {

    private static final long serialVersionUID = 4189989370221073043L;

    public RedisCacheStorageException(String message) {
        super(message);
    }

    public RedisCacheStorageException(Throwable e) {
        super(e);
    }

    public RedisCacheStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
