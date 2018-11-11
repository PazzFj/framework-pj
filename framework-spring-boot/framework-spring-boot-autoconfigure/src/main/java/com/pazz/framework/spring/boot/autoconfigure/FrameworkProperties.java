package com.pazz.framework.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: Peng Jian
 * @create: 2018/11/11 15:46
 * @description: 框架属性文件
 */
@ConfigurationProperties(prefix = "framework")
public class FrameworkProperties {
    /**
     * 静态资源地址
     *
     */
    private String staticServerAddress;
    /**
     * 包名前缀
     * 默认 com.pazz
     */
    private String packagePrefix = "com/pazz/";

    public String getStaticServerAddress() {
        return staticServerAddress;
    }

    public void setStaticServerAddress(String staticServerAddress) {
        this.staticServerAddress = staticServerAddress;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public void setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }
}
