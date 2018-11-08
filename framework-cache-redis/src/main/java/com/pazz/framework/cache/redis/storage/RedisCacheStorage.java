package com.pazz.framework.cache.redis.storage;

import com.pazz.framework.cache.exception.KeyIsNotFoundException;
import com.pazz.framework.cache.exception.ValueIsNullException;
import com.pazz.framework.cache.redis.exception.RedisCacheStorageException;
import com.pazz.framework.cache.storage.IRemoteCacheStorage;
import com.pazz.framework.util.string.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: Peng Jian
 * @create: 2018/11/8 10:41
 * @description:
 */
public class RedisCacheStorage<K, V> implements IRemoteCacheStorage<K, V>, InitializingBean {

    private RedisTemplate redisTemplate;
    /**
     * 日志
     */
    Log log = LogFactory.getLog(getClass());
    /**
     * 默认数据过期时间
     */
    private int expire = 60 * 10;
    /**
     * 稍后重试初始化Strong cache 延迟时间，默认1分钟
     */
    private int strongDelayTime = 60 * 1000;
    /**
     * 初始化Strong cache任务
     */
    private StrongCacheTask strongTask;
    /**
     * 初始化加锁key
     */
    private final String lockKeyPrefix = "framework.redis.lock.";
    /**
     * 初始化加锁Value
     */
    private final String lockValue = "1";
    /**
     * 是否初始化成功
     */
    private final String initSuccessPrefix = "framework.redis.strong.initialization.";

    @Override
    public boolean set(K key, V value) {
        return set(key, value, expire);
    }

    @Override
    public boolean set(K key, V value, int exp) {
        if (key == null) {
            throw new RedisCacheStorageException("key does not allow for null!");
        }
        redisTemplate.opsForValue().set(key, value, exp, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new RedisCacheStorageException("key does not allow for null!");
        }
        boolean exist = redisTemplate.hasKey(key);
        if (!exist) {
            throw new KeyIsNotFoundException("key is not found!");
        }
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            throw new ValueIsNullException("key exists, value is null!");
        }
        return (V) obj;
    }

    @Override
    public void remove(K key) {
        redisTemplate.delete(key);
    }

    @Override
    public void removeMulti(K... keys) {
        redisTemplate.delete(keys);
    }

    private Boolean setNx(final String key, final String value) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisSerializer serializer = new StringRedisSerializer();
                Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value));
                return success;
            }
        });
    }

    public void initializationStrongCache(final String cacheId, Map<K, V> map) {
        try {
            Boolean isSuccess = (Boolean) redisTemplate.opsForValue().get(initSuccessPrefix + cacheId);
            if (isSuccess == null || !isSuccess) {
                Boolean lock = setNx(lockKeyPrefix + cacheId, lockValue);
                if (lock) {
                    initializationStrongCacheData(cacheId, map);
                } else {
                    // 稍后重试
                    retryStorage(cacheId, map);
                    return;
                }
            }
        } catch (RedisConnectionFailureException e) {
            log.error("redis连接出现异常");
        }
    }

    private void retryStorage(String cacheId, Map<K, V> map) {
        if (strongTask == null) {
            strongTask = new StrongCacheTask("重试初始化Strong Cache任务", cacheId, map);
            strongTask.setDaemon(true);
            strongTask.start();
        } else if (strongTask.getState().name().equals(Thread.State.NEW.name())) {
            strongTask.start();
        } else if (strongTask.getState().name().equals(Thread.State.TERMINATED.name())) {
            strongTask = new StrongCacheTask("重试初始化Strong Cache任务", cacheId, map);
            strongTask.setDaemon(true);
            strongTask.start();
        }
    }

    public boolean hset(String cacheId, K key, V value) {
        if (key == null) {
            throw new RedisCacheStorageException("key does not allow for null!");
        }
        redisTemplate.opsForHash().put(cacheId, key, value);
        return true;
    }

    public Map<K, V> hget(String cacheId){
        if(StringUtil.isBlank(cacheId)){
            throw new RedisCacheStorageException("cacheId does not allow for null!");
        }
        Map<Object, Object> map = redisTemplate.opsForHash().entries(cacheId);
        Map<K, V> result = null;
        //由string转成对象
        if (map != null) {
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                if (result == null) {
                    result = new HashMap<K, V>();
                }
                result.put((K) entry.getKey(), (V) entry.getValue());
            }
            return result;
        }
        return null;
    }

    public void hremove(final String cacheId){
        if(StringUtil.isBlank(cacheId)){
            throw new RedisCacheStorageException("cacheId does not allow for null!");
        }
        redisTemplate.delete(cacheId);
    }

    @SuppressWarnings("unchecked")
    public V hget(String cacheId, K key) {
        if (key == null) {
            throw new RedisCacheStorageException("key does not allow for null!");
        }
        //key是否存在
        boolean exist = redisTemplate.opsForHash().hasKey(cacheId, key);
        if (!exist) {
            throw new KeyIsNotFoundException("key is not found!");
        }
        Object obj = redisTemplate.opsForHash().get(cacheId, key);
        if (obj == null) {
            throw new ValueIsNullException("key exists, value is null!");
        }
        return (V) obj;
    }

    private void initializationStrongCacheData(final String cacheId, Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                log.error("storage cache initialization error: key and value does not allow for null!");
                setNx(initSuccessPrefix + cacheId, "false");
                redisTemplate.delete(lockKeyPrefix + cacheId);
                return;
            }
            redisTemplate.opsForHash().put(cacheId, entry.getKey(), entry.getValue());
        }
        setNx(initSuccessPrefix + cacheId, "true");
        redisTemplate.delete(lockKeyPrefix + cacheId);
    }

    class StrongCacheTask extends Thread {
        int count = 1;
        String cacheId;
        Map<K, V> map;

        public StrongCacheTask(String name, String cacheId, Map<K, V> map) {
            super(name);
            count = 1;
            this.cacheId = cacheId;
            this.map = map;
        }

        @Override
        public void run() {
            log.debug("初始化Strong Cache任务开始，延迟" + strongDelayTime + "后开始执行!");
            while (true) {
                if (map == null || map.isEmpty()) {
                    return;
                }
                try {
                    this.sleep(strongDelayTime);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
                log.debug("第" + count + "次尝试初始化Strong Cache!");
                count++;
                Boolean isSuccess = (Boolean) redisTemplate.opsForValue().get(initSuccessPrefix + cacheId);
                if (isSuccess == null || !isSuccess) {
                    Boolean lock = setNx(lockKeyPrefix + cacheId, lockValue);
                    if (lock) {
                        initializationStrongCacheData(cacheId, map);
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
