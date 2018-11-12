package com.pazz.framework.web.context;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 17:41
 * @description: 请求的上下文信息
 */
public final class RequestContext {

    //请求ID
    public static final String REQUEST_ID = "requestId";
    //正则表达式
    private static final String regEx = "[^a-zA-Z0-9_]";
    //模式
    private static Pattern p = Pattern.compile(regEx);
    //当前对象
    private static ThreadLocal<RequestContext> context = new ThreadLocal<RequestContext>() {
        @Override
        protected RequestContext initialValue() {
            return new RequestContext();
        }
    };
    //远程调用请求的方法名称和url
    private String remoteReqMethod;
    //远程请求url
    private String remoteReqURL;
    //请求的模块名称
    private String moduleName;
    //请求id
    private String requestId;
    //ip
    private String ip;

    private RequestContext() {
        this.requestId = UUID.randomUUID().toString();
    }

    /**
     * 当前上下文
     */
    public static RequestContext getCurrentContext() {
        return context.get();
    }

    /**
     * 清除ThreadLocal
     * remove
     */
    public static void remove() {
        context.remove();
    }

    /**
     * 设置请求方法、请求URL
     */
    public static void setCurrentContext(String remoteReqMethod, String remoteReqURL) {
        setCurrentContext(remoteReqMethod, remoteReqURL, null);
    }

    /**
     * 设置请求方法、请求URL、IP
     */
    public static void setCurrentContext(String remoteReqMethod, String remoteReqURL, String ip) {
        RequestContext requestContext = getCurrentContext();
        requestContext.remoteReqMethod = remoteReqMethod;
        requestContext.remoteReqURL = remoteReqURL;
        requestContext.ip = ip;
    }

    /**
     * 修改模块名称
     */
    public static void setCurrentContext(String moduleName) {
        RequestContext requestContext = getCurrentContext();
        // fix 去掉特殊字符 by 向延楷
        Matcher m = p.matcher(moduleName);
        requestContext.moduleName = m.replaceAll("").trim();
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getRemoteRequestMethod() {
        return this.remoteReqMethod;
    }

    public String getRemoteRequestURL() {
        return this.remoteReqURL;
    }

    public String getIp() {
        return this.ip;
    }
}
