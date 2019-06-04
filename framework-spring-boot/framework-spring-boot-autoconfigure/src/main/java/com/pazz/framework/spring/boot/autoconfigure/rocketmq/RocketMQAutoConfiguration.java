package com.pazz.framework.spring.boot.autoconfigure.rocketmq;

import com.pazz.framework.rocketmq.AbstractRocketMQProducer;
import com.pazz.framework.rocketmq.AbstractRocketMQPushConsumer;
import com.pazz.framework.util.string.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author: 彭坚
 * @create: 2018/11/15 17:12
 * @description: RocketMQ 自动装载
 */
@Configuration
@ConditionalOnBean(annotation = EnableRocketMQConfiguration.class)
@ConditionalOnClass(DefaultMQProducer.class)
@EnableConfigurationProperties(RocketMQProperties.class)
@AutoConfigureAfter({AbstractRocketMQProducer.class, AbstractRocketMQPushConsumer.class})
public class RocketMQAutoConfiguration implements ApplicationContextAware {

    private final Log log = LogFactory.getLog(getClass());

    protected ApplicationContext applicationContext;

    @Autowired
    private RocketMQProperties properties;

    private DefaultMQProducer producer;

    @PostConstruct
    public void init() throws Exception {
        //初始化生产者
        initProducer();
        //初始化消费者
        initConsumer();
    }

    private void initProducer() throws Exception {
        if (producer == null) {
            if (StringUtil.isEmpty(properties.getProducerGroup())) {
                throw new RuntimeException("请在配置文件中指定消息发送方group！");
            }
            producer = new DefaultMQProducer(properties.getProducerGroup());
            producer.setNamesrvAddr(properties.getNameServerAddress());
            producer.setVipChannelEnabled(properties.isVipChannelEnabled());
            producer.start();
        }
        //根据注解查询所有的  (beanName -> bean)
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RocketMQProducer.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            publishProducer(entry.getKey(), entry.getValue());
        }
    }

    private void initConsumer() throws Exception {
        if (properties.isConsumerEnable()) {
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RocketMQConsumer.class);
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                publishConsumer(entry.getKey(), entry.getValue());
            }
        }
    }

    private void publishConsumer(String beanName, Object bean) throws Exception {
        RocketMQConsumer mqConsumer = applicationContext.findAnnotationOnBean(beanName, RocketMQConsumer.class);
        if (StringUtil.isEmpty(mqConsumer.consumerGroup())) {
            throw new RuntimeException("consumer's consumerGroup must be defined");
        }
        if (StringUtil.isEmpty(mqConsumer.topic())) {
            throw new RuntimeException("consumer's topic must be defined");
        }
        String consumerGroup = applicationContext.getEnvironment().getProperty(mqConsumer.consumerGroup());
        if (StringUtil.isEmpty(consumerGroup)) {
            consumerGroup = mqConsumer.consumerGroup();
        }
        String topic = applicationContext.getEnvironment().getProperty(mqConsumer.topic());
        if (StringUtil.isEmpty(topic)) {
            topic = mqConsumer.topic();
        }
        if (!AbstractRocketMQPushConsumer.class.isAssignableFrom(bean.getClass())) {
            throw new RuntimeException(bean.getClass().getName() + " - consumer未实现IMQPushConsumer接口");
        }
        AbstractRocketMQPushConsumer abstractRocketMQPushConsumer = (AbstractRocketMQPushConsumer) bean;
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(properties.getNameServerAddress());
        consumer.setVipChannelEnabled(properties.isVipChannelEnabled());
        consumer.subscribe(topic, mqConsumer.tag());
        consumer.setInstanceName(abstractRocketMQPushConsumer.getInstanceName());
        consumer.setMessageModel(mqConsumer.messageMode());
        consumer.setConsumeFromWhere(abstractRocketMQPushConsumer.getConsumeFromWhere());
        consumer.setConsumeThreadMin(abstractRocketMQPushConsumer.getConsumeThreadMin());
        consumer.setConsumeThreadMax(abstractRocketMQPushConsumer.getConsumeThreadMax());
        consumer.setConsumeMessageBatchMaxSize(abstractRocketMQPushConsumer.getConsumeMessageBatchMaxSize());
        consumer.setPullBatchSize(abstractRocketMQPushConsumer.getPullBatchSize());
        consumer.registerMessageListener((List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) -> {
            AbstractRocketMQPushConsumer abstractMQPushConsumer = (AbstractRocketMQPushConsumer) bean;
            return abstractMQPushConsumer.dealMessage(list, consumeConcurrentlyContext);
        });
        consumer.start();
        log.info(String.format("%s is ready to subscribe message", bean.getClass().getName()));
    }

    private void publishProducer(String beanName, Object bean) throws Exception {
        if (!AbstractRocketMQProducer.class.isAssignableFrom(bean.getClass())) {
            throw new RuntimeException(beanName + " - producer未继承AbstractRocketMQProducer");
        }
        AbstractRocketMQProducer abstractMQProducer = (AbstractRocketMQProducer) bean;
        abstractMQProducer.setProducer(producer);
        //通过beanName 找到对应的注解class
        RocketMQProducer mqProducer = applicationContext.findAnnotationOnBean(beanName, RocketMQProducer.class);
        String topic = mqProducer.topic();
        if (!StringUtil.isEmpty(topic)) {
            String transTopic = applicationContext.getEnvironment().getProperty(topic);
            if (StringUtil.isEmpty(transTopic)) {
                abstractMQProducer.setTopic(topic);
            } else {
                abstractMQProducer.setTopic(transTopic);
            }
        }
        String tag = mqProducer.tag();
        if (!StringUtil.isEmpty(tag)) {
            String transTag = applicationContext.getEnvironment().getProperty(tag);
            if (StringUtil.isEmpty(transTag)) {
                abstractMQProducer.setTag(tag);
            } else {
                abstractMQProducer.setTag(transTag);
            }
        }
        log.info(String.format("%s is ready to produce message", beanName));
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
