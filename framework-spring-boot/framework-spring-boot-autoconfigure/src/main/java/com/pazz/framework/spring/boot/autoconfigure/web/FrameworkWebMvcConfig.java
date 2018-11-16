package com.pazz.framework.spring.boot.autoconfigure.web;

import com.netflix.hystrix.HystrixCommand;
import com.pazz.framework.spring.boot.autoconfigure.feign.FeignClientAutoConfiguration;
import com.pazz.framework.spring.boot.autoconfigure.feign.RequestIdInterceptor;
import com.pazz.framework.spring.boot.autoconfigure.hystrix.SetterFactory;
import com.pazz.framework.web.AbstractWebMvcConfig;
import feign.Feign;
import feign.hystrix.HystrixFeign;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author: 彭坚
 * @create: 2018/11/16 9:36
 * @description: WebMvc 配置
 */
@Configuration
@AutoConfigureBefore(FeignClientAutoConfiguration.class)
public class FrameworkWebMvcConfig extends AbstractWebMvcConfig {

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    @ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
    @ConditionalOnProperty(name = "feign.hystrix.enabled", matchIfMissing = false)
    public Feign.Builder feignHystrixBuilder() {
        return HystrixFeign.builder().setterFactory(new SetterFactory());
    }

    /**
     * 请求id 拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public RequestIdInterceptor requestIdInterceptor() {
        return new RequestIdInterceptor();
    }

}
