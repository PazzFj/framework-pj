package com.pazz.framework.log.entity;

import lombok.Data;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Date;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 14:22
 * @description: 系统日志信息
 */
public class LogInfo implements Serializable {
    private static final long serialVersionUID = -1866263417231366274L;

    public final static String BEGIN_ACTION = "BEGIN";

    public final static String END_ACTION = "END";

    /**
     * 日期时间 格式：yyyy-MM-dd hh-mm-ss-ms
     */
    private Date date;
    /**
     * requestId 起日志信息关联作用
     */
    private String requestId;
    /**
     * web.xml中的display-name
     */
    private String appName;
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 登录用户
     */
    private String userName;
    /**
     * 访问url
     */
    private String url;
    /**
     * 日志类型 从LogType中取值
     *
     * @see LogType#AUDIT
     * @see LogType#BUSINESS
     * @see LogType#DAO
     * @see LogType#EXCEPTION
     * @see LogType#INTERFACE
     * @see LogType#JOB
     * @see LogType#PERFORMANCE
     * @see LogType#SERVICE
     * @see LogType#WEB
     */
    private String type;
    /**
     * 类名
     */
    private String clazz;
    /**
     * 方法名
     */
    private String method;
    /**
     * ip地址
     */
    private String ip;
    /**
     * 主机名
     */
    private String hostName;
    /**
     * 主机地址
     */
    private String hostAddress;
    /**
     * 版本
     */
    private String version;
    /**
     * mac地址
     */
    private String macAddress;
    /**
     * 信息 可做预留字段
     */
    private String message;
    /**
     * 操作标记  从LogInfo中常量获取
     *
     * @see LogInfo#BEGIN_ACTION
     * @see LogInfo#END_ACTION
     */
    private String action;
    /**
     * 执行方法参数
     * <p>
     * json格式
     */
    private String args;
    /**
     * 执行方法返回值
     * <p>
     * json格式
     */
    private String result;

    public LogInfo() {
    }

    public Date newDate() {
        return new Date(System.currentTimeMillis());
    }

    public void setMessage(String message, Object... formatArgs) {
        this.message = (formatArgs == null || formatArgs.length == 0) ? message : MessageFormat.format(message, formatArgs);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(8);
        sb.append("LogInfo [");
        Field[] fields = this.getClass().getDeclaredFields();
        PropertyDescriptor pd = null;
        for(int i=0;i<fields.length;i++) {
            try {
                pd = new PropertyDescriptor(fields[i].getName(),this.getClass());
                Method m = pd.getReadMethod();
                Object value = m.invoke(this);
                sb.append(fields[i].getName()).append("=").append(value);
                if(i < fields.length - 1) {
                    sb.append(",");
                }
            } catch(Exception e) {
                continue;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
