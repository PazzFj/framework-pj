package com.pazz.framework.web.cache;

import com.pazz.framework.cache.DefaultStrongCache;
import com.pazz.framework.cache.provider.IBatchCacheProvider;
import com.pazz.framework.util.Properties;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:41
 * @description: 国际化缓存
 */
public class MessageCache extends DefaultStrongCache<String, Properties> {

    public static final String UUID = "/" + MessageCache.class.getName();

    @SuppressWarnings("unchecked")
    @javax.annotation.Resource(name = "messageCacheProvider")
    public void setCacheProvider(@SuppressWarnings("rawtypes") IBatchCacheProvider cacheProvider) {
        super.setCacheProvider(cacheProvider);
    }

    /**
     * <p>返回uuid 为/+类名称</p>
     */
    @Override
    public String getUUID() {
        return UUID;
    }

}
