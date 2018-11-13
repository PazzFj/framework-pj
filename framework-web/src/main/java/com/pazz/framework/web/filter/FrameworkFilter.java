package com.pazz.framework.web.filter;

import com.pazz.framework.define.Definitions;
import com.pazz.framework.define.LocaleConst;
import com.pazz.framework.define.Protocol;
import com.pazz.framework.util.string.StringUtil;
import com.pazz.framework.web.context.AppContext;
import com.pazz.framework.web.context.RequestContext;
import com.pazz.framework.web.context.SessionContext;
import com.pazz.framework.web.context.UserContext;
import com.pazz.framework.web.context.XssConfigContext;
import com.pazz.framework.web.session.ISession;
import com.pazz.framework.web.xss.ParametersValidator;
import com.pazz.framework.web.xss.ParametersValidatorException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 10:23
 * @description: 框架过滤器
 */
public class FrameworkFilter extends DefaultFilter {

    private static ServletContext servletContext;

    /**
     * 初始化Filter，导出模块资源
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        getServletContext(filterConfig);
        ModuleManager.export(servletContext);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(XssConfigContext.EXPRESSION, filterConfig.getInitParameter(XssConfigContext.EXPRESSION));
        map.put(XssConfigContext.TACTICS, filterConfig.getInitParameter(XssConfigContext.TACTICS));
        map.put(XssConfigContext.PATH, filterConfig.getInitParameter(XssConfigContext.PATH));
        AppContext.setParametersValidator(new ParametersValidator(new XssConfigContext(map)));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        /** 取到HttpServletRequest,这里可以拿到HttpSession **/
        HttpServletRequest req = null;
        if (request instanceof HttpServletRequest) {
            req = (HttpServletRequest) request;
            try {
                AppContext.getParametersValidator().doValidator(req, (HttpServletResponse) response);
            } catch (ParametersValidatorException e) {
                return;
            }
            String remoteReqMethod = req.getHeader(Protocol.SECURITY_HEADER);
            String remoteReqURL = req.getRequestURI();
            String contextPath = req.getContextPath();
            /** 去掉应用名称，具体部署的应用名称是可变的 **/
            if (contextPath != null && !"/".equals(contextPath) && remoteReqURL.startsWith(contextPath)) {
                remoteReqURL = remoteReqURL.substring(contextPath.length());
            }
            /** 将当前访问的路径和远程头信息放入权限上下文 **/
            RequestContext.setCurrentContext(remoteReqMethod, remoteReqURL, getIp(req));
            if (StringUtil.isNotBlank(req.getHeader(RequestContext.REQUEST_ID))) {
                //设置请求id
                RequestContext.getCurrentContext().setRequestId(req.getHeader(RequestContext.REQUEST_ID));
            }
            /** 请求响应设置requestId信息 **/
            ((HttpServletResponse) response).setHeader(RequestContext.REQUEST_ID, RequestContext.getCurrentContext().getRequestId());
            /** 会话保留到SessionContext，以便各层使用 **/
            SessionContext.setSession(req.getSession());
            ISession session = SessionContext.getSession();
            // set locale to usercontext
            Locale locale = (Locale) session.getObject(Definitions.KEY_LOCALE);
            // get request locale
            if (locale == null) {
                locale = req.getLocale();
                session.setObject(Definitions.KEY_LOCALE, locale);
            }
            String localeLanguage = req.getParameter(LocaleConst.KEY_LOCALE_LANGUAGE); //语言
            String localeCountry = req.getParameter(LocaleConst.KEY_LOCALE_COUNTRY); //国家
            if (localeLanguage != null && localeCountry != null) {
                locale = new Locale(localeLanguage, localeCountry);
                session.setObject(Definitions.KEY_LOCALE, locale);
            }
            UserContext.setUserLocale(locale);
        }

        try {
            super.doFilter(request, response, chain);
        } finally {
            // 清除ThreadLocal中持有的信息
            SessionContext.remove();
            RequestContext.remove();
            UserContext.remove();
        }
    }

    /**
     * 得到IP地址
     */
    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static void getServletContext(FilterConfig config) {
        servletContext = config.getServletContext();
    }
}
