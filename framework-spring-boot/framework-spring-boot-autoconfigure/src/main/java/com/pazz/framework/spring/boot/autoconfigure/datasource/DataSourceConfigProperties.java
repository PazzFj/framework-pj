package com.pazz.framework.spring.boot.autoconfigure.datasource;

import com.pazz.framework.util.string.StringUtil;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;

/**
 * @author: Peng Jian
 * @create: 2018/11/11 16:29
 * @description: 框架读写分离主从数据源配置文件
 */
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

    public static class DataSourceConfig{
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

        public DataSource createDataSource(){
            if(StringUtil.isNotBlank(jndiName)){
                /**
                 * JNDI获取
                 */
                JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
                DataSource dataSource = dataSourceLookup.getDataSource(jndiName);
                return dataSource;
            }
            return DataSourceBuilder.create().driverClassName(driverClassName).url(url).username(username).password(password).build();
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getJndiName() {
            return jndiName;
        }

        public void setJndiName(String jndiName) {
            this.jndiName = jndiName;
        }
    }

    public DataSourceConfig getMaster() {
        return master;
    }

    public void setMaster(DataSourceConfig master) {
        this.master = master;
    }

    public DataSourceConfig getSlave() {
        return slave;
    }

    public void setSlave(DataSourceConfig slave) {
        this.slave = slave;
    }
}
