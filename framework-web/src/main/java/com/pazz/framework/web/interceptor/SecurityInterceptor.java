package com.pazz.framework.web.interceptor;

import com.pazz.framework.web.context.RequestContext;
import com.pazz.framework.web.security.SecurityAccessor;
import com.pazz.framework.web.security.SecurityNonCheckRequired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:05
 * @description: 权限检查拦截器
 */
public class SecurityInterceptor extends AbstractInterceptor {

    /**
     * 是否忽略检查权限表里没有映射的URL
     */
    private boolean ignoreUnstoredFunction = true;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod method = (HandlerMethod) handler;
        final String url = RequestContext.getCurrentContext().getRemoteRequestURL();
        if (!method.getMethod().isAnnotationPresent(SecurityNonCheckRequired.class) && method.getBean() instanceof AbstractController) {
            SecurityAccessor.checkURLAccessSecurity(url, this.ignoreUnstoredFunction);
        }
        return super.preHandle(request, response, handler);
    }

}
