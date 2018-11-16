package com.pazz.framework.samples.service.impl;

import com.pazz.framework.cache.CacheManager;
import com.pazz.framework.cache.ICache;
import com.pazz.framework.samples.cache.UserCache;
import com.pazz.framework.samples.dao.UserDao;
import com.pazz.framework.samples.entity.UserEntity;
import com.pazz.framework.samples.service.IUserService;
import com.pazz.framework.service.AbstractBaseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: 彭坚
 * @create: 2018/11/16 14:04
 * @description: User Service
 */
@Service
public class UserService extends AbstractBaseService<UserDao, UserEntity> implements IUserService {
    @Override
    public UserEntity getUser(String id) {
        ICache<String, UserEntity> cache = CacheManager.getInstance().getCache(UserCache.UUID);
        return cache.get(id);
    }

    @Override
    public void saveUser(UserEntity user) {
        super.save(user);
    }

    @Override
    public Long queryCountByUserName(String id) {
        return dao.queryCountByUserName(id);
    }

    @Override
    public List<UserEntity> select(UserEntity record) {
        return super.select(record);
    }
}
