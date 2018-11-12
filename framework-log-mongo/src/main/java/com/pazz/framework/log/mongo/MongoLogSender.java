package com.pazz.framework.log.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.pazz.framework.log.ILogSender;
import com.pazz.framework.log.entity.LogInfo;
import com.pazz.framework.log.exception.BufferedStateException;
import com.pazz.framework.util.string.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: Peng Jian
 * @create: 2018/11/12 14:38
 * @description:
 */
public class MongoLogSender implements ILogSender, InitializingBean, DisposableBean {

    public static final Log log = LogFactory.getLog(MongoLogSender.class);
    /**
     * 默认集合名称
     */
    public final static String DEFAULT_COLLECTION_NAME = LogInfo.class.getSimpleName();
    /**
     * 默认集合TTL失效索引名称
     */
    public final static String DEFAULT_COLLECTION_TTL_INDEX_NAME = "index_date_";
    /**
     * 默认TTL索引KEY
     */
    public final static String DEFAULT_COLLECTION_TTL_INDEX_KEY = "date";
    /**
     * 默认15天
     */
    public final static int DEFAULT_COLLECTION_TTL_INDEX_EXPIRE_SECONDS = 60 * 60 * 24 * 15;
    /**
     * mongodb
     */
    private MongoTemplate mongoTemplate;
    /**
     * 线程池
     */
    private ExecutorService threadPool = null;
    /**
     * 集合名称
     */
    private String collectionName = DEFAULT_COLLECTION_NAME;
    /**
     * TTL索引名称
     */
    private String collectionTTLIndexName = DEFAULT_COLLECTION_TTL_INDEX_NAME;
    /**
     * TTL索引名称
     */
    private String collectionTTLIndexKey = DEFAULT_COLLECTION_TTL_INDEX_KEY;
    /**
     * 请求ID索引KEY
     */
    private String collectionRequestIdIndexKey = "requestId";
    /**
     * 请求ID索引名称
     */
    private String collectionRequestIdIndexName = "index_requestId_";
    /**
     * TTL索引过期秒数
     * 默认15天
     */
    private int collectionTTLIndexExpireSeconds = DEFAULT_COLLECTION_TTL_INDEX_EXPIRE_SECONDS;

    private int threadSize = 5;

    public MongoLogSender(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void destroy() throws Exception {
        threadPool.shutdown(); //关闭线程池
        while (!threadPool.isTerminated()) {  //线程池是否终止
            try {
                synchronized (this) {
                    this.wait(1000);
                }
            } catch (InterruptedException e) {
                throw new BufferedStateException(e.getMessage());
            }
        }
    }

    @Override
    public void send(List<Object> msg) {
        threadPool.submit(new SendTask(msg));
    }

    @Override
    public void send(Object msg) {
        List<Object> list = new ArrayList<>();
        list.add(msg);
        threadPool.submit(new SendTask(list));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //创建索引
        createIndex();
        threadPool = new ThreadPoolExecutor(threadSize, threadSize, 3, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(5 * threadSize), new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 创建索引
     */
    private void createIndex() {
        try {
            List<IndexInfo> indexs = mongoTemplate.indexOps(collectionName).getIndexInfo();
            if (!existIndex(indexs, collectionTTLIndexName)) {
                //创建TTL索引
                mongoTemplate.indexOps(collectionName).ensureIndex(new Index().named(collectionTTLIndexName).on(collectionTTLIndexKey, Sort.Direction.DESC).expire(collectionTTLIndexExpireSeconds));
            }
            if (!existIndex(indexs, collectionRequestIdIndexName)) {
                //创建requestId索引
                mongoTemplate.indexOps(collectionName).ensureIndex(new Index().named(collectionRequestIdIndexName).on(collectionRequestIdIndexKey, Sort.Direction.ASC));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 索引是否存在
     */
    private boolean existIndex(List<IndexInfo> indexs, String indexName) {
        return indexs.stream().anyMatch(indexInfo -> StringUtil.equals(indexName, indexInfo.getName()));
    }

    public DBObject getDBObject(LogInfo logInfo) {
        DBObject obj = new BasicDBObject();
        Field[] fields = logInfo.getClass().getDeclaredFields();
        PropertyDescriptor pd = null;
        for (int i = 0; i < fields.length; i++) {
            try {
                pd = new PropertyDescriptor(fields[i].getName(), logInfo.getClass());
                Method m = pd.getReadMethod();
                obj.put(fields[i].getName(), m.invoke(logInfo));
            } catch (Exception e) {
                continue;
            }
        }
        return obj;
    }

    /**
     * 添加发送任务
     */
    private class SendTask implements Runnable {
        private List<Object> msg;

        public SendTask(List<Object> list) {
            this.msg = list;
        }

        @Override
        public void run() {
            try {
                List<DBObject> list = new Vector<DBObject>();
                for (Object obj : msg) {
                    LogInfo log = (LogInfo) obj;
                    list.add(getDBObject(log));
                }
                mongoTemplate.getCollection(collectionName).insert(list);
            } catch (Exception e) {
                log.error("MongoLogSender: Write Log error!", e);
            }
        }
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionTTLIndexName() {
        return collectionTTLIndexName;
    }

    public void setCollectionTTLIndexName(String collectionTTLIndexName) {
        this.collectionTTLIndexName = collectionTTLIndexName;
    }

    public int getCollectionTTLIndexExpireSeconds() {
        return collectionTTLIndexExpireSeconds;
    }

    public void setCollectionTTLIndexExpireSeconds(int collectionTTLIndexExpireSeconds) {
        this.collectionTTLIndexExpireSeconds = collectionTTLIndexExpireSeconds;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }
}
