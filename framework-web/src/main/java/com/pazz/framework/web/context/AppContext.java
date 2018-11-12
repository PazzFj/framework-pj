package com.pazz.framework.web.context;

import com.pazz.framework.web.xss.ParametersValidator;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 18:03
 * @description: 应用上下文
 */
public class AppContext {

    /**
     * 应用名
     */
    private final String appName;
    /**
     * 静态资源地址
     */
    private final String staticServerAddress;
    /**
     * 上下文路径
     *
     */
    private final String contextPath;
    /**
     * 单例示例
     */
    private static AppContext context;
    /**
     * 参数校验,放置xss
     */
    private static ParametersValidator parametersValidator;
    /**
     * 包名前缀
     * 例如:com.pazz
     */
    private final String packagePrefix;

    public static ParametersValidator getParametersValidator() {
        return parametersValidator;
    }

    public static void setParametersValidator(ParametersValidator parametersValidator) {
        AppContext.parametersValidator = parametersValidator;
    }

    public String getAppName() {
        return appName;
    }

    public String getStaticServerAddress() {
        return staticServerAddress;
    }

    public String getContextPath() {
        return contextPath;
    }

    public  String getPackagePrefix() {
        return packagePrefix;
    }

    public static AppContext getAppContext() {
        return context;
    }

    public AppContext(String appName, String staticServerAddress, String contextPath,String packagePrefix) {
        this.appName = appName;
        this.packagePrefix = packagePrefix;
        this.staticServerAddress = staticServerAddress;
        this.contextPath = contextPath;
    }

    public static synchronized void initAppContext(String appName, String staticServerAddress, String contextPath,String packagePrefix) {
        if (context == null) {
            context = new AppContext(appName, staticServerAddress, contextPath,packagePrefix) {};
        }
    }

}
