package com.pazz.framework.locks.jdbc;

import java.io.Closeable;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 9:12
 * @description: 锁仓库
 * 封装锁所需的SQL分流。{@link JdbcLockRegistry}需要对spring托管(事务性)客户机服务的引用，因此必须将该组件声明为bean。
 * @since 4.3
 */
public interface LockRepository extends Closeable {

    /**
     * 是否获得
     */
    boolean isAcquired(String lock);

    /**
     * 删除
     */
    void delete(String lock);

    /**
     * 获得
     *
     * @param lock
     * @return
     */
    boolean acquire(String lock);

    /**
     * 关闭
     */
    @Override
    void close();
}
