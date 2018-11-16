package com.pazz.framework.service.update;

import org.apache.ibatis.annotations.Param;

/**
 * @author: 彭坚
 * @create: 2018/11/16 10:46
 * @description: 更新 service
 */
public interface IUpdateService<T> {
    /**
     * 根据主键更新实体全部字段，null值会被更新
     */
    int updateByPrimaryKey(T record);

    /**
     * 根据主键更新属性不为null的值
     */
    int updateByPrimaryKeySelective(T record);

    /**
     * 根据Example条件更新实体`record`包含的全部属性，null值会被更新
     */
    int updateByExample(@Param("record") T record, @Param("example") Object example);

    /**
     * 根据Example条件更新实体`record`包含的不是null的属性值
     */
    int updateByExampleSelective(@Param("record") T record, @Param("example") Object example);
}
