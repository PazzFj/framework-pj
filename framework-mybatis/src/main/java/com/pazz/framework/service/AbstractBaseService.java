package com.pazz.framework.service;

import com.pazz.framework.mybatis.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: 彭坚
 * @create: 2018/11/16 10:56
 * @description: 公共基础抽象基类
 */
public abstract class AbstractBaseService<D extends BaseDao<T>, T> implements IBaseService<T> {

    @Autowired
    protected D dao;

    @Transactional
    public int save(T record) {
        return dao.insert(record);
    }

    @Transactional
    public int saveSelective(T record) {
        return dao.insertSelective(record);
    }

    @Transactional
    public int deleteByPrimaryKey(Object key) {
        return dao.deleteByPrimaryKey(key);
    }

    @Transactional
    public int delete(T record) {
        return dao.delete(record);
    }

    @Transactional
    public int deleteByExample(Object example) {
        return dao.deleteByExample(example);
    }

    @Transactional
    public int updateByPrimaryKey(T record) {
        return dao.updateByPrimaryKey(record);
    }

    @Transactional
    public int updateByPrimaryKeySelective(T record) {
        return dao.updateByPrimaryKeySelective(record);
    }

    @Transactional
    public int updateByExample(@Param("record") T record, @Param("example") Object example) {
        return dao.updateByExample(record, example);
    }

    @Transactional
    public int updateByExampleSelective(@Param("record") T record, @Param("example") Object example) {
        return dao.updateByExampleSelective(record, example);
    }

    public T selectOne(T record) {
        return dao.selectOne(record);
    }

    public List<T> select(T record) {
        return dao.select(record);
    }

    public List<T> selectAll() {
        return dao.selectAll();
    }

    public int selectCount(T record) {
        return dao.selectCount(record);
    }

    public T selectByPrimaryKey(Object key) {
        return dao.selectByPrimaryKey(key);
    }

    public List<T> selectByExample(Object example) {
        return dao.selectByExample(example);
    }

    public int selectCountByExample(Object example) {
        return dao.selectCountByExample(example);
    }

    public List<T> selectByRowBounds(T record, RowBounds rowBounds) {
        return dao.selectByRowBounds(record, rowBounds);
    }

    public List<T> selectByExampleAndRowBounds(Object example, RowBounds rowBounds) {
        return dao.selectByExampleAndRowBounds(example, rowBounds);
    }

}
