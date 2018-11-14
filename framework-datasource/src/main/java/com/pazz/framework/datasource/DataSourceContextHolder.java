package com.pazz.framework.datasource;

/**
 * @author: Peng Jian
 * @create: 2018/11/14 14:50
 * @description: 数据源context上下文
 */
public class DataSourceContextHolder {

    /**
     * dataSourceType thread local
     */
    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();

    /**
     * 设置数据源类型
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new NullPointerException();
        }
        contextHolder.set(dataSourceType);
    }

    /**
     * 获取数据源类型
     */
    public static DataSourceType getDataSourceType() {
        return contextHolder.get();
    }

    /**
     * 清除数据源类型
     */
    public static void clearDataSourceType() {
        contextHolder.remove();
    }
}
