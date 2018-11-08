package com.pazz.framework.cache.util;

import com.pazz.framework.cache.IFunctionCache;
import com.pazz.framework.cache.IUserCache;
import com.pazz.framework.holder.WebApplicationContextHolder;
import com.pazz.framework.util.string.StringUtil;

/**
 * @author: Peng Jian
 * @create: 2018/11/8 9:50
 * @description: Cache工具类
 */
public class CacheUtil {

    private static String userCacheId = null;
    private static String functionCacheId = null;

    public static String getUserCacheId() {
        if (StringUtil.isNotBlank(userCacheId)) {
            return userCacheId;
        }
        IUserCache userCache = WebApplicationContextHolder.getWebApplicationContext().getBean(IUserCache.class);
        userCacheId = userCache.getClass().getName();
        return userCacheId;
    }

    public static String getFunctionCacheId() {
        if (StringUtil.isNotBlank(functionCacheId)) {
            return functionCacheId;
        }
        IFunctionCache functionCache = WebApplicationContextHolder.getWebApplicationContext().getBean(IFunctionCache.class);
        functionCacheId = functionCache.getClass().getName();
        return functionCacheId;
    }
}
