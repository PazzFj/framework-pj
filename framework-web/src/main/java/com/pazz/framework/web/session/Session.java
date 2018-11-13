package com.pazz.framework.web.session;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Map;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 13:51
 * @description: ISession实现
 */
public class Session<V> implements ISession<V> {

    private HttpSession session;

    /**
     * 初始化HttpSession
     */
    @Override
    public void init(HttpSession session) {
        this.session = session;
    }

    /**
     * 设置session属性
     */
    @Override
    public void setObject(String k, V v) {
        validateSessionValue(v);//效验值
        session.setAttribute(k, v);
    }

    /**
     * 读取session属性
     */
    @Override
    public V getObject(String k) {
        Object obj = null;
        obj = session.getAttribute(k);
        return (V) obj;
    }

    /**
     * 使HttpSession 失效
     */
    @Override
    public void invalidate() {
        session.invalidate();
    }

    /**
     * 获取sessionId
     */
    @Override
    public String getId() {
        return session.getId();
    }

    /**
     * 设置其最大时效
     */
    @Override
    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    /**
     * 验证设置属性是否合法
     * validateSessionValue
     */
    void validateSessionValue(V v) {
        if (null == v) {
            return;
        }
        if (String.class == v.getClass()) {
            return;
        }
        if (Long.class == v.getClass()) {
            return;
        }
        if (Integer.class == v.getClass()) {
            return;
        }
        if (java.util.Date.class == v.getClass()) {
            return;
        }
        if (java.util.Locale.class == v.getClass()) {
            return;
        }
        //SessionValue注解标注的放过
        if (v.getClass().isAnnotationPresent(SessionValue.class)) {
            return;
        }
        //对于Collection及Map的遍历里面所有元素进行判断
        if (Collection.class.isAssignableFrom(v.getClass())) {
            Collection<?> collect = (Collection<?>) v;
            for (Object t : collect) {
                validateSessionValue((V) t);
            }
            return;
        }
        if (Map.class.isAssignableFrom(v.getClass())) {
            Map<?, ?> map = (Map<?, ?>) v;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                validateSessionValue((V) entry.getValue());
            }
            return;
        }
        throw new IllegalArgumentException();
    }
}
