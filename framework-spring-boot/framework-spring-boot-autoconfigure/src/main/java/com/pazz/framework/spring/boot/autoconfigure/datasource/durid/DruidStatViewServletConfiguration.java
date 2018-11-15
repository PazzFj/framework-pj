package com.pazz.framework.spring.boot.autoconfigure.datasource.durid;

import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * @author: 彭坚
 * @create: 2018/11/15 10:02
 * @description: The Druid stat view servlet configuration.
 */
@ConditionalOnProperty(name = "spring.datasource.druid.StatViewServlet.enabled", havingValue = "true", matchIfMissing = true)
public class DruidStatViewServletConfiguration {

    @Bean
    public ServletRegistrationBean servletRegistrationBean(DruidProperties properties) {
        DruidProperties.StatViewServlet config = properties.getStatViewServlet();
        ServletRegistrationBean registration = new ServletRegistrationBean();
        registration.setServlet(new StatViewServlet());
        registration.addUrlMappings(config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*");
        if (config.getAllow() != null) {
            registration.addInitParameter("allow", config.getAllow());
        }
        if (config.getDeny() != null) {
            registration.addInitParameter("deny", config.getDeny());
        }
        if (config.getLoginUsername() != null) {
            registration.addInitParameter("loginUsername", config.getLoginUsername());
        }
        if (config.getLoginPassword() != null) {
            registration.addInitParameter("loginPassword", config.getLoginPassword());
        }
        if (config.getResetEnable() != null) {
            registration.addInitParameter("resetEnable", config.getResetEnable());
        }
        return registration;
    }

}
