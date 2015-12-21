package com.jingcai.apps.common.lang.concurrent;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * DistributedLock lock = null;
 * try {
 * 		lock = new DistributedLock("127.0.0.1:2182","test");
 * 		lock.lock();
 * 		//do something...
 * } catch (Exception e) {
 * 		e.printStackTrace();
 * } finally {
 * 		if(lock != null)
 * 			lock.unlock();
 * }
 */
public class DistributedLock implements Lock, Watcher {
	private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);
	private final String splitStr = "_lock_";
	private final String root = "/locks";//根
	private ZooKeeper zk;
	private String lockName;//竞争资源的标志
	private String waitNode;//等待前一个锁
	private String myZnode;//当前锁
	private CountDownLatch latch;//计数器
	private int sessionTimeout = 30000;

	/**
	 * 创建分布式锁,使用前请确认address配置的zookeeper服务可用
	 *
	 * @param address  127.0.0.1:2181
	 * @param lockName 竞争资源标志,lockName中不能包含单词lock
	 */
	public DistributedLock(String address, String lockName) {
		if (lockName.contains(splitStr)) {
			throw new LockException("lockName can not contains " + splitStr);
		}
		this.lockName = lockName;
		try {
			this.zk = new ZooKeeper(address, sessionTimeout, this);
			Stat stat = zk.exists(root, false);
			if (stat == null) {
				zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);// 创建根节点
				logger.debug("create root node {}", root);
			}
		} catch (Exception e) {
			throw new LockException("could not connect to zookeeper!");
		}
	}

	/**
	 * zookeeper节点的监视器
	 */
	public void process(WatchedEvent event) {
		if (this.latch != null) {
			this.latch.countDown();
		}
	}

	public void lock() {
		boolean locked = tryLock(sessionTimeout, TimeUnit.MILLISECONDS);
		if(!locked){
			throw new LockException("get lock timeout");
		}
	}

	public boolean tryLock() {
		try {
			//创建临时子节点
			myZnode = zk.create(root + "/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.debug("create node {}", myZnode);
			//取出所有子节点
			List<String> subNodes = zk.getChildren(root, false);
			//取出所有lockName的锁
			List<String> lockObjNodes = new ArrayList<String>();
			for (String node : subNodes) {
				String _node = node.split(splitStr)[0];
				if (_node.equals(lockName)) {
					lockObjNodes.add(node);
				}
			}
			Collections.sort(lockObjNodes);
			if (myZnode.equals(root + "/" + lockObjNodes.get(0))) {
				//如果是最小的节点,则表示取得锁
				logger.debug("get lock {}", myZnode);
				return true;
			}
			//如果不是最小的节点，找到比自己小1的节点
			String subMyZnode = myZnode.substring(myZnode.lastIndexOf("/") + 1);
			waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyZnode) - 1);
		} catch (KeeperException e) {
			throw new LockException(e);
		} catch (InterruptedException e) {
			throw new LockException(e);
		}
		return false;
	}

	public boolean tryLock(long timeout, TimeUnit unit) {
		try {
			if (this.tryLock()) {
				logger.debug("get lock {}", myZnode);
				return true;
			}
			return waitForLock(waitNode, timeout, unit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean waitForLock(String lower, long timeout, TimeUnit unit) throws InterruptedException, KeeperException {
		Stat stat = zk.exists(root + "/" + lower, true);
		//判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
		if (stat != null) {
			logger.debug("waiting for lock {}", root + "/" + lower);
			this.latch = new CountDownLatch(1);
			this.latch.await(timeout, unit);
			this.latch = null;
		}
		return true;
	}

	public void unlock() {
		try {
			logger.debug("unlock {}", myZnode);
			zk.delete(myZnode, -1);
			myZnode = null;
			zk.close();
		} catch (InterruptedException e) {
			logger.error("interrupted", e);
		} catch (KeeperException e) {
			logger.error("keeper", e);
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		this.lock();
	}

	public Condition newCondition() {
		return null;
	}

	public class LockException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public LockException(String e) {
			super(e);
		}

		public LockException(Exception e) {
			super(e);
		}
	}

}