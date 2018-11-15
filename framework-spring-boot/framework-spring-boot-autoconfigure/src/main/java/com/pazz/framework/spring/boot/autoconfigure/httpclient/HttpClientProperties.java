package com.pazz.framework.spring.boot.autoconfigure.httpclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: 彭坚
 * @create: 2018/11/15 16:16
 * @description: httpClient配置文件
 */
@Data
@ConfigurationProperties(prefix = "framework.httpclient")
public class HttpClientProperties {

    /**
     * metric name strategy: METHOD_ONLY, HOST_AND_METHOD(default), QUERYLESS_URL_AND_METHOD
     */
    private String metricNameStrategy = "HOST_AND_METHOD";

    private RequestConfig requestConfig = new RequestConfig();

    private ConnectionManager connectionManager = new ConnectionManager();

    @Data
    public static class RequestConfig {
        /**
         * 代理服务器主机名或者ip
         */
        private String proxyHost;
        /**
         * 代理服务器端口号
         */
        private Integer proxyPort;
        /**
         * 从请求池获取请求时间
         * 默认1秒
         */
        private int connectionRequestTimeout = 1000;
        /**
         * 建立连接超时时间(毫秒): timeout for client to try to connect to the server
         * 默认2秒
         */
        private int connectTimeout = 2000;
        /**
         * 等待http响应的超时时间(毫秒): After establishing the connection, timeout for  the client socket to wait for response after sending the request
         * 默认10秒
         */
        private int socketTimeout = 10000;
    }

    @Data
    public static class ConnectionManager {
        /**
         * 每个路由连接数
         * 默认50个
         */
        private int maxConnPerRoute = 50;
        /**
         * 总连接数
         * 默认1000个
         */
        private int maxConnTotal = 1000;
    }

}
