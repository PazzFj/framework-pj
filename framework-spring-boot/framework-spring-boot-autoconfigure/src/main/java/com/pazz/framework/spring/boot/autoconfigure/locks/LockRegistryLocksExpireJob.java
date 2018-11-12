package com.pazz.framework.spring.boot.autoconfigure.locks;

import com.pazz.framework.locks.ExpirableLockRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 13:50
 * @description: 布式锁注册器失效Job
 */
public class LockRegistryLocksExpireJob implements ApplicationContextAware {

    private Log log = LogFactory.getLog(getClass());

    private ApplicationContext applicationContext;

    /**
     * 定时失效 LockRegistryLocks中的 locks
     * 默认执行时间一小时 1000 * 60 * 60
     */
    @Scheduled(fixedDelayString = "${framework.locks.expireJobFixedDelay:3600000}")
    public void expireLockRegistryLocks() {
        try {
            String[] beanNames = applicationContext.getBeanNamesForType(ExpirableLockRegistry.class);
            for (String beanName : beanNames) {
                ExpirableLockRegistry expirableLockRegistry = applicationContext.getBean(beanName, ExpirableLockRegistry.class);
                expirableLockRegistry.expireUnusedOlderThan(expirableLockRegistry.getDefaultExpireUnusedOlderThanTime());
            }
        } catch (Exception e) {
            log.error("Error expire lockRegistry locks", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
