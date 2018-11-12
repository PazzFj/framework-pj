package com.pazz.framework.spring.boot.autoconfigure.log;

import com.pazz.framework.log.entity.LogInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 14:17
 * @description: 日志配置文件
 */
@Data
@ConfigurationProperties(prefix = "framework.log")
public class LogProperties {

    /**
     * 过滤器配置
     */
    private LogFilterProperties filter = new LogFilterProperties();
    /**
     * 缓冲设置
     */
    private LogBufferProperties buffer = new LogBufferProperties();
    /**
     * mongo 日志配置
     */
    private MongoLogProperties mongo = new MongoLogProperties();
    /**
     * 日志异常配置
     */
    private LogExceptionProperties exception = new LogExceptionProperties();

    @Data
    public static class LogFilterProperties {
        /**
         * 是否启用
         */
        private boolean enable = true;
        /**
         * 是否打印日志
         */
        private boolean logPrint = true;
        /**
         * 是否打印请求request
         */
        private boolean logRequest = true;
        /**
         * 是否打印返回response
         */
        private boolean logResponse = true;
        /**
         * 是否包含header
         */
        private boolean includeHeaders = true;
        /**
         * 是否包含payload
         */
        private boolean includePayload = true;
        /**
         * 最大payload长度
         */
        private int maxPayloadLength = 10000;
        /**
         * 排除Url模式
         */
        private String excludeUrlPatterns;
    }

    @Data
    public static class LogBufferProperties {
        /**
         * 自动
         */
        private boolean enable = false;
        /**
         * 队列大小
         */
        private int queueSize = 20;
        /**
         * 集合长度
         */
        private int listSize = 5000;
        /**
         * 间隔
         */
        private long interval = 30L * 60 * 1000;
    }

    @Data
    public static class MongoLogProperties {
        /**
         * 集合名称
         */
        private String collectionName = LogInfo.class.getSimpleName();
        ;
        /**
         * TTL索引名称
         */
        private String collectionTTLIndexName = "index_date_";

        /**
         * TTL索引名称
         */
        private String collectionTTLIndexKey = "date";
        /**
         * TTL索引过期秒数
         * 默认15天
         */
        private int collectionTTLIndexExpireSeconds = 60 * 60 * 24 * 15;
        /**
         * 线程数量
         */
        private int threadSize = 5;
    }

    @Data
    public static final class LogExceptionProperties {
        /**
         * 是否记录业务异常
         */
        private boolean writeBusinessException = false;
        /**
         * 是否记录异常日志
         */
        private boolean writeException = true;
    }

}
