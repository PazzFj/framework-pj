package com.pazz.framework.log;

import java.util.List;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 14:31
 * @description: 发日志接口
 */
public interface ILogSender {
    /**
     * 批量发送日志
     */
    void send(List<Object> msg);

    /**
     * 发送日志
     */
    void send(Object msg);
}
