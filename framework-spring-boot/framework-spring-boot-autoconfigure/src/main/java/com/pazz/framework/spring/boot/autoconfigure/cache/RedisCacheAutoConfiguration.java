package com.pazz.framework.spring.boot.autoconfigure.cache;

import com.pazz.framework.cache.CacheManager;
import com.pazz.framework.cache.redis.serializer.GenericJackson2JsonRedisSerializer;
import com.pazz.framework.cache.redis.storage.RedisCacheStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author: Peng Jian
 * @create: 2018/11/11 15:54
 * @description: Redis Cache 自动装载
 */
@Configuration
@ConditionalOnClass(CacheManager.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(CacheProperties.class)
public class RedisCacheAutoConfiguration {
    @Autowired
    private CacheProperties properties;

    @Bean
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @ConditionalOnMissingBean
    @ConditionalOnBean({RedisTemplate.class})
    public RedisCacheStorage redisCacheStorage(RedisTemplate redisTemplate) {
        RedisCacheStorage redisCacheStorage = new RedisCacheStorage();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisCacheStorage.setRedisTemplate(redisTemplate);
        redisCacheStorage.setExpire(properties.getExpireAfter());
        return redisCacheStorage;
    }

}
