package com.pazz.framework.spring.boot.autoconfigure.job;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: 彭坚
 * @create: 2018/11/19 13:42
 * @description: Job 任务调度
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(JobSchedulerRegistrar.class)
public @interface EnableJobScheduler {
}
