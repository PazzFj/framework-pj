package com.pazz.framework.web.cache;

import com.pazz.framework.cache.DefaultStrongCache;

import java.util.Map;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:39
 * @description: 动态国际化缓存
 */
public class AbstractDynamicMessageCache<K, V> extends DefaultStrongCache<String, Map<String, String>> {

    public static final String UUID = AbstractDynamicMessageCache.class.getName();

    @Override
    public String getUUID() {
        return AbstractDynamicMessageCache.UUID;
    }
}
