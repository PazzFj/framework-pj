package com.pazz.framework.spring.boot.autoconfigure;

import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: 彭坚
 * @create: 2018/11/16 15:52
 * @description: 平台抽象EnvironmentPostProcessor
 */
public abstract class AbstractEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * 获取默认配置
     *
     * @see ConfigFileApplicationListener
     */
    protected Map getDefaultPropertyMap(ConfigurableEnvironment environment) {
        MutablePropertySources sources = environment.getPropertySources();
        Map<String, Object> map = null;
        if (sources.contains("defaultProperties")) {
            PropertySource<?> source = sources.get("defaultProperties");
            if (source instanceof MapPropertySource) {
                map = ((MapPropertySource) source).getSource();
            }
        } else {
            map = new LinkedHashMap<>();
            sources.addLast(new MapPropertySource("defaultProperties", map));
        }
        return map;
    }
}
