package com.pazz.framework.cache;

import com.pazz.framework.cache.provider.IBatchCacheProvider;
import com.pazz.framework.cache.storage.StrongCacheStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Date;
import java.util.Map;

/**
 * @author: Peng Jian
 * @create: 2018/11/7 15:05
 * @description: 只读缓存默认实现
 */
public abstract class DefaultStrongCache<K, V> implements IRefreshableCache<K, V>, InitializingBean, DisposableBean {

    private static Log LOG = LogFactory.getLog(DefaultStrongCache.class);

    //批量加载缓存(接口)
    private IBatchCacheProvider<K, V> cacheProvider;
    //只读缓存存储(类)
    private StrongCacheStorage<K, V> cacheStorage;

    /**
     * 自动刷新间隔时间,单位秒,默认15分钟
     */
    private long interval = 15L * 60 * 1000;
    /**
     * 数据提供者provider提供的数据最后修改时间,作为刷新缓存的时间戳
     */
    private Date modifyTime;

    /**
     * 自动刷新线程
     */
    private ReloadThread thread;

    /**
     * 初始化
     */
    public DefaultStrongCache() {
        this.cacheStorage = new StrongCacheStorage<>();
    }

    //设置自动刷新间隔时间
    public void setInterval(int seconds) {
        this.interval = (long) seconds * 1000;
    }

    /**
     * <p>刷新策略</p>
     */
    @Override
    public boolean refresh() {
        Date lastTime = cacheProvider.getLastModifyTime();
        if (modifyTime != null && lastTime != null && lastTime.after(modifyTime)) {
            Map<K, V> map = cacheProvider.get();
            cacheStorage.set(map);
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
    public void invalid() {
        cacheStorage.clear();
        //因为是批量加载所以全部失效考虑重新加载的问题
        Map<K, V> map = this.cacheProvider.get();
        cacheStorage.set(map);
        modifyTime = this.cacheProvider.getLastModifyTime();
    }

    @Override
    public void invalid(K key) {
        //cacheStorage.remove(key);
        throw new RuntimeException("Strong Cache Cannot Invalid Part!");
    }

    @Override
    public void invalidMulti(K... keys) {
        throw new RuntimeException("Strong Cache Cannot Invalid Part!");
    }


    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        CacheManager.getInstance().registerCacheProvider(this);
        Map<K, V> map = this.cacheProvider.get();
        cacheStorage.set(map);
        modifyTime = cacheProvider.getLastModifyTime();
        thread = new ReloadThread("STRONG_CACHE_" + this.getUUID());
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void destroy() throws Exception {
        while (thread.state != 2) {
            synchronized (this) {
                this.wait(301 * 1000);
            }
        }
        thread.interrupt();
    }

    @Override
    public final V get(K key) {
        return cacheStorage.get(key);
    }

    @Override
    public final Map<K, V> get() {
        return cacheStorage.get();
    }

    /**
     * 内部线程类
     */
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
                    //刷新间隔
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage());
                    break;
                }
                try {
                    state = 1;
                    //如果最后更新时间早于当前时间
                    //更新所有数据
                    refresh();
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                    break;
                }
            }
        }
    }
}
