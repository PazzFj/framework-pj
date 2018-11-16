package com.pazz.framework.samples.dao;

import com.pazz.framework.mybatis.BaseDao;
import com.pazz.framework.samples.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author: 彭坚
 * @create: 2018/11/16 13:58
 * @description:
 */
@Repository
public interface UserDao extends BaseDao<UserEntity> {

    Long queryCountByUserName(@Param("id") String id);

}
