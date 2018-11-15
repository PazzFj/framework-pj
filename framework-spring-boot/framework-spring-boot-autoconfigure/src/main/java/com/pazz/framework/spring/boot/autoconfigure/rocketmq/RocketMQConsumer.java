package com.pazz.framework.spring.boot.autoconfigure.rocketmq;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * RocketMQ 消费者注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface RocketMQConsumer {
    /**
     * 消费者组
     */
    String consumerGroup();

    /**
     * Topic
     */
    String topic();

    /**
     * Tag
     */
    String tag() default "*";

    /**
     * 消费模式
     */
    MessageModel messageMode() default MessageModel.CLUSTERING;
}
