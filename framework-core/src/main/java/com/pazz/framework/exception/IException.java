package com.pazz.framework.exception;

/**
 * @author: 彭坚
 * @create: 2018/11/7 14:13
 * @description: 异常接口
 */
public interface IException {

    String getErrorCode(); //得到错误码

    String getNativeMessage(); //得到错误消息

    void setErrorArguments(Object... objects); //设置错误参数

    Object[] getErrorArguments(); //获取错误参数

}
