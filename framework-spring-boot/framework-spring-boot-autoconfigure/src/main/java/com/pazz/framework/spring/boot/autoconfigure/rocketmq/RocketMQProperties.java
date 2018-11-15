package com.pazz.framework.spring.boot.autoconfigure.rocketmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: 彭坚
 * @create: 2018/11/15 17:15
 * @description: RocketMQ配置文件
 */
@Data
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMQProperties {
    /**
     * server 地址
     */
    private String nameServerAddress;
    /**
     * 生产者组
     */
    private String producerGroup;

    /**
     * VIP 通道是否启用
     */
    private boolean vipChannelEnabled = false;

    /**
     * 消费者是否可用
     */
    private boolean consumerEnable = true;
}
