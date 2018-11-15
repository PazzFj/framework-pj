package com.pazz.framework.web.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:28
 * @description: Web 返回结果
 */
@Data
public class Response<T> implements Serializable {

    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 是否业务异常
     */
    private boolean businessException;
    /**
     * 状态码
     */
    private String errorCode;
    /**
     * 信息
     */
    private String message;
    /**
     * 返回结果
     */
    private T result;
}
