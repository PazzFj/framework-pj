package com.pazz.framework.spring.boot.autoconfigure.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: 彭坚
 * @create: 2018/11/11 15:49
 * @description: 缓存配置文件
 */
@ConfigurationProperties(prefix = "framework.cache")
public class CacheProperties {
    /**
     * 过期时间
     */
    private int expireAfter = 60 * 10;

    public int getExpireAfter() {
        return expireAfter;
    }

    public void setExpireAfter(int expireAfter) {
        this.expireAfter = expireAfter;
    }
}
