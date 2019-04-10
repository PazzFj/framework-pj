package com.pazz.framework.locks;

/**
 * @author: 彭坚
 * @create: 2018/11/11 17:24
 * @description: 此接口的实现支持删除当前未锁定的旧锁。
 * A {@link LockRegistry} implementing this interface supports the removal of aged locks
 * that are not currently locked.
 */
public interface ExpireableLockRegistry extends LockRegistry {
    /**
     * 本地锁超时时间
     * 默认1小时
     */
    long DEFAULT_EXPIRE_UNUSED_OLDER_THEN_TIME = 1000 * 60 * 60;

    /**
     * 获取默认本地锁超时失效时间
     *
     * @return
     */
    default long getDefaultExpireUnusedOlderThanTime() {
        return DEFAULT_EXPIRE_UNUSED_OLDER_THEN_TIME;
    }

    /**
     * 移除上次获得的超过“年龄”的锁，这些锁目前还没有被锁定
     * Remove locks last acquired more than 'age' ago that are not currently locked.
     *
     * @param age the time since the lock was last obtained.
     * @throws IllegalStateException if the registry configuration does not support this feature.
     */
    void expireUnusedOlderThan(long age);
}
