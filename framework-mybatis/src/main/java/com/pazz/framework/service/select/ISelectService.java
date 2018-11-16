package com.pazz.framework.service.select;

import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author: 彭坚
 * @create: 2018/11/16 10:46
 * @description: 查询 service
 */
public interface ISelectService<T> {

    /**
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     */
    T selectOne(T record);

    /**
     * 根据实体中的属性值进行查询，查询条件使用等号
     */
    List<T> select(T record);

    /**
     * 查询全部结果
     */
    List<T> selectAll();

    /**
     * 根据实体中的属性查询总数，查询条件使用等号
     */
    int selectCount(T record);

    /**
     * 根据主键字段进行查询，方法参数必须包含完整的主键属性，查询条件使用等号
     */
    T selectByPrimaryKey(Object key);

    /**
     * 根据Example条件进行查询
     */
    List<T> selectByExample(Object example);

    /**
     * 根据Example条件进行查询总数
     */
    int selectCountByExample(Object example);

    /**
     * 根据实体属性和RowBounds进行分页查询
     */
    List<T> selectByRowBounds(T record, RowBounds rowBounds);

    /**
     * 根据example条件和RowBounds进行分页查询
     */
    List<T> selectByExampleAndRowBounds(Object example, RowBounds rowBounds);

}
