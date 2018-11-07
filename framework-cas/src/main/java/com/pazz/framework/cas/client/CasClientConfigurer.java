package com.pazz.framework.cas.client;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 13:46
 * @description: CAS 客户端配置
 */
public interface CasClientConfigurer {

    /**
     * 配置 CAS authentication filter.
     *
     * @param authenticationFilter
     */
    void configureAuthenticationFilter(FilterRegistrationBean authenticationFilter);

    /**
     * 配置 CAS validation filter.
     *
     * @param validationFilter
     */
    void configureValidationFilter(FilterRegistrationBean validationFilter);

    /**
     * 配置 CAS http servlet wrapper filter.
     *
     * @param httpServletRequestWrapperFilter
     */
    void configureHttpServletRequestWrapperFilter(FilterRegistrationBean httpServletRequestWrapperFilter);

    /**
     * 配置 CAS assertion thread local filter.
     *
     * @param assertionThreadLocalFilter
     */
    void configureAssertionThreadLocalFilter(FilterRegistrationBean assertionThreadLocalFilter);

}
