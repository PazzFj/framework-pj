package com.pazz.framework.spring.boot.autoconfigure.session;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: Peng Jian
 * @create: 2018/11/14 13:36
 * @description: session 相关配置文件
 */
@ConfigurationProperties(prefix = "spring.session")
public class SessionProperties {

    private Cookie cookie = new Cookie();

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    @Data
    public static class Cookie {
        /**
         * Session cookie name.
         */
        private String name = "JSESSIONID";
        /**
         * Domain for the session cookie.
         */
        private String domain;
        /**
         * Domain pattern
         */
        private String domainPattern;
        /**
         * Path of the session cookie.
         */
        private String path;
        /**
         * Comment for the session cookie.
         */
        private String comment;
        /**
         * "HttpOnly" flag for the session cookie.
         */
        private Boolean httpOnly;
        /**
         * "Secure" flag for the session cookie.
         */
        private Boolean secure;
        /**
         * Maximum age of the session cookie in seconds.
         */
        private Integer maxAge;
    }

}
