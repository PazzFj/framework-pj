package com.pazz.framework.samples.cache;

import com.pazz.framework.cache.provider.IBatchCacheProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: 彭坚
 * @create: 2018/11/16 14:23
 * @description: 版本缓存
 */
public class AppVersionCacheProvider implements IBatchCacheProvider<String, Object> {

    public static final String APP_VERSION_KEY = "appVersion";

    @Override
    public Map<String, Object> get() {
        return new HashMap() {
            {
                put(APP_VERSION_KEY, 1);
            }
        };
    }

    @Override
    public Date getLastModifyTime() {
        return new Date();
    }
}
