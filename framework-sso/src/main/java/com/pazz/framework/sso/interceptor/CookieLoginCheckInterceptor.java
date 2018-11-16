package com.pazz.framework.sso.interceptor;

import com.pazz.framework.sso.annotation.CookieNonCheckRequired;
import com.pazz.framework.sso.define.SSOConstants;
import com.pazz.framework.util.string.StringUtil;
import com.pazz.framework.web.context.SessionContext;
import com.pazz.framework.web.context.UserContext;
import com.pazz.framework.web.interceptor.AbstractInterceptor;
import com.pazz.framework.web.security.exception.UserNotLoginException;
import com.pazz.framework.web.session.ISession;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:24
 * @description: Cookie登录验证拦截器
 */
public class CookieLoginCheckInterceptor extends AbstractInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod method = (HandlerMethod) handler;
        ISession session = SessionContext.getSession();
        //当前用户
        String userName = (String) session.getObject(SSOConstants.KEY_CURRENT_USER_NAME);
        //当前组织
        String orgCode = (String) session.getObject(SSOConstants.KEY_CURRENT_ORG_CODE);
        if (StringUtil.isNotBlank(userName)) {
            //初始化当前用户组织
            UserContext.setUserContext(userName, orgCode);
            UserContext.getCurrentUser().loadAccessUris();
        }
        if (method.getBean() instanceof AbstractController && !method.getMethod().isAnnotationPresent(CookieNonCheckRequired.class)) {
            if (UserContext.getCurrentUser() == null) {
                throw new UserNotLoginException();
            }
        }
        return super.preHandle(request, response, handler);
    }

}
