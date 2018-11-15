package com.pazz.framework.rocketmq.exception;

/**
 * @author: 彭坚
 * @create: 2018/11/15 17:22
 * @description: RocketMQ 异常
 */
public class RocketMQException extends RuntimeException {

    private static final long serialVersionUID = 7449151827569059498L;

    public RocketMQException(String msg) {
        super(msg);
    }

    public RocketMQException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
