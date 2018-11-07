package com.pazz.framework.cache.storage;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 14:42
 * @description: 远程缓存接口
 */
public interface IRemoteCacheStorage<K, V> {

    /**
     * 主动向Cache更新指定的数据
     */
    boolean set(K key, V value);

    /**
     * 主动向Cache更新指定的数据,指定过期时间
     */
    boolean set(K key, V value, int exp);

    /**
     * 获取缓存
     */
    V get(K key);

    /**
     * 删除指定的缓存信息
     */
    void remove(K key);

    /**
     * 删除多个key的缓存信息
     */
    void removeMulti(K... keys);

}
