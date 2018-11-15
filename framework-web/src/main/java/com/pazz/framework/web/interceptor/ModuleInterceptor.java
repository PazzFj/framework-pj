package com.pazz.framework.web.interceptor;

import com.pazz.framework.util.CollectionUtils;
import com.pazz.framework.util.string.StringUtil;
import com.pazz.framework.web.context.AppContext;
import com.pazz.framework.web.context.RequestContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: 彭坚
 * @create: 2018/11/16 0:01
 * @description: 模块化拦截器
 */
public class ModuleInterceptor extends AbstractInterceptor {

    /**
     * 排除的模块，排除集成的其他第三方组件
     * 例如:hystrix等
     */
    private Set<String> excludes = new HashSet() {
        {
            add("hystrix");
        }
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String moduleName = "";
        String remoteReqURL = RequestContext.getCurrentContext().getRemoteRequestURL();
        if (StringUtil.isNotBlank(remoteReqURL)) {
            String[] array = remoteReqURL.split("/");
            if (array.length > 1) {
                moduleName = array[1];
            }
        }
        //将模块名设置到模块上下文中
        RequestContext.setCurrentContext(moduleName);

        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && StringUtil.isNotBlank(modelAndView.getViewName()) && !excludes.contains(RequestContext.getCurrentContext().getModuleName())) {
            modelAndView.setViewName(RequestContext.getCurrentContext().getModuleName() + "/" + modelAndView.getViewName());
        }
        String contextPath = AppContext.getAppContext().getContextPath();
        String moduleName = RequestContext.getCurrentContext().getModuleName();
        request.setAttribute("images", contextPath + "/images/" + moduleName);
        request.setAttribute("scripts", contextPath + "/scripts/" + moduleName);
        request.setAttribute("styles", contextPath + "/styles/" + moduleName);
        //设置静态资源服务器地址
        request.setAttribute("resource", AppContext.getAppContext().getStaticServerAddress());
        super.postHandle(request, response, handler, modelAndView);
    }

    public void addExclude(String moduleName) {
        if (StringUtil.isNotBlank(moduleName)) {
            String[] moduleNames = moduleName.split(",");
            this.excludes.addAll(CollectionUtils.arrayToList(moduleNames));
        }
    }

}
