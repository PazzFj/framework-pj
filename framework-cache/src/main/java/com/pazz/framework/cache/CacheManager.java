package com.pazz.framework.cache;

import com.pazz.framework.cache.exception.CacheConfigException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 15:47
 * @description: 对缓存进行门面处理，所有的缓存策略通过应用根据场景去适配 应用只用实现对应的DataProvider 即可
 */
public final class CacheManager<K, V> {

    private static final CacheManager INSTANCE = new CacheManager();

    private final Map<String, ICache<K, V>> uuidCaches = new ConcurrentHashMap<>();

    private CacheManager() {
    }

    public static CacheManager getInstance() {
        return INSTANCE;
    }

    /**
     * 系统启动后自动注册所有的缓存类别
     */
    public void registerCacheProvider(ICache<K, V> cache) {
        // 不允许UUID重复，应用必须在实现的Cache接口确保命名不重复
        String uuid = cache.getUUID();
        if (uuidCaches.containsKey(uuid)) {
            throw new CacheConfigException("Dumplicate uuid " + uuid + " to cache provider " + cache.getClass().getName() + " and " + uuidCaches.get(uuid).getClass().getName());
        }
        uuidCaches.put(uuid, cache);
    }

    /**
     * 根据uuid获取缓存实例
     */
    public ICache<K, V> getCache(String uuid) {
        ICache<K, V> cache = uuidCaches.get(uuid);
        if (cache == null) {
            throw new CacheConfigException("No register cache provider for cache UUID " + uuid);
        }
        return cache;
    }

    public void shutdown() {
        uuidCaches.clear();
    }

}
