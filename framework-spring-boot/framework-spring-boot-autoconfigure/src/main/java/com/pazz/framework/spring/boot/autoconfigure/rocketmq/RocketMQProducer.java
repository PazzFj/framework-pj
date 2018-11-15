package com.pazz.framework.spring.boot.autoconfigure.rocketmq;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * RocketMQ 生产者注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface RocketMQProducer {
    /**
     * topic
     */
    String topic() default "";

    /**
     * tag
     */
    String tag() default "";
}
