package com.pazz.framework.samples.cache;

import com.pazz.framework.cache.provider.ITTLCacheProvider;
import com.pazz.framework.samples.dao.UserDao;
import com.pazz.framework.samples.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: 彭坚
 * @create: 2018/11/16 14:34
 * @description: 用户缓存数据提供
 */
public class UserCacheProvider implements ITTLCacheProvider<UserEntity> {

    @Autowired
    private UserDao userDao;

    @Override
    public UserEntity get(String key) {
        return userDao.selectByPrimaryKey(key);
    }
}
