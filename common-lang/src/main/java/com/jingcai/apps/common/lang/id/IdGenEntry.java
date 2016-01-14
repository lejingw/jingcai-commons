package com.jingcai.apps.common.lang.id;

import com.jingcai.apps.common.lang.exception.Exceptions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lejing on 16/1/5.
 */
public class IdGenEntry {
	private final Charset utf8 = Charset.forName("UTF-8");
	private final CuratorFramework curatorFramework;
	private final String lockPath;
	private final int stepLength;

	private final InterProcessMutex lock;
	private final Lock innerLock = new ReentrantLock();
	private volatile AtomicInteger current = new AtomicInteger(-1);
	private volatile int end = -1;

	public IdGenEntry(CuratorFramework curatorFramework, String lockPath, int stepLength) {
		this.curatorFramework = curatorFramework;
		this.lockPath = lockPath;
		this.stepLength = stepLength;
		this.lock = new InterProcessMutex(curatorFramework, lockPath);
	}

	private void checkAvail() throws Exception {
		if (current.get() >= end) {
			if (!lock.acquire(10, TimeUnit.SECONDS)) {
				throw new IllegalStateException("could not acquire the lock");
			}
			try {
				if (current.get() >= end) {
					byte[] bytes = curatorFramework.getData().forPath(lockPath);
					if (null == bytes || bytes.length < 1) {
						current.set(0);
					} else {
						current.set(Integer.parseInt(new String(bytes, utf8)));
					}
					end = current.get() + stepLength;
					curatorFramework.setData().forPath(lockPath, String.valueOf(end).getBytes(utf8));
				}
			} finally {
				lock.release();
			}
		}
	}

	//	private Random random = new Random();
	public int nextId() {
		try {
			checkAvail();
//			Thread.sleep(random.nextInt(1000));
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
		if (innerLock.tryLock()) {
			try{
				return current.incrementAndGet();
			}finally {
				innerLock.unlock();
			}
		}
		return -1;
	}
}
