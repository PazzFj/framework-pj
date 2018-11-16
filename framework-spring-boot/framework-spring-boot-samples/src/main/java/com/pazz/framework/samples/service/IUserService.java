package com.pazz.framework.samples.service;

import com.pazz.framework.samples.entity.UserEntity;

import java.util.List;

/**
 * @author: 彭坚
 * @create: 2018/11/16 14:02
 * @description:
 */
public interface IUserService {

    public UserEntity getUser(String id);

    public void saveUser(UserEntity user);

    public Long queryCountByUserName(String id);

    public List<UserEntity> select(UserEntity user);

}
