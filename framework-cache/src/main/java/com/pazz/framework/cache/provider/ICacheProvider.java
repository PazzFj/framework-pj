package com.pazz.framework.cache.provider;

import java.util.Date;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 14:29
 * @description: Cache数据提供接口
 */
public interface ICacheProvider<K, V> {

    /**
     * @description: 获取最后修改时间
     * @author Peng Jian
     * @date 2018/11/7 14:29
     * @since V1.0.0
     */
    Date getLastModifyTime();
}
