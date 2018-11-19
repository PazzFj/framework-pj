package com.pazz.framework.spring.boot.autoconfigure.actuate;

import com.pazz.framework.web.filter.ShutdownFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @author: 彭坚
 * @create: 2018/11/11 16:22
 * @description: Shutdown 自动装载
 */
@Configuration
public class ShutdownAutoConfiguration {

    @Bean
    @ConditionalOnClass(ShutdownFilter.class)
    @ConditionalOnProperty(value = "endpoints.shutdown.enabled", havingValue = "true")
    public FilterRegistrationBean shutdownFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new ShutdownFilter());
        registrationBean.setUrlPatterns(Collections.singleton("/shutdown"));
        return registrationBean;
    }

}
