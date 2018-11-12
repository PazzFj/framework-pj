package com.pazz.framework.log;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 14:28
 * @description:
 */
public interface ILogBuffer {

    /**
     * 写日志对象
     * @param obj
     */
    void write(Object obj);

    /**
     * 获取队列大小
     * @return
     */
    public int getQueueSize();

    /**
     * 设置缓冲队列大小
     * @param queueSize
     */
    public void setQueueSize(int queueSize);

    /**
     * 获取日志列表大小
     * @return
     */
    public int getListSize();

    /**
     * 设置日志列表大小
     * @param listSize
     */
    public void setListSize(int listSize);

    /**
     * Buffer是否生效
     * @return
     */
    public boolean isEnable();

    /**
     * 设置buffer是否生效
     * @param enable
     */
    public void setEnable(boolean enable);

    /**
     * 获取日志发送器
     * @return
     */
    public ILogSender getLogSender();

    /**
     * 设置日志发送器
     * @param logSender
     */
    public void setLogSender(ILogSender logSender);

    /**
     * 传入分钟为单位
     *
     * @param interval
     * @throws
     */
    public void setInterval(long interval);

}
