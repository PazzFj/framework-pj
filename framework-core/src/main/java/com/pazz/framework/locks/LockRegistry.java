package com.pazz.framework.locks;

import java.util.concurrent.locks.Lock;

/**
 * @author Oleg Zhurakousky
 * @author Gary Russell
 * @author: Peng Jian
 * @create: 2018/11/11 17:22
 * @description: Strategy for maintaining a registry of shared locks
 * @since 2.1.1
 */
@FunctionalInterface
public interface LockRegistry {

    /**
     * Obtains the lock associated with the parameter object.
     *
     * @param lockKey The object with which the lock is associated.
     * @return The associated lock.
     */
    Lock obtain(Object lockKey);

}
