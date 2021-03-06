package com.pazz.framework.locks.redis;

import com.pazz.framework.locks.ExpireableLockRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: 彭坚
 * @create: 2018/11/11 17:21
 * @description: Implementation of {@link com.pazz.framework.locks.LockRegistry} providing a distributed lock using Redis.
 * Locks are stored under the key {@code registryKey:lockKey}. Locks expire after
 * (default 60) seconds. Threads unlocking an
 * expired lock will get an {@link IllegalStateException}. This should be
 * considered as a critical error because it is possible the protected
 * resources were compromised.
 * <p>
 * Locks are reentrant.
 *
 * <b>However, locks are scoped by the registry; a lock from a different registry with the
 * same key (even if the registry uses the same 'registryKey') are different
 * locks, and the second cannot be acquired by the same thread while the first is
 * locked.</b>
 *
 * <b>Note: This is not intended for low latency applications.</b> It is intended
 * for resource locking across multiple JVMs.
 * <p>
 * {@link Condition}s are not supported.
 * @since 4.0
 */
public final class RedisLockRegistry implements ExpireableLockRegistry {

    private static final Log logger = LogFactory.getLog(RedisLockRegistry.class);

    private static final long DEFAULT_EXPIRE_AFTER = 60000;

    private static final String OBTAIN_LOCK_SCRIPT =
            "local lockClientId = redis.call('GET', KEYS[1])\n" +
                    "if lockClientId == ARGV[1] then\n" +
                    "  redis.call('PEXPIRE', KEYS[1], ARGV[2])\n" +
                    "  return true\n" +
                    "elseif not lockClientId then\n" +
                    "  redis.call('SET', KEYS[1], ARGV[1], 'PX', ARGV[2])\n" +
                    "  return true\n" +
                    "end\n" +
                    "return false";

    private final Map<String, RedisLock> locks = new ConcurrentHashMap<>();

    private final String clientId = UUID.randomUUID().toString();

    private final String registryKey;

    private final StringRedisTemplate redisTemplate;

    private final RedisScript<Boolean> obtainLockScript;

    private final long expireAfter;

    /**
     * 默认本地锁超时失效时间
     */
    private final long defaultExpireUnusedOlderThanTime;

    /**
     * Constructs a lock registry with the default (60 second) lock expiration.
     *
     * @param connectionFactory The connection factory.
     * @param registryKey       The key prefix for locks.
     */
    public RedisLockRegistry(RedisConnectionFactory connectionFactory, String registryKey) {
        this(connectionFactory, registryKey, DEFAULT_EXPIRE_AFTER, DEFAULT_EXPIRE_UNUSED_OLDER_THEN_TIME);
    }

    /**
     * Constructs a lock registry with the supplied lock expiration.
     *
     * @param connectionFactory The connection factory.
     * @param registryKey       The key prefix for locks.
     * @param expireAfter       The expiration in milliseconds.
     */
    public RedisLockRegistry(RedisConnectionFactory connectionFactory, String registryKey, long expireAfter, long defaultExpireUnusedOlderThanTime) {
        Assert.notNull(connectionFactory, "'connectionFactory' cannot be null");
        Assert.notNull(registryKey, "'registryKey' cannot be null");
        this.redisTemplate = new StringRedisTemplate(connectionFactory);
        this.obtainLockScript = new DefaultRedisScript<>(OBTAIN_LOCK_SCRIPT, Boolean.class);
        this.registryKey = registryKey;
        this.expireAfter = expireAfter;
        this.defaultExpireUnusedOlderThanTime = defaultExpireUnusedOlderThanTime;
    }

    @Override
    public Lock obtain(Object lockKey) {
        Assert.isInstanceOf(String.class, lockKey);
        String path = (String) lockKey;
        return this.locks.computeIfAbsent(path, RedisLock::new);
    }

    @Override
    public void expireUnusedOlderThan(long age) {
        Iterator<Map.Entry<String, RedisLock>> iterator = this.locks.entrySet().iterator();
        long now = System.currentTimeMillis();
        while (iterator.hasNext()) {
            Map.Entry<String, RedisLock> entry = iterator.next();
            RedisLock lock = entry.getValue();
            if (now - lock.getLockedAt() > age && !lock.isAcquiredInThisProcess()) {
                iterator.remove();
            }
        }
    }

    @Override
    public long getDefaultExpireUnusedOlderThanTime() {
        return defaultExpireUnusedOlderThanTime;
    }

    /**
     * RedisLock implements Lock
     */
    private final class RedisLock implements Lock {

        // default:locks:   path
        private final String lockKey;

