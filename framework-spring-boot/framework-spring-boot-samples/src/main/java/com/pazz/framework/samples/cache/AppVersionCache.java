package com.pazz.framework.samples.cache;

import com.pazz.framework.cache.DefaultStrongCache;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: 彭坚
 * @create: 2018/11/16 14:23
 * @description:
 */
public class AppVersionCache extends DefaultStrongCache {

    public static final String UUID = AppVersionCache.class.getName();

    @Override
    public String getUUID() {
        return UUID;
    }

    @Autowired
    public void setCacheProvider(AppVersionCacheProvider cacheProvider) {
        super.setCacheProvider(cacheProvider);
    }
}
