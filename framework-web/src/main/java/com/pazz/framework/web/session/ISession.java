package com.pazz.framework.web.session;

import javax.servlet.http.HttpSession;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 13:49
 * @description: 自定义Session接口
 */
public interface ISession<V> {

    /**
     * 设置真实session
     * init
     */
    void init(HttpSession session);

    /**
     * 设置session属性
     * setObject
     */
    void setObject(String k, V v);

    /**
     * 读取session属性
     * getObject
     */
    V getObject(String k);

    /**
     * session失效
     * invalidate
     */
    void invalidate();

    /**
     * 获取sessionId
     */
    String getId();

    /**
     * 获取session有效期
     */
    int getMaxInactiveInterval();

}
