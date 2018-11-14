package com.pazz.framework.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author: Peng Jian
 * @create: 2018/11/14 16:52
 * @description: 读写分离数据源
 */
public class ReadWriteSeparationDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceType();
    }
}
