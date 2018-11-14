package com.pazz.framework.locks;

import java.util.concurrent.locks.Lock;

/**
 * @author: 彭坚
 * @create: 2018/11/11 17:22
 * @description: 维护共享锁注册表的策略
 * Strategy for maintaining a registry of shared locks
 * @since 2.1.1
 */
@FunctionalInterface
public interface LockRegistry {

    /**
     * 获取与参数对象关联的锁。
     */
    Lock obtain(Object lockKey);

}
