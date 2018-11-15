package com.pazz.framework.cache.redis;

import com.pazz.framework.cache.CacheManager;
import com.pazz.framework.cache.ICache;
import com.pazz.framework.cache.exception.KeyIsNotFoundException;
import com.pazz.framework.cache.exception.ValueIsBlankException;
import com.pazz.framework.cache.exception.ValueIsNullException;
import com.pazz.framework.cache.provider.ITTLCacheProvider;
import com.pazz.framework.cache.redis.storage.RedisCacheStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author: Peng Jian
 * @create: 2018/11/8 10:30
 * @description: TTL类型的缓存
 */
public abstract class AbstractTTLRedisCache<V> implements ICache<String, V>, InitializingBean, DisposableBean {

    /**
     * 日志
     */
    private static final Log LOG = LogFactory.getLog(AbstractTTLRedisCache.class);
    /**
     * 数据提供者
     */
    protected ITTLCacheProvider<V> cacheProvider;
    /**
     * 数据储存器
     */
    protected RedisCacheStorage<String, V> cacheStorage;
    /**
     * 超时时间,单位秒,默认10分钟
     */
    protected int timeOut = 0;

    /**
     * 设置数据提供者
     */
    public void setCacheProvider(ITTLCacheProvider<V> cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    /**
     * 设置数据储存器
     */
    public void setCacheStorage(RedisCacheStorage<String, V> cacheStorage) {
        this.cacheStorage = cacheStorage;
    }

    /**
     * 设置超时时间
     */
    public void setTimeOut(int seconds) {
        this.timeOut = seconds;
    }

    /**
     * 根据uuid和key生成key
     */
    protected String getKey(String key) {
        return getUUID() + "_" + key;
    }

    /**
     * 获取数据
     * 如果返回null就是真的没有数据，无需再去数据库再取
     */
    @Override
    public V get(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new RuntimeException("key does not allow for null!");
        }
        V value = null;
        try {
            value = cacheStorage.get(getKey(key));
        } catch (ValueIsBlankException e) {
            LOG.warn("缓存[" + getUUID() + "]，key[" + key + "]存在，value为空串，返回结果[null]");
            //key存在，value为空串
            return null;
        } catch (ValueIsNullException ex) {
            //key存在，value为null
            LOG.warn("缓存[" + getUUID() + "]，key[" + key + "]存在，value为null，返回结果[null]");
            return null;
        } catch (KeyIsNotFoundException ex1) {
            //key不存在
            value = cacheProvider.get(key);
            LOG.warn("缓存[" + getUUID() + "]，key[" + key + "]不存在，走数据库查询，返回结果[" + value + "]");
            if (timeOut > 0) {
                cacheStorage.set(getKey(key), value, timeOut);
            } else {
                cacheStorage.set(getKey(key), value);
            }
        } catch (RedisConnectionFailureException exx) {
            //redis 连接出现异常
            value = cacheProvider.get(key);
            LOG.warn("redis连接出现异常，走数据库查询!");
            return value;
        } catch (Exception e) {
            //其他异常
            e.printStackTrace();
            value = cacheProvider.get(key);
            return value;
        }
        return value;
    }

    @Override
    public Map<String, V> get() {
        throw new RuntimeException(getUUID() + ":TTLCache cannot get all!");
    }

    @Override
    public void invalid() {
        throw new RuntimeException(getUUID() + ":TTLCache cannot invalid all!");
    }

    @Override
    public void invalid(String key) {
        cacheStorage.remove(getKey(key));
    }

    /**
     * 失效数据
     */
    @Override
    public void invalidMulti(String... keys) {
        if (keys == null) return;
        String[] skeys = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            skeys[i] = getKey(keys[i]);
        }
        cacheStorage.removeMulti(skeys);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void afterPropertiesSet() {
        CacheManager.getInstance().registerCacheProvider(this);
    }
}
