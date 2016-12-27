package com.jingcai.apps.common.lang.id;

import com.jingcai.apps.common.lang.exception.Exceptions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lejing on 16/1/5.
 */
public class IdGenEntry {
	private final Charset utf8 = Charset.forName("UTF-8");
	private final CuratorFramework curatorFramework;
	private final String lockPath;
	private final int stepLength;

	private final InterProcessMutex lock;
	private volatile int end = -1;
	private volatile AtomicInteger current = new AtomicInteger(end);

	public IdGenEntry(CuratorFramework curatorFramework, String lockPath, int stepLength) {
		this.curatorFramework = curatorFramework;
		this.lockPath = lockPath;
		this.stepLength = stepLength;
		this.lock = new InterProcessMutex(curatorFramework, lockPath);
	}

	private int bytes2Int(byte[] b) {
		int sum = 0;
		for (int i = 0; i < 4; i++) {
			int n = ((int) b[i]) & 0xff;
			n <<= (3 - i) * 8;
			sum = n + sum;
		}
		return sum;
	}

	private byte[] int2Bytes(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[3 - i] = (byte) ((value >> 8 * i) & 0xff);
		}
		return b;
	}

	private void checkAvail() throws Exception {
		if (current.get() >= end) {
			try {
				if (!lock.acquire(10, TimeUnit.SECONDS))
					throw new IllegalStateException("could not acquire the lock");

				if (current.get() >= end) {
					byte[] bytes = curatorFramework.getData().forPath(lockPath);
//					current.set((null == bytes || bytes.length < 1) ? 0 : bytes2Int(bytes));
//					int endtemp;
//					curatorFramework.setData().forPath(lockPath, int2Bytes(endtemp = current.get() + stepLength));
//					end = endtemp;

					current.set((null == bytes || bytes.length < 1) ? 0 : Integer.parseInt(new String(bytes, utf8)));
					int endtemp = current.get() + stepLength;
					curatorFramework.setData().forPath(lockPath, String.valueOf(endtemp).getBytes(utf8));
					end = endtemp;
				}
			} finally {
				lock.release();
			}
		}
	}

	public int nextId() {
		try {
			checkAvail();
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
		return current.incrementAndGet();
	}
}
