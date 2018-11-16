package com.pazz.framework.spring.boot.autoconfigure.web;

import com.pazz.framework.spring.boot.autoconfigure.interceptor.FrameworkInterceptorAutoConfiguration;
import com.pazz.framework.sso.interceptor.CookieLoginCheckInterceptor;
import com.pazz.framework.web.AbstractWebMvcConfig;
import com.pazz.framework.web.converter.StringToDateConverter;
import com.pazz.framework.web.interceptor.ModuleInterceptor;
import com.pazz.framework.web.interceptor.SecurityInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * @author: 彭坚
 * @create: 2018/11/16 9:36
 * @description: WebMvc 配置
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnMissingBean(AbstractWebMvcConfig.class)
@Import(FrameworkInterceptorAutoConfiguration.class)
public class FrameworkWebMvcAutoConfiguration extends AbstractWebMvcConfig {

    /**
     * 模块化拦截器
     */
    private ModuleInterceptor moduleInterceptor;
    /**
     * 权限拦截器
     */
    private SecurityInterceptor securityInterceptor;
    /**
     * Cookie登录验证拦截器
     */
    private CookieLoginCheckInterceptor cookieLoginCheckInterceptor;

    public FrameworkWebMvcAutoConfiguration(ObjectProvider<ModuleInterceptor> moduleInterceptor, ObjectProvider<SecurityInterceptor> securityInterceptor, ObjectProvider<CookieLoginCheckInterceptor> cookieLoginCheckInterceptor) {
        this.moduleInterceptor = moduleInterceptor.getIfAvailable();
        this.securityInterceptor = securityInterceptor.getIfAvailable();
        this.cookieLoginCheckInterceptor = cookieLoginCheckInterceptor.getIfAvailable();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //增加模块拦截器
        if (moduleInterceptor != null) {
            registry.addInterceptor(moduleInterceptor);
        }
        //登录验证拦截器
        if (cookieLoginCheckInterceptor != null) {
            registry.addInterceptor(cookieLoginCheckInterceptor);
        }
        //增加权限验证拦截器
        if (securityInterceptor != null) {
            registry.addInterceptor(securityInterceptor);
        }
        super.addInterceptors(registry);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
        super.addFormatters(registry);
    }

}
