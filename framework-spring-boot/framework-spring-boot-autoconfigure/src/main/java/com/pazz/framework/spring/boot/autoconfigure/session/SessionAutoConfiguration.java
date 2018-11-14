package com.pazz.framework.spring.boot.autoconfigure.session;

import com.pazz.framework.util.string.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author: Peng Jian
 * @create: 2018/11/14 13:43
 * @description: session 自动装配
 */
@Configuration
@ConditionalOnClass(CookieSerializer.class)
@EnableConfigurationProperties(SessionProperties.class)
public class SessionAutoConfiguration {

    @Autowired
    private SessionProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public CookieSerializer defaultCookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        if(StringUtil.isNotBlank(properties.getCookie().getName())){
            cookieSerializer.setCookieName(properties.getCookie().getName());
        }
        if(StringUtil.isNotBlank(properties.getCookie().getPath())){
            cookieSerializer.setCookiePath(properties.getCookie().getPath());
        }
        if(properties.getCookie().getMaxAge() != null) {
            cookieSerializer.setCookieMaxAge(properties.getCookie().getMaxAge());
        }
        if(StringUtil.isNotBlank(properties.getCookie().getDomain())){
            cookieSerializer.setDomainName(properties.getCookie().getDomain());
        }
        if(StringUtil.isNotBlank(properties.getCookie().getDomainPattern())){
            cookieSerializer.setDomainNamePattern(properties.getCookie().getDomainPattern());
        }
        if(properties.getCookie().getSecure() != null) {
            cookieSerializer.setUseSecureCookie(properties.getCookie().getSecure());
        }
        if(properties.getCookie().getHttpOnly() != null) {
            cookieSerializer.setUseHttpOnlyCookie(properties.getCookie().getHttpOnly());
        }
        return cookieSerializer;
    }

}
