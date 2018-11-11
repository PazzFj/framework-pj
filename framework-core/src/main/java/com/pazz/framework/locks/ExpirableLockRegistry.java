package com.pazz.framework.locks;

/**
 * @author: Peng Jian
 * @create: 2018/11/11 17:24
 * @description:
 */
public interface ExpirableLockRegistry extends LockRegistry {
    /**
     * 本地锁超时时间
     * 默认1小时
     */
    long DEFAULT_EXPIRE_UNUSED_OLDER_THEN_TIME = 1000 * 60 * 60;

    /**
     * 获取默认本地锁超时失效时间
     * @return
     */
    default long getDefaultExpireUnusedOlderThanTime(){
        return DEFAULT_EXPIRE_UNUSED_OLDER_THEN_TIME;
    }

    /**
     * Remove locks last acquired more than 'age' ago that are not currently locked.
     * @param age the time since the lock was last obtained.
     * @throws IllegalStateException if the registry configuration does not support this feature.
     */
    void expireUnusedOlderThan(long age);
}
