package com.pazz.framework.spring.boot.autoconfigure.web;

import com.pazz.framework.web.filter.FrameworkFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 10:21
 * @description: 框架过滤器自动装配
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({Servlet.class, FrameworkFilter.class})
@AutoConfigureAfter(AppContextAutoConfiguration.class)
public class FrameworkFilterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "framework.web.filter", name = "enabled", matchIfMissing = true)
    public FrameworkFilter frameworkFilter() {
        return new FrameworkFilter();
    }

}
