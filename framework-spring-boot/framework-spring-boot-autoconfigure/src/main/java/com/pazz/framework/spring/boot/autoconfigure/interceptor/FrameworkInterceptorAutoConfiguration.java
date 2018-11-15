package com.pazz.framework.spring.boot.autoconfigure.interceptor;

import com.pazz.framework.spring.boot.autoconfigure.web.FrameworkFilterAutoConfiguration;
import com.pazz.framework.sso.interceptor.CookieLoginCheckInterceptor;
import com.pazz.framework.web.filter.FrameworkFilter;
import com.pazz.framework.web.interceptor.ModuleInterceptor;
import com.pazz.framework.web.interceptor.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 拦截器自动装配
 */
@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter({FrameworkFilterAutoConfiguration.class})
@EnableConfigurationProperties(FrameworkInterceptorProperties.class)
@ConditionalOnProperty(prefix = "framework.web.interceptor", name = "enabled", matchIfMissing = true)
public class FrameworkInterceptorAutoConfiguration {

    @Autowired
    private FrameworkInterceptorProperties properties;

    /**
     * 模块化拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(FrameworkFilter.class)
    @ConditionalOnProperty(prefix = "framework.web.interceptor", name = "moduleEnabled", matchIfMissing = true)
    public ModuleInterceptor moduleInterceptor() {
        ModuleInterceptor moduleInterceptor = new ModuleInterceptor();
        moduleInterceptor.addExclude(properties.getModuleExcludes());
        return moduleInterceptor;
    }

    /**
     * 权限验证拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(FrameworkFilter.class)
    @ConditionalOnProperty(prefix = "framework.web.interceptor", name = "securityEnabled", matchIfMissing = true)
    public SecurityInterceptor securityInterceptor() {
        return new SecurityInterceptor();
    }

    /**
     * Cookie登录验证拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(FrameworkFilter.class)
    @ConditionalOnProperty(prefix = "framework.web.interceptor", name = "cookieLoginEnabled", matchIfMissing = false)
    public CookieLoginCheckInterceptor cookieLoginCheckInterceptor() {
        com.pazz.framework.sso.interceptor.CookieLoginCheckInterceptor cookieLoginCheckInterceptor = new CookieLoginCheckInterceptor();
        return cookieLoginCheckInterceptor;
    }

}
