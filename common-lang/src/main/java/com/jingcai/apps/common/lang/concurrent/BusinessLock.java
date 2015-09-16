package com.jingcai.apps.common.lang.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lejing on 15/9/16.
 */
public class BusinessLock implements InitializingBean, DisposableBean {
    private Logger logger = LoggerFactory.getLogger(BusinessLock.class);
    private Map<Object, ReentrantLock> conditionMap = new HashMap<Object, ReentrantLock>();
    private final ReentrantLock putLock = new ReentrantLock();
    private final ThreadLocal<ReentrantLock> threadLocal = new InheritableThreadLocal<ReentrantLock>();
    private ScheduledExecutorService service;

    /**
     * @param obj
     * @return true put成功 如果obj已经存在，wait
     * false   put失败，如obj为null
     */
    public boolean lock(Object obj) {
        if (null == obj) return false;
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        ReentrantLock cond = null;
        try {
            cond = conditionMap.get(obj);
            if (null == cond) {
                conditionMap.put(obj, cond = new ReentrantLock());
            }
        } finally {
            putLock.unlock();
        }
        cond.lock();
        threadLocal.set(cond);
        return true;
    }

    public void unlock() {
        ReentrantLock cond = threadLocal.get();
        cond.unlock();
    }

    public void afterPropertiesSet() throws Exception {
        Runnable runnable = new Runnable() {
            public void run() {
                putLock.lock();
                try {
                    logger.debug("clear ReentrantLock");
                    conditionMap.clear();
                } finally {
                    putLock.unlock();
                }
            }
        };
        service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 2, 2, TimeUnit.SECONDS);
        logger.debug("BusinessLock[{}] service started", this);
    }

    public void destroy() throws Exception {
        logger.debug("BusinessLock[{}] service shutdown", this);
        if (null != service && !service.isShutdown()) {
            service.shutdownNow();
        }
    }
}