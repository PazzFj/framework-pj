package com.pazz.framework.web.context;

import com.pazz.framework.cache.CacheManager;
import com.pazz.framework.cache.util.CacheUtil;
import com.pazz.framework.entity.IUser;

import java.util.Locale;

/**
 * 系统用户信息获得的上下文管理
 * 用户信息的ID存放于应用服务器的Session中
 * 通过Session的ID通过缓存获取用户
 * 缓存中没有指定用户信息时，会自动通过Provider去获取信息
 * 用户在缓存中存在的时候受DataReloader决定
 */
public final class UserContext {

    private static final ThreadLocal<IUser> USER_STORE = new ThreadLocal<>();//存储userName
    private static final ThreadLocal<Locale> USER_LOCALE = new ThreadLocal<>();
    private static final ThreadLocal<String> ORG_STORE = new ThreadLocal<>(); //存储orgCode

    /**
     * 设置当前用户与组织
     */
    public static void setUserContext(String userName, String orgCode){
        IUser user = (IUser) CacheManager.getInstance().getCache(CacheUtil.getUserCacheId()).get(userName);
        setUserContext(user, orgCode);
    }

    /**
     * 设置当前用户与组织
     */
    public static void setUserContext(IUser user, String orgCode){
        setCurrentUser(user);
        setCurrentOrgCode(orgCode);
    }

    /**
     * 不允许构造
     */
    private UserContext() {
        // TODO Auto-generated constructor stub
        super();
    }

    public static void setCurrentUser(IUser user){
        USER_STORE.set(user);
    }

    public static IUser getCurrentUser() {
        return USER_STORE.get();
    }

    public static void setCurrentOrgCode(String orgCode){
        ORG_STORE.set(orgCode);
    }

    public static String getCurrentOrgCode(){
        return ORG_STORE.get();
    }

    public static void setUserLocale(Locale locale){
        USER_LOCALE.set(locale);
    }

    public static Locale getUserLocale(){
        return USER_LOCALE.get();
    }

    /**
     * 清除值
     */
    public static void remove(){
        ORG_STORE.remove();
        USER_STORE.remove();
        USER_LOCALE.remove();
    }
}
