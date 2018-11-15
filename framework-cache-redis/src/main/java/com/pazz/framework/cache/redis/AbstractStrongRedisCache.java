package com.pazz.framework.cache.redis;

import com.pazz.framework.cache.CacheManager;
import com.pazz.framework.cache.IRefreshableCache;
import com.pazz.framework.cache.exception.KeyIsNotFoundException;
import com.pazz.framework.cache.exception.ValueIsBlankException;
import com.pazz.framework.cache.exception.ValueIsNullException;
import com.pazz.framework.cache.provider.IBatchCacheProvider;
import com.pazz.framework.cache.redis.storage.RedisCacheStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.RedisConnectionFailureException;

import java.util.Date;
import java.util.Map;

/**
 * @author: Peng Jian
 * @create: 2018/11/8 10:30
 * @description: redis储存库
 */
public abstract class AbstractStrongRedisCache<K, V> implements IRefreshableCache<K, V>, InitializingBean, DisposableBean {

    /**
     * 日志
     */
    private static final Log LOG = LogFactory.getLog(AbstractStrongRedisCache.class);

    private IBatchCacheProvider<K, V> cacheProvider;

    private RedisCacheStorage<K, V> cacheStorage;

    private long interval = 15L * 60 * 1000;

    private Date modifyTime;

    private ReloadThread thread;

    private String prefix = "framework.redis.strong.initialization.";


    public void setCacheProvider(IBatchCacheProvider<K, V> cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    public void setCacheStorage(RedisCacheStorage<K, V> cacheStorage) {
        this.cacheStorage = cacheStorage;
    }

    public void setInterval(int seconds) {
        this.interval = (long) seconds * 1000;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {
        CacheManager.getInstance().registerCacheProvider(this);
        cacheStorage.initializationStrongCache(getUUID(), cacheProvider.get());
        modifyTime = cacheProvider.getLastModifyTime();
        //启动刷新线程
        thread = new ReloadThread("STRONG_REDIS_CACHE_" + this.getUUID());
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public V get(K key) {
        V value = null;
        try {
            value = cacheStorage.hget(getUUID(),key);
        } catch(ValueIsBlankException e) {
            //key存在，value为空串
            return null;
        } catch(ValueIsNullException ex) {
            //key存在，value为null
            return null;
        } catch(KeyIsNotFoundException ex1) {
            //key不存在
            return null;
        } catch(RedisConnectionFailureException exx) {
            //redis 连接出现异常
            LOG.error("redis 连接异常!");
            return null;
        }
        return value;
    }

    @Override
    public Map<K, V> get() {
        try {
            return cacheStorage.hget(getUUID());
        } catch(RedisConnectionFailureException e) {
            //redis 连接出现异常
            LOG.error("redis 连接异常!");
            return cacheProvider.get();
        }

    }

    /**
     * <p>刷新策略</p>
     */
    @Override
    public boolean refresh() {
        Date lastTime = cacheProvider.getLastModifyTime();
        if (lastTime != null && modifyTime != null && lastTime.after(modifyTime)) {
            Map<K, V> map = cacheProvider.get();
            for (Map.Entry<K, V> entry : map.entrySet()) {
                cacheStorage.hset(getUUID(), entry.getKey(), entry.getValue());
            }
            modifyTime = lastTime;
            return true;
        }
        return false;
    }

    @Override
    public boolean refresh(K... keys) {
        throw new RuntimeException("Strong Cache Cannot Refresh Part!");
    }

    @Override
    public void destroy() throws Exception {

    }

    private class ReloadThread extends Thread {
        private volatile int state;

        ReloadThread(String threadName) {
            super(threadName);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    state = 2;
                    //刷新间隔时间
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                try {
                    state = 1;
                    //如果最后更新时间早于当前时间
                    //更新所有数据
                    refresh();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    break;
                }
            }
        }
    }

    /**
     * <p>重新初始化缓存数据</p>
     */
    @Override
    public void invalid() {
        cacheStorage.hremove(getUUID());
        cacheStorage.hremove(prefix+getUUID());
        cacheStorage.initializationStrongCache(getUUID(), cacheProvider.get());
        modifyTime = cacheProvider.getLastModifyTime();
    }

    @Override
    public void invalid(K key) {
        throw new RuntimeException("Strong Cache Cannot Invalid Part!");
    }

    @Override
    public void invalidMulti(K... keys) {
        throw new RuntimeException("Strong Cache Cannot Invalid Part!");
    }

}
