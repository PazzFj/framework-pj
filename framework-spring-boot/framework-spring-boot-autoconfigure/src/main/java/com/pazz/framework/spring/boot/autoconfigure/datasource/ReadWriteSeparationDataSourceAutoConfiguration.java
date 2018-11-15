package com.pazz.framework.spring.boot.autoconfigure.datasource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author: 彭坚
 * @create: 2018/11/15 16:02
 * @description: 读写分离数据源自动装载
 */
@EnableConfigurationProperties(DataSourceConfigProperties.class)
public class ReadWriteSeparationDataSourceAutoConfiguration {

    private DataSourceConfigProperties properties;
/*
    @Bean
    public DataSource dataSource() {
        ReadWriteSeparationDataSource dataSource = new ReadWriteSeparationDataSource();
        //使用Log4jdbc数据源
        DataSource masterDataSource = new Log4jdbcProxyDataSource(properties.getMaster().createDataSource());
        DataSource slaveDataSource = new Log4jdbcProxyDataSource(properties.getSlave().createDataSource());
        //默认主数据源
        dataSource.setDefaultTargetDataSource(masterDataSource);
        Map<Object,Object> map = new HashMap(){
            {
                put(DataSourceType.MASTER,masterDataSource);
                put(DataSourceType.SLAVE,slaveDataSource);
            }
        };
        dataSource.setTargetDataSources(map);
        return dataSource;
    }
*/

}
