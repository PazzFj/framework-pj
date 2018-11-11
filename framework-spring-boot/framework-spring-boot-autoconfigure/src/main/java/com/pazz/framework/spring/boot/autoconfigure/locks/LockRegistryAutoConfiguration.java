package com.pazz.framework.spring.boot.autoconfigure.locks;

import com.pazz.framework.locks.redis.RedisLockRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author: Peng Jian
 * @create: 2018/11/11 17:17
 * @description: Lock 自动装载
 */
@Configuration
@EnableConfigurationProperties(LockRegistryProperties.class)
public class LockRegistryAutoConfiguration {

    /**
     * Redis锁自动装载
     */
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Configuration
    @ConditionalOnBean(RedisConnectionFactory.class)
    @AutoConfigureAfter(RedisAutoConfiguration.class)
    @EnableConfigurationProperties(LockRegistryProperties.class)
    protected static class RedisLockRegistryAutoConfiguration {
        @Autowired
        private LockRegistryProperties properties;

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean(RedisConnectionFactory.class)
        public RedisLockRegistry defaultRedisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
            LockRegistryProperties.Redis redis = properties.getRedis();
            RedisLockRegistry redisLockRegistry = new RedisLockRegistry(redisConnectionFactory, redis.getRegistryKey(), redis.getExpireAfter(), redis.getDefaultExpireUnusedOlderThanTime());
            return redisLockRegistry;
        }
    }

    /**
     * Jdbc锁自动装载
     */
    @Configuration
    @ConditionalOnClass({DataSource.class, JdbcTemplate.class})
    @AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
    @Import(JdbcTemplateAutoConfiguration.class)
    @EnableConfigurationProperties(LockRegistryProperties.class)
    @ConditionalOnProperty(name = "framework.locks.jdbc.enable",havingValue = "true")
    protected  static class JdbcLockRegistryAutoConfiguration{
        @Autowired
        private LockRegistryProperties properties;

        @Bean
        @ConditionalOnMissingBean
        public DefaultLockRepository defaultLockRepository(JdbcTemplate jdbcTemplate){
            DefaultLockRepository defaultLockRepository = new DefaultLockRepository(jdbcTemplate);
            defaultLockRepository.setRegion(properties.getJdbc().getRegion());
            defaultLockRepository.setTableName(properties.getJdbc().getTableName());
            defaultLockRepository.setTimeToLive(properties.getJdbc().getTimeToLive());
            return defaultLockRepository;
        }

        @Bean
        @ConditionalOnMissingBean
        public JdbcLockRegistry jdbcLockRegistry(DefaultLockRepository lockRepository){
            return new JdbcLockRegistry(lockRepository);
        }
    }
}
