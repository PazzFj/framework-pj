package com.pazz.framework.spring.boot.autoconfigure.datasource.durid;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: 彭坚
 * @create: 2018/11/11 16:45
 * @description: druid 数据源
 */
@ConfigurationProperties("spring.datasource.druid")
@Data
public class DruidProperties {
    private Integer initialSize;
    private Integer minIdle;
    private Integer maxActive;
    private Integer maxWait;
    private Integer timeBetweenEvictionRunsMillis;
    private Integer minEvictableIdleTimeMillis;
    private String validationQuery;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;
    private Boolean testWhileIdle;
    private String filters;
    private Boolean poolPreparedStatements;
    private Integer maxPoolPreparedStatementPerConnectionSize;
    private String[] aopPatterns;
    private String connectionProperties;
    private StatViewServlet statViewServlet = new StatViewServlet();
    private StatFilter statFilter = new StatFilter();
    private String logFilterType = "commonLogging";
    private LogFilter logFilter = new LogFilter();

    /**
     */
    @Data
    public static class StatViewServlet {
        private String urlPattern;
        private String allow;
        private String deny;
        private String loginUsername;
        private String loginPassword;
        private String resetEnable;
    }

    /**
     */
    @Data
    public static class StatFilter {
        private String urlPattern;
        private String exclusions;
        private String sessionStatMaxCount;
        private String sessionStatEnable;
        private String principalSessionName;
        private String principalCookieName;
        private String profileEnable;
    }

    @Data
    public static class LogFilter {
        private boolean connectionConnectBeforeLogEnable = true;
        private boolean connectionConnectAfterLogEnable = true;
        private boolean connectionCommitAfterLogEnable = true;
        private boolean connectionRollbackAfterLogEnable = true;
        private boolean connectionCloseAfterLogEnable = true;
        private boolean connectionLogEnabled = true;
        private boolean connectionLogErrorEnabled = true;

        private boolean dataSourceLogEnabled = true;

        private boolean resultSetNextAfterLogEnable = true;
        private boolean resultSetOpenAfterLogEnable = true;
        private boolean resultSetCloseAfterLogEnable = true;
        private boolean resultSetLogEnabled = true;
        private boolean resultSetLogErrorEnabled = true;

        private boolean statementCreateAfterLogEnable = true;
        private boolean statementPrepareAfterLogEnable = true;
        private boolean statementPrepareCallAfterLogEnable = true;
        private boolean statementExecuteAfterLogEnable = true;
        private boolean statementExecuteQueryAfterLogEnable = true;
        private boolean statementExecuteUpdateAfterLogEnable = true;
        private boolean statementExecuteBatchAfterLogEnable = true;
        private boolean statementExecutableSqlLogEnable = false;
        private boolean statementCloseAfterLogEnable = true;
        private boolean statementParameterClearLogEnable = true;
        private boolean statementParameterSetLogEnable = true;
        private boolean statementLogEnabled = true;
        private boolean statementLogErrorEnabled = true;
        private boolean statementLogSqlPrettyFormat = false;
    }
}
