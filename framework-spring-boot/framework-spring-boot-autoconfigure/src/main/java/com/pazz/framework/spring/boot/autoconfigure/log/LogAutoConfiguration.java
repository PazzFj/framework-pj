package com.pazz.framework.spring.boot.autoconfigure.log;

import com.pazz.framework.log.ILogBuffer;
import com.pazz.framework.log.ILogSender;
import com.pazz.framework.log.LogBuffer;
import com.pazz.framework.spring.boot.autoconfigure.web.FrameworkFilterAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    public LogAutoConfiguration(ObjectProvider<ILogSender> logSender){
        this.logSender = logSender.getIfAvailable();
    }

    /**
     * 日志缓存
     */
    @Bean
    @ConditionalOnMissingBean
    public ILogBuffer logBuffer(){
        LogBuffer logBuffer = new LogBuffer();
        logBuffer.setEnable(properties.getBuffer().isEnable());
        logBuffer.setInterval(properties.getBuffer().getInterval());
        logBuffer.setListSize(properties.getBuffer().getListSize());
        logBuffer.setQueueSize(properties.getBuffer().getQueueSize());
        logBuffer.setLogSender(this.logSender);
        return logBuffer;
    }
}
