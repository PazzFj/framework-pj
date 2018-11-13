package com.pazz.framework.define;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 14:23
 * @description: 框架定义类
 */
public final class Definitions {

    /**
     * 用户id key定义
     */
    public static final String KEY_USER = "FRAMEWORK_KEY_USER";
    /**
     * 国际化key 定义
     */
    public static final String KEY_LOCALE = "FRAMEWORK_KEY_LOCALE";
    /**
     * 请求key定义
     */
    public static final String KEY_REQUEST_URL = "FRAMEWORK_KEY_REQUEST_URL";
    /**
     * 请求key定义
     */
    // the client send request type | text/html;text/json;application-data/stream,etc
    public static final String KEY_REQUEST_TYPE ="FRAMEWORK_KEY_REQUEST_TYPE";
    /**
     * 角色缓存key
     */
    public static final String KEY_ROLE_CACHE = "FRAMEWORK_KEY_ROLE_CACHE";
    /**
     * 功能缓存key
     */
    public static final String KEY_FUNCTION_CACHE = "FRAMEWORK_KEY_FUNCTION_CACHE";
    /**
     * 用户缓存key
     */
    public static final String KEY_USER_CACHE = "FRAMEWORK_KEY_USER_CACHE";

    /**
     * spring加载bean时遇到id重复 通过该标识判断 是覆盖 还是抛出异常
     * true 允许覆盖
     * false 抛出异常
     */
    public static final String ALLOW_BEAN_DEFINITION_OVERRIDING = "allowBeanDefinitionOverriding";

    private Definitions() {
    }

}
