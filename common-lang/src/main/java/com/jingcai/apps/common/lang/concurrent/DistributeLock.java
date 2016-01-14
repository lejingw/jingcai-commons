package com.jingcai.apps.common.lang.concurrent;

import com.jingcai.apps.common.lang.exception.Exceptions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

/**
 * Created by lejing on 16/1/5.
 */
public class DistributeLock {
	private static final String PATH_PATTERN = "%s/locks/%s";
	private static final String PATH_PATTERN2 = "%s/%s";
	private final ThreadLocal<InterProcessMutex> threadLocal = new InheritableThreadLocal<InterProcessMutex>();
	private final CuratorFramework curatorFramework;
	private final String lockPath;

	public DistributeLock(CuratorFramework curatorFramework, String prefix, Class cls) {
		this.curatorFramework = curatorFramework;
		this.lockPath = String.format(PATH_PATTERN, prefix, cls.getSimpleName());
	}

	/**
	 * @param key
	 * @return true put成功 如果obj已经存在，wait
	 * false   put失败，如obj为null
	 */
	public void lock(final String key) {
		InterProcessMutex lock = new InterProcessMutex(curatorFramework, String.format(PATH_PATTERN2, lockPath, key));
		boolean successflag = false;
		try {
			successflag = lock.acquire(10, TimeUnit.SECONDS);
		}catch (Exception e){
			throw Exceptions.unchecked(e);
		}
		if(!successflag){
			throw new IllegalStateException("could not acquire the lock");
		}
		threadLocal.set(lock);
	}

	public void unlock() {
		InterProcessMutex lock = threadLocal.get();
		if (null != lock) {
			try {
				lock.release();
			}catch (Exception e){
				throw Exceptions.unchecked(e);
			}
			threadLocal.remove();
		}
	}
}
