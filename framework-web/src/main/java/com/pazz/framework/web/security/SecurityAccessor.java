package com.pazz.framework.web.security;

import com.pazz.framework.cache.CacheManager;
import com.pazz.framework.cache.ICache;
import com.pazz.framework.cache.util.CacheUtil;
import com.pazz.framework.entity.IFunction;
import com.pazz.framework.entity.IUser;
import com.pazz.framework.web.context.UserContext;
import com.pazz.framework.web.security.exception.AccessNotAllowException;
import com.pazz.framework.web.security.exception.UserNotLoginException;

import java.util.Set;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:09
 * @description: 权限访问控制器
 */
public final class SecurityAccessor {

    private SecurityAccessor() {
    }

    /**
     * 校验权限
     */
    public static void checkURLAccessSecurity(String accessURL) {
        checkURLAccessSecurity(accessURL, true);
    }

    /**
     * 校验权限
     */
    public static void checkURLAccessSecurity(String accessURL, boolean ignoreUnstoredFunction) {

        IUser user = UserContext.getCurrentUser();
        //用户未登录
        if (user == null) {
            throw new UserNotLoginException();
        }

        // 去掉多余的'/'
        if (accessURL != null) {
            accessURL = accessURL.replaceAll("[/]{2,}", "/");
        }
        IFunction function = getFunction(accessURL);
        if (function == null) {
            if (ignoreUnstoredFunction) {
                return;
            }

            throw new AccessNotAllowException(accessURL);
        }

        if (!function.getValidFlag()) {
            return;
        }
        Set<String> accessUris = user.queryAccessUris();

        // 是否拥有访问权限
        if (accessUris == null || !accessUris.contains(function.getUri())) {
            throw new AccessNotAllowException();
        }
    }

    @SuppressWarnings("unchecked")
    public static IFunction getFunction(String accessURL) {
        ICache<String, IFunction> functionCache = CacheManager.getInstance().getCache(CacheUtil.getFunctionCacheId());
        IFunction function = functionCache.get(accessURL);
        return function;
    }

    /**
     * 判断请求是否被允许
     */
    public static boolean hasAccessSecurity(String accessURL) {
        try {
            checkURLAccessSecurity(accessURL);
            return true;
        } catch (Exception t) {
            return false;
        }
    }
}
