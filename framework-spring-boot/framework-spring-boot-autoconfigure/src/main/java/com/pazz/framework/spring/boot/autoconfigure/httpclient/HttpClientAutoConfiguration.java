package com.pazz.framework.spring.boot.autoconfigure.httpclient;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.httpclient.HttpClientMetricNameStrategies;
import com.codahale.metrics.httpclient.HttpClientMetricNameStrategy;
import com.codahale.metrics.httpclient.InstrumentedHttpClientConnectionManager;
import com.codahale.metrics.httpclient.InstrumentedHttpRequestExecutor;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.MetricsDropwizardAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author: 彭坚
 * @create: 2018/11/15 16:22
 * @description: httpClient自动装配
 */
@Configuration
@ConditionalOnClass(HttpClient.class)
@AutoConfigureAfter(MetricsDropwizardAutoConfiguration.class)
@EnableConfigurationProperties(HttpClientProperties.class)
@Import(MetricsDropwizardAutoConfiguration.class)
public class HttpClientAutoConfiguration {

    @Autowired
    private HttpClientProperties properties;
    @Autowired
    private MetricRegistry metric;

    @Bean
    @ConditionalOnMissingBean
    public RequestConfig httpClientRequestConfig() {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        //从请求池获取请求时间
        requestConfigBuilder.setConnectionRequestTimeout(properties.getRequestConfig().getConnectionRequestTimeout());
        //建立连接超时时间(毫秒)
        requestConfigBuilder.setConnectTimeout(properties.getRequestConfig().getConnectTimeout());
        //等待http响应的超时时间(毫秒)
        requestConfigBuilder.setSocketTimeout(properties.getRequestConfig().getSocketTimeout());
        if (properties.getRequestConfig().getProxyHost() != null) {
            requestConfigBuilder.setProxy(new HttpHost(properties.getRequestConfig().getProxyHost(), properties.getRequestConfig().getProxyPort()));
        }
        return requestConfigBuilder.build();
    }

    /**
     * org.apache.http.client.HttpClient
     */
    @Bean
    @ConditionalOnMissingBean
    public HttpClient httpClient(RequestConfig requestConfig) {
        HttpClientMetricNameStrategy nameStrategy = HttpClientMetricNameStrategies.HOST_AND_METHOD;
        if (properties.getMetricNameStrategy() != null) {
            if (properties.getMetricNameStrategy().equalsIgnoreCase("QUERYLESS_URL_AND_METHOD")) {
                nameStrategy = HttpClientMetricNameStrategies.QUERYLESS_URL_AND_METHOD;
            } else if (properties.getMetricNameStrategy().equalsIgnoreCase("METHOD_ONLY")) {
                nameStrategy = HttpClientMetricNameStrategies.METHOD_ONLY;
            }
        }
        InstrumentedHttpClientConnectionManager instrumentedHttpClientConnectionManager = new InstrumentedHttpClientConnectionManager(metric);
        instrumentedHttpClientConnectionManager.setMaxTotal(properties.getConnectionManager().getMaxConnTotal());
        instrumentedHttpClientConnectionManager.setDefaultMaxPerRoute(properties.getConnectionManager().getMaxConnPerRoute());
        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                .setRequestExecutor(new InstrumentedHttpRequestExecutor(metric, nameStrategy))
                .setConnectionManager(instrumentedHttpClientConnectionManager);
        clientBuilder.setDefaultRequestConfig(requestConfig);
        clientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
        return clientBuilder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClientEndpoint httpClientEndpoint(HttpClientProperties properties, MetricRegistry metrics) {
        return new HttpClientEndpoint(properties, metrics);
    }
}
