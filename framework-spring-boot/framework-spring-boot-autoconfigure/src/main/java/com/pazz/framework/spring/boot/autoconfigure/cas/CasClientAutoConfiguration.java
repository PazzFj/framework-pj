package com.pazz.framework.spring.boot.autoconfigure.cas;

import com.google.common.collect.Lists;
import com.pazz.framework.cas.client.CasClientConfigurer;
import com.pazz.framework.cas.client.define.CasValidationTypeEnum;
import com.pazz.framework.spring.boot.autoconfigure.web.FrameworkFilterAutoConfiguration;
import com.pazz.framework.util.CollectionUtils;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.authentication.Saml11AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.jasig.cas.client.validation.Saml11TicketValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pazz.framework.cas.client.define.CasValidationTypeEnum.CAS;
import static com.pazz.framework.cas.client.define.CasValidationTypeEnum.CAS3;

/**
 * @author: 彭坚
 * @create: 2018/11/16 10:14
 * @description: Cas 客户端自动装载
 */
@Configuration
@EnableConfigurationProperties(CasClientProperties.class)
@AutoConfigureAfter(FrameworkFilterAutoConfiguration.class)
@ConditionalOnProperty(name = "framework.cas.client.enable", havingValue = "true")
public class CasClientAutoConfiguration {

    @Autowired
    private CasClientProperties properties;

    private CasClientConfigurer casClientConfigurer;

    @Bean
    public SingleSignOutHttpSessionListener singleSignOutHttpSessionListener() {
        SingleSignOutHttpSessionListener singleSignOutHttpSessionListener = new SingleSignOutHttpSessionListener();
        return singleSignOutHttpSessionListener;
    }

    @Bean
    public FilterRegistrationBean singleSignOutFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        filterRegistrationBean.setFilter(singleSignOutFilter);
        filterRegistrationBean.setUrlPatterns(Lists.newArrayList("/login/loginByCas.html"));
        filterRegistrationBean.addInitParameter("casServerUrlPrefix", this.properties.getServerUrlPrefix());
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean casAuthenticationFilter() {
        final FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        final Filter targetCasAuthnFilter =
                (this.properties.getValidationType() == CAS || properties.getValidationType() == CAS3) ? new AuthenticationFilter()
                        : new Saml11AuthenticationFilter();

        initFilter(authenticationFilter,
                targetCasAuthnFilter,
                constructInitParams("casServerLoginUrl", this.properties.getServerLoginUrl(), this.properties.getClientHostUrl()),
                this.properties.getAuthenticationUrlPatterns());

        if (this.properties.getGateway() != null) {
            authenticationFilter.getInitParameters().put("gateway", String.valueOf(this.properties.getGateway()));
        }

        if (this.casClientConfigurer != null) {
            this.casClientConfigurer.configureAuthenticationFilter(authenticationFilter);
        }
        return authenticationFilter;
    }

    @Bean
    public FilterRegistrationBean casValidationFilter() {
        final FilterRegistrationBean validationFilter = new FilterRegistrationBean();
        final Filter targetCasValidationFilter;
        switch (this.properties.getValidationType()) {
            case CAS:
                targetCasValidationFilter = new Cas20ProxyReceivingTicketValidationFilter();
                break;
            case CAS3:
                targetCasValidationFilter = new Cas30ProxyReceivingTicketValidationFilter();
                break;
            case SAML:
                targetCasValidationFilter = new Saml11TicketValidationFilter();
                break;
            default:
                throw new IllegalStateException("Unknown CAS validation type");
        }

        initFilter(validationFilter,
                targetCasValidationFilter,
                constructInitParams("casServerUrlPrefix", this.properties.getServerUrlPrefix(), this.properties.getClientHostUrl()),
                this.properties.getValidationUrlPatterns());

        if (this.properties.getUseSession() != null) {
            validationFilter.getInitParameters().put("useSession", String.valueOf(this.properties.getUseSession()));
        }
        if (this.properties.getRedirectAfterValidation() != null) {
            validationFilter.getInitParameters().put("redirectAfterValidation", String.valueOf(this.properties.getRedirectAfterValidation()));
        }
        if (this.properties.getEncodeServiceUrl() != null) {
            validationFilter.getInitParameters().put("encodeServiceUrl", String.valueOf(this.properties.getEncodeServiceUrl()));
        }
        //Proxy tickets validation
        if (this.properties.getAcceptAnyProxy() != null) {
            validationFilter.getInitParameters().put("acceptAnyProxy", String.valueOf(this.properties.getAcceptAnyProxy()));
        }
        if (this.properties.getAllowedProxyChains().size() > 0) {
            validationFilter.getInitParameters().put("allowedProxyChains", StringUtils.collectionToDelimitedString(this.properties.getAllowedProxyChains(), " "));
        }
        if (this.properties.getProxyCallbackUrl() != null) {
            validationFilter.getInitParameters().put("proxyCallbackUrl", this.properties.getProxyCallbackUrl());
        }
        if (this.properties.getProxyReceptorUrl() != null) {
            validationFilter.getInitParameters().put("proxyReceptorUrl", this.properties.getProxyReceptorUrl());
        }

        if (this.casClientConfigurer != null) {
            this.casClientConfigurer.configureValidationFilter(validationFilter);
        }
        return validationFilter;
    }


    @Bean
    public FilterRegistrationBean casHttpServletRequestWrapperFilter() {
        final FilterRegistrationBean reqWrapperFilter = new FilterRegistrationBean();
        reqWrapperFilter.setFilter(new HttpServletRequestWrapperFilter());
        if (this.properties.getRequestWrapperUrlPatterns().size() > 0) {
            reqWrapperFilter.setUrlPatterns(this.properties.getRequestWrapperUrlPatterns());
        }
        if (this.casClientConfigurer != null) {
            this.casClientConfigurer.configureHttpServletRequestWrapperFilter(reqWrapperFilter);
        }
        return reqWrapperFilter;
    }

    @Bean
    public FilterRegistrationBean casAssertionThreadLocalFilter() {
        final FilterRegistrationBean assertionTLFilter = new FilterRegistrationBean();
        assertionTLFilter.setFilter(new AssertionThreadLocalFilter());
        if (this.properties.getAssertionThreadLocalUrlPatterns().size() > 0) {
            assertionTLFilter.setUrlPatterns(this.properties.getAssertionThreadLocalUrlPatterns());
        }
        if (this.casClientConfigurer != null) {
            this.casClientConfigurer.configureAssertionThreadLocalFilter(assertionTLFilter);
        }
        return assertionTLFilter;
    }


    @Autowired(required = false)
    void setConfigurers(Collection<CasClientConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException(configurers.size() + " implementations of " +
                    "CasClientConfigurer were found when only 1 was expected. " +
                    "Refactor the configuration such that CasClientConfigurer is " +
                    "implemented only once or not at all.");
        }
        this.casClientConfigurer = configurers.iterator().next();
    }

    private Map<String, String> constructInitParams(final String casUrlParamName, final String casUrlParamVal, final String clientHostUrlVal) {
        final Map<String, String> initParams = new HashMap<>(2);
        initParams.put(casUrlParamName, casUrlParamVal);
        initParams.put("serverName", clientHostUrlVal);
        return initParams;
    }

    private void initFilter(final FilterRegistrationBean filterRegistrationBean, final Filter targetFilter, final Map<String, String> initParams, List<String> urlPatterns) {
        filterRegistrationBean.setFilter(targetFilter);
        filterRegistrationBean.setInitParameters(initParams);
        if (urlPatterns.size() > 0) {
            filterRegistrationBean.setUrlPatterns(urlPatterns);
        }
    }
}
