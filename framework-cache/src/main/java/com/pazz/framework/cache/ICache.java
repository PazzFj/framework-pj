package com.pazz.framework.cache;

import java.util.Map;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 14:56
 * @description: 缓存接口
 */
public interface ICache<K, V> extends ICacheId {
    /**
     * 获取缓存
     */
    V get(K key);

    /**
     * 一次性取出所有内容
     */
    Map<K, V> get();

    /**
     * 失效一组缓存
     * 使旧的一组缓存全部失效
     * 如果是LRU的在下一次使用会自动加载最新的
     * 如果是Strong的会立即重新加载一次新的数据到缓存中
     */
    void invalid();

    /**
     * 失效key对应的缓存
     * 如果是LRU的会在下一次使用这个Key时自动加载最新的
     * 如果是Strong的会Throws RuntimeException异常，不允许失效部分数据
     */
    void invalid(K key);

    /**
     * 失效传入的多个key对应的缓存
     */
    void invalidMulti(K... keys);
}
