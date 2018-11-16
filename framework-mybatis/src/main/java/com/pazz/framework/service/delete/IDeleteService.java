package com.pazz.framework.service.delete;

/**
 * @author: 彭坚
 * @create: 2018/11/16 10:45
 * @description: 删除service
 */
public interface IDeleteService<T> {

    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Object key);

    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号
     */
    int delete(T record);

    /**
     * 根据Example条件删除数据
     */
    int deleteByExample(Object example);

}
