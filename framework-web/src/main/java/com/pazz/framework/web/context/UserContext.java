package com.pazz.framework.web.context;

import java.util.Locale;

/**
 * 系统用户信息获得的上下文管理
 * 用户信息的ID存放于应用服务器的Session中
 * 通过Session的ID通过缓存获取用户
 * 缓存中没有指定用户信息时，会自动通过Provider去获取信息
 * 用户在缓存中存在的时候受DataReloader决定
 */
public final class UserContext {

    private static final ThreadLocal<Locale> USER_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_STORE = new ThreadLocal<>();

    private UserContext() {
        // TODO Auto-generated constructor stub
        super();
    }

    /**
     * 清除值
     */
    public static void remove(){
        USER_LOCAL.remove();
        USER_STORE.remove();
    }
}
