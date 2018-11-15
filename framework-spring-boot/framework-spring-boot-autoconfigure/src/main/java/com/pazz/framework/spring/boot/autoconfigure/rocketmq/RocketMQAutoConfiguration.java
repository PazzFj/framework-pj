package com.pazz.framework.spring.boot.autoconfigure.rocketmq;

import com.pazz.framework.rocketmq.RocketMQProducer;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: 彭坚
 * @create: 2018/11/15 17:12
 * @description: RocketMQ 自动装载
 */
@Configuration
@ConditionalOnBean(annotation = EnableRocketMQConfiguration.class)
@ConditionalOnClass(DefaultMQProducer.class)
@EnableConfigurationProperties(RocketMQProperties.class)
@AutoConfigureAfter(RocketMQProducer.class, )
@CommonsLog
public class RocketMQAutoConfiguration {


}
