package com.pazz.framework.spring.boot.autoconfigure.datasource;

import com.pazz.framework.util.string.StringUtil;
import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;

/**
 * @author: 彭坚
 * @create: 2018/11/11 16:29
 * @description: 框架读写分离主从数据源配置文件
 */
@Data
@ConfigurationProperties(prefix = "framework.datasource")
public class DataSourceConfigProperties {
    /**
     * 主数据源
     */
    private DataSourceConfig master;
    /**
     * 从数据源
     */
    private DataSourceConfig slave;

    @Data
    public static class DataSourceConfig {
        /**
         * Fully qualified name of the JDBC driver. Auto-detected based on the URL by default.
         */
        private String driverClassName;

        /**
         * JDBC url of the database.
         */
        private String url;

        /**
         * Login user of the database.
         */
        private String username;

        /**
         * Login password of the database.
         */
        private String password;

        /**
         * JNDI location of the datasource. Class, url, username & password are ignored when
         * set.
         */
        private String jndiName;

        public DataSource createDataSource() {
            if (StringUtil.isNotBlank(jndiName)) {
                /**
                 * JNDI获取
                 */
                JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
                DataSource dataSource = dataSourceLookup.getDataSource(jndiName);
                return dataSource;
            }
            return DataSourceBuilder.create().driverClassName(driverClassName).url(url).username(username).password(password).build();
        }

    }

}
