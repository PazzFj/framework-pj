package com.pazz.framework.samples.cache;

import com.pazz.framework.cache.redis.AbstractTTLRedisCache;
import com.pazz.framework.cache.redis.storage.RedisCacheStorage;
import com.pazz.framework.samples.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: 彭坚
 * @create: 2018/11/16 14:13
 * @description: User缓存存储
 */
public class UserCache extends AbstractTTLRedisCache<UserEntity> {

    public static final String UUID = UserCache.class.getName();

    @Override
    public String getUUID() {
        return UUID;
    }

    //注入缓存数据提供器
    @Autowired
    public void setCacheProvider(UserCacheProvider cacheProvider) {
        super.setCacheProvider(cacheProvider);
    }

    //注入缓存存储器
    @Autowired
    public void setCacheStorage(RedisCacheStorage<String, UserEntity> cacheStorage) {
        super.setCacheStorage(cacheStorage);
    }
}
