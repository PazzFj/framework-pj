package com.pazz.framework.holder;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author: Peng Jian
 * @create: 2018/11/8 9:42
 * @description: WebApplicationContext持有类
 */
public class WebApplicationContextHolder implements ApplicationContextAware {

    private static volatile ApplicationContext context;
    private static final Object lock = new Object();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized(lock){
            context = applicationContext;
        }
    }

    public static WebApplicationContext getWebApplicationContext() {
        return (WebApplicationContext) context;
    }
}
