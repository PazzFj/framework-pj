package com.pazz.framework.locks.jdbc;

import com.pazz.framework.locks.ExpirableLockRegistry;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: 彭坚
 * @create: 2018/11/12 9:15Jdbc锁注册
 * @description:
 * 使用共享数据库来协调锁的{@link com.pazz.framework.locks.LockRegistry}。提供与{@link com.pazz.framework.locks.DefaultLockRegistry}相同的语义，
 * 但是所获取的锁是全局的，只要底层数据库在其事务中支持“可序列化”隔离级别。
 * @since 4.3
 */
public class JdbcLockRegistry implements ExpirableLockRegistry {

    private final Map<String, JdbcLock> locks = new ConcurrentHashMap<>();

    private final LockRepository client;

    public JdbcLockRegistry(LockRepository client) {
        this.client = client;
    }

    @Override
    public void expireUnusedOlderThan(long age) {
        Iterator<Map.Entry<String, JdbcLock>> iterator = this.locks.entrySet().iterator();
        long now = System.currentTimeMillis();
        while (iterator.hasNext()) {
            Map.Entry<String, JdbcLock> entry = iterator.next();
            JdbcLock lock = entry.getValue();
            if (now - lock.getLastUsed() > age && !lock.isAcquiredInThisProcess()) {
                iterator.remove();
            }
        }
    }

    @Override
    public Lock obtain(Object lockKey) {
        Assert.isInstanceOf(String.class, lockKey);
        String path = (String) lockKey;
        return this.locks.computeIfAbsent(path, p -> new JdbcLock(this.client, p));
    }

    private static class JdbcLock implements Lock {

        private final LockRepository mutex;

        private final String path;

        private volatile long lastUsed = System.currentTimeMillis();

        private final ReentrantLock delegate = new ReentrantLock();

        JdbcLock(LockRepository client, String path) {
            this.mutex = client;
            this.path = path;
        }
        public long getLastUsed() {
            return this.lastUsed;
        }

        @Override
        public void lock() {
            this.delegate.lock();
            while (true) {
                try {
                    while (!doLock()) {
                        Thread.sleep(100); //NOSONAR
                    }
                    break;
                }
                catch (CannotSerializeTransactionException e) {
                    // try again
                }
                catch (TransactionTimedOutException e) {
                    // try again
                }
                catch (InterruptedException e) {
                    /*
                     * This method must be uninterruptible so catch and ignore
                     * interrupts and only break out of the while loop when
                     * we get the lock.
                     */
                }
                catch (Exception e) {
                    this.delegate.unlock();
                    rethrowAsLockException(e);
                }
            }
        }

        private void rethrowAsLockException(Exception e) {
            throw new CannotAcquireLockException("Failed to lock mutex at " + this.path, e);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            this.delegate.lockInterruptibly();
            while (true) {
                try {
                    while (!doLock()) {
                        Thread.sleep(100); //NOSONAR
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        }
                    }
                    break;
                }
                catch (CannotSerializeTransactionException e) {
                    // try again
                }
                catch (TransactionTimedOutException e) {
                    // try again
                }
                catch (InterruptedException ie) {
                    this.delegate.unlock();
                    Thread.currentThread().interrupt();
                    throw ie;
                }
                catch (Exception e) {
                    this.delegate.unlock();
                    rethrowAsLockException(e);
                }
            }
        }

        @Override
        public boolean tryLock() {
            try {
                return tryLock(0, TimeUnit.MICROSECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            long now = System.currentTimeMillis();
            if (!this.delegate.tryLock(time, unit)) {
                return false;
            }
            long expire = now + TimeUnit.MILLISECONDS.convert(time, unit);
            boolean acquired;
            while (true) {
                try {
                    while (!(acquired = doLock()) && System.currentTimeMillis() < expire) { //NOSONAR
                        Thread.sleep(100); //NOSONAR
                    }
                    if (!acquired) {
                        this.delegate.unlock();
                    }
                    return acquired;
                }
                catch (CannotSerializeTransactionException e) {
                    // try again
                }
                catch (TransactionTimedOutException e) {
                    // try again
                }
                catch (Exception e) {
                    this.delegate.unlock();
                    rethrowAsLockException(e);
                }
            }
        }

        private boolean doLock() {
            boolean acquired = this.mutex.acquire(this.path);
            if (acquired) {
                this.lastUsed = System.currentTimeMillis();
            }
            return acquired;
        }

        @Override
        public void unlock() {
            if (!this.delegate.isHeldByCurrentThread()) {
                throw new IllegalMonitorStateException("You do not own mutex at " + this.path);
            }
            if (this.delegate.getHoldCount() > 1) {
                this.delegate.unlock();
                return;
            }
            try {
                this.mutex.delete(this.path);
            }
            catch (Exception e) {
                throw new DataAccessResourceFailureException("Failed to release mutex at " + this.path, e);
            }
            finally {
                this.delegate.unlock();
            }
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("Conditions are not supported");
        }

        public boolean isAcquiredInThisProcess() {
            return this.mutex.isAcquired(this.path);
        }

    }
}
