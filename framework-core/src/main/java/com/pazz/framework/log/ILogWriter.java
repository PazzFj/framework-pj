package com.pazz.framework.log;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 14:31
 * @description: 写日志接口
 */
public interface ILogWriter {

    /**
     * 写日志对象
     */
    void write(Object obj);
}
