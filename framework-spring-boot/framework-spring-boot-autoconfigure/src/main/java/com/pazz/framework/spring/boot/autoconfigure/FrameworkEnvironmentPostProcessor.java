package com.pazz.framework.spring.boot.autoconfigure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author: 彭坚
 * @create: 2018/11/16 15:53
 * @description: 平台默认配置
 */
public class FrameworkEnvironmentPostProcessor extends AbstractEnvironmentPostProcessor implements Ordered {

    public static final String location = "classpath*:application-common.properties";

    public static final String APPLICATION_COMMON_CONFIGURATION_PROPERTY_SOURCE_NAME ="applicationCommonConfigurationProperties";

    private int order = ConfigFileApplicationListener.DEFAULT_ORDER + 1;

    private static final Log logger = LogFactory.getLog(FrameworkEnvironmentPostProcessor.class);
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //tomcat默认配置
        addTomcatDefaultProperty(environment);
        //日志默认配置文件
        addLogDefaultProperty(environment);
        //Endpoints 默认配置
        addEndpointsDefaultProperty(environment);
        //http默认配置
        addHttpDefaultProperty(environment);
        //jackson默认配置
        addJacksonDefaultProperty(environment);
        //健康检测默认配置
        addHealthDefaultProperty(environment);
        //druid默认配置
        addDruidDefaultProperty(environment);
        //mybatis默认配置
        addMybatisDefaultProperty(environment);
        //Feign默认配置
        addFeignDefaultProperty(environment);
        //session 默认配置
        addSessionDefaultProperty(environment);
        //新增公共配置
        addCommonProperty(environment,application);
    }

    private void addSessionDefaultProperty(ConfigurableEnvironment environment) {
        Map map = getDefaultPropertyMap(environment);
        if (map != null) {
            //默认redis存储session
            map.put("spring.session.store-type", "redis");
        }
    }


    private void addLogDefaultProperty(ConfigurableEnvironment environment){
        Map map = getDefaultPropertyMap(environment);
        if (map != null) {
            //log4jdbc日志配置
            map.put("logging.level.jdbc.sqltiming", "info");
            map.put("logging.level.jdbc.connection", "error");
            map.put("logging.level.jdbc.resultset", "error");
            map.put("logging.level.jdbc.audit", "error");
            map.put("logging.level.jdbc.sqlonly", "error");
            map.put("logging.level.jdbc.resultsettable", "error");
        }
    }

    private void addEndpointsDefaultProperty(ConfigurableEnvironment environment){
        Map map = getDefaultPropertyMap(environment);
        if (map != null) {
            map.put("endpoints.shutdown.enabled", "true");
            map.put("endpoints.shutdown.sensitive", "false");
        }
    }

    private void addFeignDefaultProperty(ConfigurableEnvironment environment){
        Map map = getDefaultPropertyMap(environment);
        if (map != null) {
            map.put("feign.compression.response.enabled", "true");
            map.put("feign.compression.request.enabled", "true");
        }
    }

    private void addCommonProperty(ConfigurableEnvironment environment, SpringApplication application){
        try{
            environment.getPropertySources().addAfter(ConfigFileApplicationListener.APPLICATION_CONFIGURATION_PROPERTY_SOURCE_NAME,load(location,application.getResourceLoader()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private ConfigurationPropertySources load(String location, ResourceLoader resourceLoader) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(location);
        StringBuilder msg = new StringBuilder();
        String name = "applicationConfig: [" + location + "]";
        msg.append(name);
        PropertySourcesLoader propertiesLoader = new PropertySourcesLoader();
        if (resources != null && resources.length > 0) {
            for(Resource resource : resources){
                if (resource == null || !resource.exists()) {
                    msg.append(" resource not found");
                }
                propertiesLoader.load(resource);
                msg.append("config file ");
                msg.append(getResourceDescription(location, resource));
            }

        }else {
            msg.append("Skipped resource not found");
        }
        this.logger.debug(msg);
        List<PropertySource<?>> reorderedSources = new ArrayList<PropertySource<?>>();
        MutablePropertySources sources = propertiesLoader.getPropertySources();
        for (PropertySource<?> item : sources) {
            reorderedSources.add(item);
        }
        return new ConfigurationPropertySources(reorderedSources);
    }

    private String getResourceDescription(String location, Resource resource) {
        String resourceDescription = "'" + location + "'";
        if (resource != null) {
            try {
                resourceDescription = String.format("'%s' (%s)",
                        resource.getURI().toASCIIString(), location);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return resourceDescription;
    }

    private void addMybatisDefaultProperty(ConfigurableEnvironment environment) {
        Map map = getDefaultPropertyMap(environment);
        if (map != null) {
            map.put("mapper.not-empty", "false");
            map.put("offsetAsPageNum", "true");
            map.put("pagehelper.reasonable", "true");
            map.put("pagehelper.supportMethodsArguments", "true");
            map.put("pagehelper.helperDialect", "mysql");
        }
    }

    private void addDruidDefaultProperty(ConfigurableEnvironment environment) {
        Map map = getDefaultPropertyMap(environment);
        if (map != null) {
            map.put("logging.level.druid.sql.Statement", "debug");
            map.put("spring.datasource.druid.filters", "stat,wall");
            map.put("spring.datasource.druid.log-filter.statement-prepare-after-log-enable", "false");
        }
    }

    private void addHealthDefaultProperty(ConfigurableEnvironment environment) {
        Map map = getDefaultPropertyMap(environment);
        if (map != null) {
            map.put("management.health.mongo.enabled", "false");
            map.put("management.health.db.enabled", "true");
        }
    }

    private void addHttpDefaultProperty(ConfigurableEnvironment environment) {
        Map map = getDefaultPropertyMap(environment);
        if (map != null) {
            //默认强制使用编码
            map.put("spring.http.encoding.force", "true");
        }
    }

    private void addTomcatDefaultProperty(ConfigurableEnvironment environment) {
        Map map = getDefaultPropertyMap(environment);
        if (map != null) {
            //uri默认使用utf-8编码
            map.put("server.tomcat.uri-encoding", "utf-8");
        }
    }

    private void addJacksonDefaultProperty(ConfigurableEnvironment environment){
        Map map = getDefaultPropertyMap(environment);
        if (map != null) {
            //spring.jackson.deserialization.fail-on-unknown-properties=false
            map.put("spring.jackson.deserialization.fail-on-unknown-properties", "false");
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

    static class ConfigurationPropertySources
            extends EnumerablePropertySource<Collection<PropertySource<?>>> {

        private final Collection<PropertySource<?>> sources;

        private final String[] names;

        ConfigurationPropertySources(Collection<PropertySource<?>> sources) {
            super(APPLICATION_COMMON_CONFIGURATION_PROPERTY_SOURCE_NAME, sources);
            this.sources = sources;
            List<String> names = new ArrayList<String>();
            for (PropertySource<?> source : sources) {
                if (source instanceof EnumerablePropertySource) {
                    names.addAll(Arrays.asList(
                            ((EnumerablePropertySource<?>) source).getPropertyNames()));
                }
            }
            this.names = names.toArray(new String[names.size()]);
        }

        @Override
        public Object getProperty(String name) {
            for (PropertySource<?> propertySource : this.sources) {
                Object value = propertySource.getProperty(name);
                if (value != null) {
                    return value;
                }
            }
            return null;
        }

        @Override
        public String[] getPropertyNames() {
            return this.names;
        }

    }
}
