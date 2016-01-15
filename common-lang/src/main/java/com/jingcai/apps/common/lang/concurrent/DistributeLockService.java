package com.jingcai.apps.common.lang.concurrent;

import com.jingcai.apps.common.lang.string.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lejing on 16/1/5.
 */
public class DistributeLockService {
	private static final Logger log = LoggerFactory.getLogger(DistributeLockService.class);
	private final ThreadLocal<Class> threadLocal = new InheritableThreadLocal<Class>();
	private Map<Class, DistributeLock> map = new ConcurrentHashMap<Class, DistributeLock>(3);
	private String connectString;
	private String prefix;
	private CuratorFramework curatorFramework;

	private DistributeLock getDistributeLock(Class cls) {
		if (map.containsKey(cls)) {
			return map.get(cls);
		}
		DistributeLock idGenEntry = new DistributeLock(curatorFramework, prefix, cls);
		map.put(cls, idGenEntry);
		return idGenEntry;
	}

	public void lock(Class cls, String key) {
		DistributeLock distributeLock = getDistributeLock(cls);
		distributeLock.lock(key);
		threadLocal.set(cls);
	}

	public void unlock() {
		Class cls = threadLocal.get();
		if(null != cls) {
			DistributeLock idGenentry = getDistributeLock(cls);
			idGenentry.unlock();
			threadLocal.remove();
		}
	}

	public void init() {
		if (StringUtils.isEmpty(connectString)) {
			log.error("connectString can't be empty");
			return;
		}
		if (StringUtils.isEmpty(prefix)) {
			log.error("prefix can't be empty");
			return;
		}
		curatorFramework = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
		curatorFramework.start();
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

}
