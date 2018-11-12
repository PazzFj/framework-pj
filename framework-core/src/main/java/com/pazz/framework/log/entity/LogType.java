package com.pazz.framework.log.entity;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 14:27
 * @description: 日志类型
 */
public class LogType {
    /**
     * struts http请求
     */
    public final static String WEB = "WEB";//完成
    /**
     * 系统业务方法
     */
    public final static String SERVICE = "SERVICE";//完成
    /**
     * 数据访问
     */
    public final static String DAO = "DAO";//完成
    /**
     * gui客户端
     */
    public final static String GUI = "GUI";//完成
    /**
     * pda设备
     */
    public final static String PDA = "PDA";
    /**
     * webservice接口访问
     */
    public final static String INTERFACE = "INTERFACE";
    /**
     * job定时任务
     */
    public final static String JOB = "JOB";
    /**
     * 异常
     */
    public final static String EXCEPTION = "EXCEPTION";//完成
    /**
     * 业务主动调用发送日志的类型
     */
    public final static String BUSINESS = "BUSINESS";
    /**
     * 审计
     */
    public final static String AUDIT = "AUDIT";
    /**
     * 性能
     */
    public final static String PERFORMANCE = "PERFORMANCE";//待定
    /**
     * esb通信
     */
    public final static String ESB = "ESB";
}