        private final ReentrantLock localLock = new ReentrantLock();

        private volatile long lockedAt;

        private RedisLock(String path) {
            this.lockKey = constructLockKey(path);
        }

        /**
         * 构造key
         */
        private String constructLockKey(String path) {
            return RedisLockRegistry.this.registryKey + ":" + path;
        }

        /**
         * 拿锁的时间戳
         */
        public long getLockedAt() {
            return this.lockedAt;
        }

        @Override
        public void lock() {
            this.localLock.lock();
            while (true) {
                try {
                    while (!obtainLock()) {
                        Thread.sleep(100); //NOSONAR
                    }
                    break;
                } catch (InterruptedException e) {
                    /*
                     * This method must be uninterruptible so catch and ignore
                     * interrupts and only break out of the while loop when
                     * we get the lock.
                     */
                } catch (Exception e) {
                    this.localLock.unlock();
                    rethrowAsLockException(e);
                }
            }
        }

        private void rethrowAsLockException(Exception e) {
            throw new CannotAcquireLockException("Failed to lock mutex at " + this.lockKey, e);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            this.localLock.lockInterruptibly();
            try {
                while (!obtainLock()) {
                    Thread.sleep(100); //NOSONAR
                }
            } catch (InterruptedException ie) {
                this.localLock.unlock();
                Thread.currentThread().interrupt();
                throw ie;
            } catch (Exception e) {
                this.localLock.unlock();
                rethrowAsLockException(e);
            }
        }

        @Override
        public boolean tryLock() {
            try {
                return tryLock(0, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        /**
         * 如果在给定的等待时间内没有被另一个线程 占用 ，并且当前线程尚未被 保留，则获取该锁（ interrupted）
         */
        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            long now = System.currentTimeMillis();
            // 如果在给定的等待时间内没有被另一个线程 占用 ，并且当前线程尚未被 保留，则获取该锁（ interrupted）
            if (!this.localLock.tryLock(time, unit)) {
                return false;
            }
            try {
                long expire = now + TimeUnit.MILLISECONDS.convert(time, unit);
                boolean acquired;
                // 尝试一直获取锁, 及根据
                while (!(acquired = obtainLock()) && System.currentTimeMillis() < expire) { //NOSONAR
                    Thread.sleep(100); //NOSONAR
                }
                if (!acquired) {
                    this.localLock.unlock();
                }
                return acquired;
            } catch (Exception e) {
                this.localLock.unlock();
                rethrowAsLockException(e);
            }
            return false;
        }

        /**
         * 获取锁
         */
        private boolean obtainLock() {
            boolean success = RedisLockRegistry.this.redisTemplate.execute(RedisLockRegistry.this.obtainLockScript,
                    Collections.singletonList(this.lockKey), RedisLockRegistry.this.clientId,
                    String.valueOf(RedisLockRegistry.this.expireAfter));
            if (success) {
                this.lockedAt = System.currentTimeMillis();
            }
            return success;
        }

        @Override
        public void unlock() {
            if (!this.localLock.isHeldByCurrentThread()) {
                throw new IllegalStateException("You do not own lock at " + this.lockKey);
            }
            // 查询当前线程对此锁的暂停数量。 如果大于1 释放锁
            if (this.localLock.getHoldCount() > 1) {
                this.localLock.unlock();
                return;
            }
            try {
                RedisLockRegistry.this.redisTemplate.delete(this.lockKey);
                if (logger.isDebugEnabled()) {
                    logger.debug("Released lock; " + this);
                }
            } finally {
                this.localLock.unlock();
            }
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("Conditions are not supported");
        }

        public boolean isAcquiredInThisProcess() {
            return RedisLockRegistry.this.clientId.equals(
                    RedisLockRegistry.this.redisTemplate.boundValueOps(this.lockKey).get());
        }

        @Override
        public String toString() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd@HH:mm:ss.SSS");
            return "RedisLock [lockKey=" + this.lockKey
                    + ",lockedAt=" + dateFormat.format(new Date(this.lockedAt))
                    + ", clientId=" + RedisLockRegistry.this.clientId
                    + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((this.lockKey == null) ? 0 : this.lockKey.hashCode());
            result = prime * result + (int) (this.lockedAt ^ (this.lockedAt >>> 32));
            result = prime * result + RedisLockRegistry.this.clientId.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            RedisLock other = (RedisLock) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (!this.lockKey.equals(other.lockKey)) {
                return false;
            }
            if (this.lockedAt != other.lockedAt) {
                return false;
            }
            return true;
        }

        private RedisLockRegistry getOuterType() {
            return RedisLockRegistry.this;
        }

    }
}
