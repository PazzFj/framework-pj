package com.pazz.framework.cache.provider;

import java.util.Map;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 14:30
 * @description: 批量加载缓存接口
 */
public interface IBatchCacheProvider<K, V> extends ICacheProvider<K, V> {

    /**
     * @description: 批量加载数据
     * @author Peng Jian
     * @date 2018/11/7 14:31
     * @since V1.0.0
     */
    Map<K, V> get();
}
