package com.pazz.framework.spring.boot.autoconfigure.cas;

import com.google.common.collect.Lists;
import com.pazz.framework.cas.client.define.CasValidationTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: 彭坚
 * @create: 2018/11/16 10:14
 * @description: CAS client 配置
 */
@Data
@ConfigurationProperties(prefix = "framework.cas.client")
public class CasClientProperties {

    /**
     * 默认authenticationUrlPatterns
     */
    private final static String DEFAULT_AUTHENTICATION_URL = "/login/loginByCas.html";
    /**
     * 默认validationUrlPatterns
     */
    private final static String DEFAULT_VALIDATION_URL = "/login/loginByCas.html";

    private final static String ALL_URL = "/*";
    /**
     * 是否启用
     */
    private boolean enable = false;
    /**
     * CAS server URL E.g. https://example.com/cas or https://cas.example.
     */
    private String serverUrlPrefix;

    /**
     * CAS server login URL E.g. https://example.com/cas/login or https://cas.example/login
     */
    private String serverLoginUrl;

    /**
     * CAS-protected client application host URL E.g. https://myclient.example.com
     */
    private String clientHostUrl;

    /**
     * List of URL patterns protected by CAS authentication filter.
     */
    private List<String> authenticationUrlPatterns = Lists.newArrayList(DEFAULT_AUTHENTICATION_URL);

    /**
     * List of URL patterns protected by CAS validation filter.
     */
    private List<String> validationUrlPatterns = Lists.newArrayList(DEFAULT_VALIDATION_URL);

    /**
     * List of URL patterns protected by CAS request wrapper filter.
     */
    private List<String> requestWrapperUrlPatterns = Lists.newArrayList(ALL_URL);

    /**
     * List of URL patterns protected by CAS assertion thread local filter.
     */
    private List<String> assertionThreadLocalUrlPatterns = Lists.newArrayList(ALL_URL);

    /**
     * Authentication filter gateway parameter.
     */
    private Boolean gateway = false;

    /**
     * Validation filter useSession parameter.
     */
    private Boolean useSession = true;

    /**
     * Validation filter redirectAfterValidation.
     */
    private Boolean redirectAfterValidation = true;

    /**
     * Cas20ProxyReceivingTicketValidationFilter acceptAnyProxy parameter.
     */
    private Boolean acceptAnyProxy = true;

    /**
     * Cas20ProxyReceivingTicketValidationFilter encodeServiceUrl parameter
     */
    private Boolean encodeServiceUrl = false;

    /**
     * Cas20ProxyReceivingTicketValidationFilter allowedProxyChains parameter.
     */
    private List<String> allowedProxyChains = new ArrayList<>();

    /**
     * Cas20ProxyReceivingTicketValidationFilter proxyCallbackUrl parameter.
     */
    private String proxyCallbackUrl;

    /**
     * Cas20ProxyReceivingTicketValidationFilter proxyReceptorUrl parameter.
     */
    private String proxyReceptorUrl;

    /**
     * ValidationType the CAS protocol validation type. Defaults to CAS3 if not explicitly set.
     */
    private CasValidationTypeEnum validationType = CasValidationTypeEnum.CAS3;

}
