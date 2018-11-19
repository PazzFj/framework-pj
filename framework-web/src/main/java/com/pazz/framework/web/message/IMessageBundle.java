package com.pazz.framework.web.message;

import java.util.Locale;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:37
 * @description: 国际化资源接口
 */
public interface IMessageBundle {

    /**
     * 根据键取得国际化资源，并格式化
     */
    String getMessage(String key, Object... args);

    /**
     * 取得指定地区的国际化资源
     */
    String getMessage(Locale locale, String key, Object... args);

    /**
     * 取得动态国际化资源并格式化
     */
    String getDynamicMessage(String key, Object... args);

    /**
     * 取得指定地区的动态国际化资源
     */
    String getDynamicMessage(Locale locale, String key, Object... args);

}
