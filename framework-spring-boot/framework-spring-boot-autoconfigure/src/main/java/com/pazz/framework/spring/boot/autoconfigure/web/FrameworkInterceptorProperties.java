package com.pazz.framework.spring.boot.autoconfigure.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 框架拦截器配置文件
 */
@Data
@ConfigurationProperties(prefix = "framework.web.interceptor")
public class FrameworkInterceptorProperties {

    /**
     * 拦截器可用
     */
    private boolean enabled;
    /**
     * 模块拦截器可用
     */
    private boolean moduleEnabled;
    /**
     * 模块排除
     */
    private String moduleExcludes;
    /**
     * 权限拦截器可用
     */
    private boolean securityEnabled;
    /**
     * cookie登录验证拦截器可用
     */
    private boolean cookieLoginEnabled;

}
