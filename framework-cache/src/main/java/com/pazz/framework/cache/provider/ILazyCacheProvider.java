package com.pazz.framework.cache.provider;

import java.util.Date;
import java.util.Map;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 14:32
 * @description: 延迟加载缓存
 */
public interface ILazyCacheProvider<K, V> extends ICacheProvider<K, V> {

    /**
     * @description: 加载单个元素
     * @author Peng Jian
     * @date 2018/11/7 14:36
     * @since V1.0.0
     */
    V get(K ket);

    /**
     * @description: 加载最近被更新的数据
     * @author Peng Jian
     * @date 2018/11/7 14:37
     * @since V1.0.0
     */
    Map<K, V> getObjectUpdateMaps(Date time);

    /**
     * @description: 加载传入多个K的数据
     * @author Peng Jian
     * @date 2018/11/7 14:37
     * @since V1.0.0
     */
    Map<K, V> getObjectUpdateMaps(K... keys);

}
