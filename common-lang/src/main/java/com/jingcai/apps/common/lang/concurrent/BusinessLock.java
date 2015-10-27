package com.jingcai.apps.common.lang.concurrent;

import org.apache.commons.collections.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lejing on 15/9/16.
 */
public class BusinessLock {
	private static Logger logger = LoggerFactory.getLogger(BusinessLock.class);
	private final Map<Object, ReentrantLock> lockMap;
	private final ThreadLocal<ReentrantLock> threadLocal = new InheritableThreadLocal<ReentrantLock>();
	private final ThreadLocal<Object> threadLocal2 = new InheritableThreadLocal<Object>();

	public BusinessLock() {
		lockMap = Collections.synchronizedMap(new LRUMap(100));
	}

	public BusinessLock(int maxSize) {
		lockMap = Collections.synchronizedMap(new LRUMap(maxSize));
	}

	/**
	 * @param obj
	 * @return true put成功 如果obj已经存在，wait
	 * false   put失败，如obj为null
	 */
	public void lock(final Object obj) {
		if (null == obj) return;
		ReentrantLock cond = lockMap.get(obj);
		if (null == cond) {
			lockMap.put(obj, cond = new ReentrantLock());
		}
		cond.lock();
		logger.debug("lock object[{}]", obj);
		threadLocal.set(cond);
		threadLocal2.set(obj);
	}

	public void unlock() {
		ReentrantLock cond = threadLocal.get();
		if (null != cond) {
			logger.debug("unlock object[{}]", threadLocal2.get());
			cond.unlock();
			threadLocal.remove();
			threadLocal2.remove();
		}
	}
}