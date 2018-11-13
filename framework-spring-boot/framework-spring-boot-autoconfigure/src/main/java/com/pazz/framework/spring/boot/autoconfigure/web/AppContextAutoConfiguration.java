package com.pazz.framework.spring.boot.autoconfigure.web;

import com.pazz.framework.spring.boot.autoconfigure.FrameworkProperties;
import com.pazz.framework.web.listener.AppContextListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 10:34
 * @description: AppContext 自动装载
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({Servlet.class, AppContextListener.class})
@EnableConfigurationProperties({FrameworkProperties.class})
public class AppContextAutoConfiguration implements ApplicationContextAware {

    @Autowired
    private FrameworkProperties frameworkProperties;

    @Bean
    @ConditionalOnMissingBean(AppContextListener.class)
    public AppContextListener appContextListener() {
        return new AppContextListener(frameworkProperties.getStaticServerAddress(), frameworkProperties.getPackagePrefix());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        init(applicationContext);
    }

    private void init(ApplicationContext applicationContext) {

    }
}
