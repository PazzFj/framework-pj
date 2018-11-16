package com.pazz.framework.rocketmq;

import com.pazz.framework.util.JsonUtils;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

/**
 * RocketMQ 消费者抽象基类
 */
@Data
public abstract class AbstractRocketMQPushConsumer<T> {

    protected final Log log = LogFactory.getLog(getClass());

    /**
     * 默认 UUID
     */
    private String instanceName = UUID.randomUUID().toString();
    /**
     * 消费点策略
     * 默认从队列尾部消费
     */
    private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;
    /**
     * 最小消费线程数量
     */
    private int consumeThreadMin = 20;
    /**
     * 最大消费线程数量
     */
    private int consumeThreadMax = 64;
    /**
     * 批量消费的最大消息条数
     */
    private int consumeMessageBatchMaxSize = 1;
    /**
     * 一次最大拉取的批量大小
     */
    private int pullBatchSize = 32;
    /**
     * 消息过期间隔时间
     * 默认24小时
     */
    private long consumeMessageExpireInterval = 1000 * 60 * 60 * 24;

    /**
     * 继承这个方法处理消息
     */
    public abstract boolean process(T object, MessageExt messageExt, ConsumeConcurrentlyContext consumeConcurrentlyContext);

    /**
     * 原生dealMessage方法，可以重写此方法自定义序列化和返回消费成功的相关逻辑
     *
     * @param list                       消息列表
     * @param consumeConcurrentlyContext 上下文
     */
    public ConsumeConcurrentlyStatus dealMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        for (MessageExt messageExt : list) {
            log.info("start consume message" + messageExt);
            if (System.currentTimeMillis() - messageExt.getBornTimestamp() > getConsumeMessageExpireInterval()) {
                log.info("message has already expire" + messageExt);
                //过期消息跳过
                continue;
            }
            T t = parseMessage(messageExt);
            if (null != t && !process(t, messageExt, consumeConcurrentlyContext)) {
                log.info("consume message fail" + messageExt);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            log.info("end consume message" + messageExt);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    /**
     * 反序列化解析消息
     *
     * @param message 消息体
     */
    private T parseMessage(MessageExt message) {
        if (message == null || message.getBody() == null) {
            return null;
        }
        final Type type = this.getMessageType();
        return JsonUtils.toObject(new String(message.getBody()), type);
    }

    /**
     * 解析消息类型
     */
    private Type getMessageType() {
        Type superType = this.getClass().getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            return ((ParameterizedType) superType).getActualTypeArguments()[0];
        } else {
            throw new RuntimeException("Unkown parameterized type.");
        }
    }

}
