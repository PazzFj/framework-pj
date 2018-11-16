package com.pazz.framework.service.insert;

/**
 * @author: 彭坚
 * @create: 2018/11/16 10:46
 * @description: 新增service
 */
public interface IInsertService<T> {
    /**
     * 保存记录
     */
    int save(T record);

    /**
     * 保存记录
     * 属性为null的不会保存使用数据库默认值
     */
    int saveSelective(T record);
}
