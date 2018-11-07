package com.pazz.framework.cache.storage;

import java.util.Map;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 14:40
 * @description: 抽象的数据缓存仓库
 */
public interface ICacheStorage<K, V> {

    /**
     * 存放数据
     */
    void set(K key, V value);

    /**
     * 存放多数据
     */
    void set(Map<K, V> values);

    /**
     * 获取数据
     */
    V get(K key);

    /**
     * 移除指定的数据
     */
    void remove(K key);

    /**
     * 移除所有的数据
     */
    void clear();

    /**
     * 获取多数据
     */
    Map<K, V> get();

    /**
     * 是否存在
     */
    Boolean exists(K key);

}
