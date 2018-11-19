package com.pazz.framework.spring.boot.autoconfigure.feign;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Peng Jian
 * @create: 2018/11/14 14:28
 * @description: Feign 自动装配
 */
@Configuration
@AutoConfigureBefore(FeignClientsConfiguration.class)
public class FeignClientAutoConfiguration {
//
//    @Bean
//    @Scope("prototype")
//    @ConditionalOnMissingBean
//    @ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
//    @ConditionalOnProperty(name = "feign.hystrix.enabled", matchIfMissing = false)
//    public Feign.Builder feignHystrixBuilder() {
//        return HystrixFeign.builder().setterFactory(new SetterFactory());
//    }
//
//    /**
//     * 请求id 拦截器
//     */
//    @Bean
//    @ConditionalOnMissingBean(RequestIdInterceptor.class)
//    public RequestIdInterceptor requestIdInterceptor() {
//        return new RequestIdInterceptor();
//    }
//
}
