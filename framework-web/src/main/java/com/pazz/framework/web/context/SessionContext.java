package com.pazz.framework.web.context;

import com.pazz.framework.define.Definitions;
import com.pazz.framework.web.session.ISession;
import com.pazz.framework.web.session.Session;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 13:46
 * @description: session上下文
 */
public final class SessionContext {

    private static final ThreadLocal<ISession<Object>> SAFE_SESSION = new ThreadLocal<ISession<Object>>() {
        @Override
        protected ISession<Object> initialValue() {
            return new Session<Object>();
        }
    };

    private SessionContext() {
    }

    /**
     * 设置真实session
     */
    public static void setSession(HttpSession session) {
        ISession sessionHold = SAFE_SESSION.get();
        sessionHold.init(session);
    }

    /**
     * 获取session
     */
    public static ISession<Object> getSession() {
        return SAFE_SESSION.get();
    }

    /**
     * 设置user
     */
    public static void setCurrentUser(String user) {
        SAFE_SESSION.get().setObject(Definitions.KEY_USER, user);
    }

    /**
     * 设置Locale
     */
    public static void setUserLocale(Locale userLocale) {
        SAFE_SESSION.get().setObject(Definitions.KEY_LOCALE, userLocale);
    }

    /**
     * 清除ThreadLocal
     */
    public static void remove() {
        SAFE_SESSION.get().init(null);
    }

    /**
     * 使session失效
     */
    public static void invalidateSession() {
        SAFE_SESSION.get().invalidate();
    }

}
