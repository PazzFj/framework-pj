package com.pazz.framework.web.listener;

import com.pazz.framework.web.context.AppContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 10:41
 * @description: 应用上下文监听器
 */
public class AppContextListener implements ServletContextListener {

    /**
     * 静态资源地址
     **/
    private String staticServerAddress;
    /**
     * 包名前缀
     **/
    private String packagePrefix;

    public AppContextListener() {
    }

    public AppContextListener(String staticServerAddress, String packagePrefix) {
        this.staticServerAddress = staticServerAddress;
        this.packagePrefix = packagePrefix;
    }

    /**
     * 初始化应用上下文
     *
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        AppContext.initAppContext(sc.getServletContextName(), staticServerAddress, sc.getContextPath(), packagePrefix);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
