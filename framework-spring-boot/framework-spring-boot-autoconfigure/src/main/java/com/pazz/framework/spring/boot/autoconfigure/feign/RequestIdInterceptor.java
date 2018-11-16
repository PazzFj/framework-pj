package com.pazz.framework.spring.boot.autoconfigure.feign;

import com.pazz.framework.web.context.RequestContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author: 彭坚
 * @create: 2018/11/16 10:07
 * @description: RequestId 拦截器 自动注册参照{@link FeignClientAutoConfiguration}
 */
public class RequestIdInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(RequestContext.REQUEST_ID, RequestContext.getCurrentContext().getRequestId());
    }
}
