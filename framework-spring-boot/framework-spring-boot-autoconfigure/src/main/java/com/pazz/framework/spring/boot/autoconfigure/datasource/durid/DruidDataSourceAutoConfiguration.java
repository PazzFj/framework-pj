package com.pazz.framework.spring.boot.autoconfigure.datasource.durid;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterManager;
import com.alibaba.druid.filter.logging.LogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 彭坚
 * @create: 2018/11/15 9:59
 * @description: druid 数据源
 */
@Configuration
@ConditionalOnClass(com.alibaba.druid.pool.DruidDataSource.class)
@EnableConfigurationProperties(DruidProperties.class)
@Import({DruidSpringAopConfiguration.class, DruidStatViewServletConfiguration.class, DruidStatFilterConfiguration.class})
public class DruidDataSourceAutoConfiguration {

    private final static Log log = LogFactory.getLog(DruidDataSourceAutoConfiguration.class);

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource(DruidProperties properties) throws SQLException {
        //base datasource config,use spring datasource autoconfig.
        DruidDataSource datasource = (DruidDataSource) DataSourceBuilder.create().type(DruidDataSource.class).build();
        //druid config.
        configDruid(datasource, properties);
        return datasource;
    }

    /**
     * 设置datasource属性
     */
    private void configDruid(DruidDataSource datasource, DruidProperties properties) throws SQLException {
        if (properties.getMaxActive() != null) {
            datasource.setMaxActive(properties.getMaxActive());
        }
        if (properties.getInitialSize() != null) {
            datasource.setInitialSize(properties.getInitialSize());
        }
        if (properties.getMaxWait() != null) {
            datasource.setMaxWait(properties.getMaxWait());
        }
        if (properties.getMinIdle() != null) {
            datasource.setMinIdle(properties.getMinIdle());
        }
        if (properties.getTimeBetweenEvictionRunsMillis() != null) {
            datasource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        }
        if (properties.getMinEvictableIdleTimeMillis() != null) {
            datasource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        }
        if (properties.getTestWhileIdle() != null) {
            datasource.setTestWhileIdle(properties.getTestWhileIdle());
        }
        if (properties.getTestOnBorrow() != null) {
            datasource.setTestOnBorrow(properties.getTestOnBorrow());
        }
        if (properties.getTestOnReturn() != null) {
            datasource.setTestOnReturn(properties.getTestOnReturn());
        }
        if (properties.getPoolPreparedStatements() != null) {
            datasource.setPoolPreparedStatements(properties.getPoolPreparedStatements());
        }
        if (properties.getMaxPoolPreparedStatementPerConnectionSize() != null) {
            datasource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());
        }
        if (properties.getConnectionProperties() != null) {
            datasource.setConnectionProperties(properties.getConnectionProperties());
        }
        //配置日志Filter
        //configLogFilter(datasource,properties);
        try {
            datasource.setFilters(properties.getFilters() != null ? properties.getFilters() : "stat");
        } catch (SQLException e) {
            throw new IllegalArgumentException("please check your spring.datasource.druid.filters property.", e);
        }
    }

    private void configLogFilter(DruidDataSource datasource, DruidProperties properties) throws SQLException {
        String filterClassName = FilterManager.getFilter(properties.getLogFilterType());
        Class<?> filterClass = Utils.loadClass(filterClassName);
        if (filterClass == null) {
            log.error("load filter error, filter not found : " + filterClassName);
            return;
        }
        LogFilter filter;
        try {
            filter = (LogFilter) filterClass.newInstance();
        } catch (InstantiationException e) {
            throw new SQLException("load managed jdbc driver event listener error. " + properties.getLogFilterType(), e);
        } catch (IllegalAccessException e) {
            throw new SQLException("load managed jdbc driver event listener error. " + properties.getLogFilterType(), e);
        }
        filter.setConnectionCloseAfterLogEnabled(properties.getLogFilter().isConnectionCloseAfterLogEnable());
        filter.setConnectionCommitAfterLogEnabled(properties.getLogFilter().isConnectionCommitAfterLogEnable());
        filter.setConnectionConnectAfterLogEnabled(properties.getLogFilter().isConnectionConnectAfterLogEnable());
        filter.setConnectionConnectBeforeLogEnabled(properties.getLogFilter().isConnectionConnectBeforeLogEnable());
        filter.setConnectionLogEnabled(properties.getLogFilter().isConnectionLogEnabled());
        filter.setConnectionLogErrorEnabled(properties.getLogFilter().isConnectionLogErrorEnabled());
        filter.setConnectionRollbackAfterLogEnabled(properties.getLogFilter().isConnectionRollbackAfterLogEnable());

        filter.setDataSourceLogEnabled(properties.getLogFilter().isDataSourceLogEnabled());

        filter.setResultSetCloseAfterLogEnabled(properties.getLogFilter().isResultSetCloseAfterLogEnable());
        filter.setResultSetLogEnabled(properties.getLogFilter().isResultSetLogEnabled());
        filter.setResultSetLogErrorEnabled(properties.getLogFilter().isResultSetLogErrorEnabled());
        filter.setResultSetNextAfterLogEnabled(properties.getLogFilter().isResultSetNextAfterLogEnable());
        filter.setResultSetOpenAfterLogEnabled(properties.getLogFilter().isResultSetOpenAfterLogEnable());

        filter.setStatementCloseAfterLogEnabled(properties.getLogFilter().isStatementCloseAfterLogEnable());
        filter.setStatementCreateAfterLogEnabled(properties.getLogFilter().isStatementCreateAfterLogEnable());
        filter.setStatementExecutableSqlLogEnable(properties.getLogFilter().isStatementExecutableSqlLogEnable());
        filter.setStatementExecuteAfterLogEnabled(properties.getLogFilter().isStatementExecuteAfterLogEnable());
        filter.setStatementExecuteBatchAfterLogEnabled(properties.getLogFilter().isStatementExecuteBatchAfterLogEnable());
        filter.setStatementExecuteQueryAfterLogEnabled(properties.getLogFilter().isStatementExecuteQueryAfterLogEnable());
        filter.setStatementExecuteUpdateAfterLogEnabled(properties.getLogFilter().isStatementExecuteUpdateAfterLogEnable());
        filter.setStatementLogEnabled(properties.getLogFilter().isStatementLogEnabled());
        filter.setStatementLogErrorEnabled(properties.getLogFilter().isStatementLogErrorEnabled());
        filter.setStatementParameterClearLogEnable(properties.getLogFilter().isStatementParameterClearLogEnable());
        filter.setStatementPrepareAfterLogEnabled(properties.getLogFilter().isStatementPrepareAfterLogEnable());
        filter.setStatementParameterSetLogEnabled(properties.getLogFilter().isStatementParameterSetLogEnable());
        filter.setStatementPrepareCallAfterLogEnabled(properties.getLogFilter().isStatementPrepareCallAfterLogEnable());
        filter.setStatementSqlPrettyFormat(properties.getLogFilter().isStatementLogSqlPrettyFormat());
        List<Filter> filters = new ArrayList<>();
        filters.add(filter);
        datasource.setProxyFilters(filters);
    }

}
