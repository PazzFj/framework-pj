package com.pazz.framework.spring.boot.autoconfigure.log;

import com.pazz.framework.log.ILogBuffer;
import com.pazz.framework.log.ILogExceptionWriter;
import com.pazz.framework.log.ILogSender;
import com.pazz.framework.log.ILogWriter;
import com.pazz.framework.log.LogBuffer;
import com.pazz.framework.log.LogExceptionWriter;
import com.pazz.framework.log.LogWriter;
import com.pazz.framework.log.filter.LogFilter;
import com.pazz.framework.spring.boot.autoconfigure.web.FrameworkFilterAutoConfiguration;
import com.pazz.framework.util.string.StringUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 10:17
 * @description: 日志自动装载
 */
@Configuration
@EnableConfigurationProperties(LogProperties.class)
@AutoConfigureAfter({MongoLogAutoConfiguration.class, FrameworkFilterAutoConfiguration.class})
@Import({MongoLogAutoConfiguration.class})
public class LogAutoConfiguration {

    @Autowired
    private LogProperties properties;

    private ILogSender logSender;

    public LogAutoConfiguration(ObjectProvider<ILogSender> logSender) {
        this.logSender = logSender.getIfAvailable();
    }

    /**
     * 日志缓存
     */
    @Bean
    @ConditionalOnMissingBean
    public ILogBuffer logBuffer() {
        LogBuffer logBuffer = new LogBuffer();
        logBuffer.setEnable(properties.getBuffer().isEnable());
        logBuffer.setInterval(properties.getBuffer().getInterval());
        logBuffer.setListSize(properties.getBuffer().getListSize());
        logBuffer.setQueueSize(properties.getBuffer().getQueueSize());
        logBuffer.setLogSender(this.logSender);
        return logBuffer;
    }

    /**
     * 日志记录
     */
    @Bean
    @ConditionalOnMissingBean
    public ILogWriter logWriter(ILogBuffer logBuffer) {
        LogWriter logWriter = new LogWriter();
        logWriter.setLogBuffer(logBuffer);
        return logWriter;
    }

    /**
     * 记录异常日志
     */
    @Bean
    @ConditionalOnMissingBean
    public ILogExceptionWriter logExceptionWriter() {
        LogExceptionWriter logExceptionWriter = new LogExceptionWriter();
        logExceptionWriter.setLogSender(this.logSender);
        logExceptionWriter.setWriteBusinessException(properties.getException().isWriteBusinessException());
        logExceptionWriter.setWriteException(properties.getException().isWriteException());
        return logExceptionWriter;
    }

    /**
     * 日志异常拦截器
     */
    @Bean
    @ConditionalOnMissingBean(LogFilter.class)
    @ConditionalOnProperty(prefix = "framework.log.filter", name = "enabled", matchIfMissing = true)
    public LogFilter logFilter(ILogWriter logWriter) {
        LogFilter logFilter = new LogFilter();
        logFilter.setLogPrint(properties.getFilter().isLogPrint());
        logFilter.setLogRequest(properties.getFilter().isLogRequest());
        logFilter.setLogResponse(properties.getFilter().isLogResponse());
        logFilter.setIncludeHeaders(properties.getFilter().isIncludeHeaders());
        logFilter.setIncludePayload(properties.getFilter().isIncludePayload());
        logFilter.setMaxPayloadLength(properties.getFilter().getMaxPayloadLength());
        if (StringUtil.isNotBlank(properties.getFilter().getExcludeUrlPatterns())) {
            logFilter.addExcludeUrlPattern(StringUtil.split(properties.getFilter().getExcludeUrlPatterns(), ","));
        }
        logFilter.setLogWriter(logWriter);
        return logFilter;
    }
}
