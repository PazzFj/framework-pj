package com.pazz.framework.cache.provider;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 14:37
 * @description: TTL的缓存数据提供者
 */
public interface ITTLCacheProvider<V> {

    /**
     * @description: 加载单个元素
     * @author Peng Jian
     * @date 2018/11/7 14:39
     * @since V1.0.0
     */
    V get(String key);
}
