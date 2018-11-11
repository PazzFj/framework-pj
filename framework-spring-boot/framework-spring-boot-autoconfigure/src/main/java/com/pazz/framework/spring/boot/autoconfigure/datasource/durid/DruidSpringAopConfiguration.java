package com.pazz.framework.spring.boot.autoconfigure.datasource.durid;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author: Peng Jian
 * @create: 2018/11/11 16:48
 * @description: The Druid spring aop configuration.
 */
@ConditionalOnProperty("spring.datasource.druid.aop-patterns")
public class DruidSpringAopConfiguration {
    @Value("${spring.aop.proxy-target-class:false}")
    private boolean proxyTargetClass;

//    @Bean
//    public Advice advice(){
//        return new DruidStatInterceptor();
//    }
//
//    @Bean
//    public Advisor advisor(DruidProperties properties) {
//        return new RegexpMethodPointcutAdvisor(properties.getAopPatterns(), advice());
//    }
//
//    @Bean
//    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        advisorAutoProxyCreator.setProxyTargetClass(proxyTargetClass);
//        return advisorAutoProxyCreator;
//    }
}
