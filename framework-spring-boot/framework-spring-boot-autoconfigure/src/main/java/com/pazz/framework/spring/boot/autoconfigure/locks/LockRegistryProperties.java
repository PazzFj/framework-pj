package com.pazz.framework.spring.boot.autoconfigure.locks;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: 彭坚
 * @create: 2018/11/11 17:09
 * @description: 锁注册器配置文件
 */
@Data
@ConfigurationProperties(prefix = "framework.locks")
public class LockRegistryProperties {
    /**
     * 失效本地锁Job执行时间
     */
    private long expireJobFixedDelay = 3600000;
    /**
     * 默认 Jdbc 锁配置
     */
    private Jdbc jdbc = new Jdbc();
    /**
     * 默认 Redis 锁配置
     */
    private Redis redis = new Redis();

    @Data
    public static class Redis {
        /**
         * 默认可用
         */
        private boolean enable = true;
        /**
         * 默认key值
         */
        private String registryKey = "locks:default";
        /**
         * 默认 1分钟
         */
        private long expireAfter = 60000;
        /**
         * 默认 2小时
         */
        private long defaultExpireUnusedOlderThanTime = 1000 * 60 * 60 * 2;
    }

    @Data
    public static class Jdbc {
        /**
         * 是否可用
         * 默认不可用
         */
        private boolean enable = false;
        /**
         * 失效时间 默认10秒
         */
        private int timeToLive = 10000;
        /**
         * 表名
         */
        private String tableName = "T_LOCK";
        /**
         * 范围
         */
        private String region = "DEFAULT";
    }

}
