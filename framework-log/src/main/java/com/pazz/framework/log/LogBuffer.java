package com.pazz.framework.log;

import com.pazz.framework.log.exception.BufferedInitException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: Peng Jian
 * @create: 2018/11/13 15:43
 * @description: 该类用来作为日志的buffer, 使用队列的结构
 */
public class LogBuffer implements ILogBuffer, InitializingBean, DisposableBean {

    private static final Log log = LogFactory.getLog(LogBuffer.class);
    /**
     * 阻塞队列元素的长度
     */
    private int queueSize = 20;
    /**
     * 队列中数组的长度
     */
    private int listSize = 5000;
    /**
     * 是否启动日志缓存
     */
    private boolean enable = true;
    /**
     * 日志发送者
     */
    private ILogSender logSender;
    /**
     * 定时刷新时间间隔,单位秒,默认30分钟
     */
    private long interval = 30L * 60 * 1000;
    /**
     * 刷新线程
     */
    private FlushThread thread;
    /**
     * 日志缓冲开关标志
     */
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    /**
     * 日志集合队列
     */
    private BlockingDeque<ArrayList<Object>> queuePool;

    /**
     * 属性注入完毕后，调用初始化方法
     * 初始化阻塞队列、线程池以及阻塞队列中的缓冲区
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        init();
        thread = new FlushThread(this.getClass().getName());
        thread.setDaemon(true);  //设置为守护线程
        thread.start();
    }

    /**
     * 如果队列的元素个数和队列的元素的容量设置正确，进行下面操作:
     * 1、 初始化日志缓冲
     * 2、初始化队列中的缓冲区
     */
    private void init() {
        check(); //检测
        initQueues(); //实例队列
    }

    /**
     * 初始化队列中的缓冲区
     */
    private void initQueues() {
        queuePool = new LinkedBlockingDeque<>(queueSize);
        for (int i = 0; i < queueSize; i++) {
            ArrayList<Object> list = new ArrayList<>(listSize);
            try {
                queuePool.put(list);
            } catch (InterruptedException e) {
                throw new BufferedInitException("LogBuffer initQueues error!");
            }
        }
    }

    /**
     * 检查队列的初始化元素个数、队列元素的初始化容量是否正确，
     * 若不正确则抛出初始化异常
     */
    private void check() {
        if (listSize <= 0) {
            throw new BufferedInitException("listSize can not <= 0");
        }
        if (queueSize < 1) {
            throw new BufferedInitException("queueSize can not < 1");
        }
    }

    /**
     * 强制关闭LogBuffer,可能会抛出线程异常,关闭时内存中的日志信息将会丢失
     *
     * @see DisposableBean#destroy()
     */
    @Override
    public void destroy() {
        thread.interrupt(); //中断线程
        shutdown.set(true); //设置开关
    }

    @Override
    public void write(Object obj) {
        try {
            //判断日志缓冲的状态
            if (shutdown.get()) {
                log.debug("Logbuffer has closed!");
                return;
            }
            //默认开启
            if (!enable) {
                return;
            }
            if (obj == null) {
                return;
            }
            if (logSender == null) {
                return;
            }
            //从队头取出缓冲块
            ArrayList list = queuePool.takeFirst();
            //将日志放入缓冲块
            list.add(obj);
            //如果缓冲块已满,则新建一个发送任务交给线程池发送
            //并且新建一个空的缓冲块放入队尾
            if (list.size() == listSize) {
                queuePool.putLast(new ArrayList(listSize));
                if (logSender == null) {
                    log.debug("LogSender didn't find the corresponding configuration! shutdown LogBuffer!");
                    shutdown.set(true);
                    return;
                }
                logSender.send(list);
            } else {//否则将缓冲块放回队头
                queuePool.putFirst(list);
            }
        } catch (Exception e) {
            log.error("Write Log error! shutdown LogBuffer!", e);
            shutdown.set(true);
            return;
        }
    }

    @Override
    public int getQueueSize() {
        return queueSize;
    }

    @Override
    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    @Override
    public int getListSize() {
        return listSize;
    }

    @Override
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public ILogSender getLogSender() {
        return logSender;
    }

    @Override
    public void setLogSender(ILogSender logSender) {
        this.logSender = logSender;
    }

    @Override
    public void setInterval(long interval) {
        this.interval = interval * 1000;
    }

    //线程
    private class FlushThread extends Thread {

        public FlushThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    //判断日志缓冲的状态
                    if (shutdown.get()) {
                        log.debug("Logbuffer has closed!");
                        return;
                    }
                    //默认开启
                    if (!enable) {
                        return;
                    }
                    Thread.sleep(interval);
                    //从队头取出缓冲块
                    ArrayList<Object> list = queuePool.pollFirst();
                    if (list != null && list.size() > 0) {
                        //如果缓冲块已满,则新建一个发送任务交给线程池发送
                        //并且新建一个空的缓冲块放入队尾
                        queuePool.offerLast(new ArrayList<Object>(listSize));
                        if (logSender == null) {
                            log.error("LogSender didn't find the corresponding configuration! shutdown LogBuffer!");
                            shutdown.set(true);
                            return;
                        }
                        logSender.send(list);
                    } else {
                        queuePool.putFirst(list);
                    }
                } catch (Exception e) {
                    log.error("Flush log error! shutdown LogBuffer!", e);
                    shutdown.set(true);
                }
            }
        }
    }

}
