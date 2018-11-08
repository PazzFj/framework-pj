package com.pazz.framework.cache.redis.exception;

/**
 * @author: Peng Jian
 * @create: 2018/11/8 10:39
 * @description: Redis连接异常
 */
public class RedisConnectionException extends RedisCacheStorageException {

    private static final long serialVersionUID = -148887209062926398L;

    public RedisConnectionException(String message) {
        super(message);
    }

    public RedisConnectionException(Throwable e) {
        super(e);
    }

    public RedisConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
