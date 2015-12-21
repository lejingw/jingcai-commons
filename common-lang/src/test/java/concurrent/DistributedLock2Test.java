package concurrent;

import com.jingcai.apps.common.lang.concurrent.DistributedLock;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ConcurrentTask[] tasks = new ConcurrentTask[5];
 * for(int i=0;i<tasks.length;i++){
 * tasks[i] = new ConcurrentTask(){
 * public void run() {
 * System.out.println("==============");
 * }};
 * }
 * new ConcurrentTest(tasks);
 */
public class DistributedLock2Test {
	private static final Logger logger = LoggerFactory.getLogger(DistributedLock2Test.class);
	public static final String ZOOKEEPER_URI = "192.168.0.11:2181,192.168.0.18:2181,192.168.0.19:2181";
	private CountDownLatch startSignal = new CountDownLatch(1);//开始阀门
	private CountDownLatch doneSignal = null;//结束阀门
	private CopyOnWriteArrayList<Long> list = new CopyOnWriteArrayList<Long>();
	private AtomicInteger err = new AtomicInteger();//原子递增
	private ConcurrentTask[] tasks = null;

	public DistributedLock2Test(ConcurrentTask... tasks) {
		this.tasks = tasks;
		if (tasks == null) {
			System.out.println("tasks can not null");
			System.exit(1);
		}
		doneSignal = new CountDownLatch(tasks.length);
		start();
	}

	private void start() {
		//创建线程，并将所有线程等待在阀门处
		createThread();
		//打开阀门
		startSignal.countDown();//递减锁存器的计数，如果计数到达零，则释放所有等待的线程
		try {
			doneSignal.await();//等待所有线程都执行完毕
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//计算执行时间
		getExeTime();
		System.out.println("--------------" + count + "-------------");
	}

	/**
	 * 初始化所有线程，并在阀门处等待
	 */
	private void createThread() {
		long len = doneSignal.getCount();
		for (int i = 0; i < len; i++) {
			final int j = i;
			new Thread(new Runnable() {
				public void run() {
					try {
						startSignal.await();//使当前线程在锁存器倒计数至零之前一直等待
						long start = System.currentTimeMillis();
						tasks[j].run();
						long end = (System.currentTimeMillis() - start);
						list.add(end);
					} catch (Exception e) {
						err.getAndIncrement();//相当于err++
					}
					doneSignal.countDown();
				}
			}).start();
		}
	}

	/**
	 * 计算平均响应时间
	 */
	private void getExeTime() {
		int size = list.size();
		List<Long> _list = new ArrayList<Long>(size);
		_list.addAll(list);
		Collections.sort(_list);
		long min = _list.get(0);
		long max = _list.get(size - 1);
		long sum = 0L;
		for (Long t : _list) {
			sum += t;
		}
		long avg = sum / size;
		System.out.println("min: " + min);
		System.out.println("max: " + max);
		System.out.println("avg: " + avg);
		System.out.println("err: " + err.get());
	}

	public interface ConcurrentTask {
		void run();
	}

	static int count = 0;

	public static void main(String[] args) {
		Runnable task1 = new Runnable() {
			public void run() {
				DistributedLock lock = null;
				try {
					lock = new DistributedLock(ZOOKEEPER_URI, "test1");
					//lock = new DistributedLock("127.0.0.1:2182","test2");
					lock.lock();
					Thread.sleep(3000);
					System.out.println("===Thread " + Thread.currentThread().getId() + " running");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (lock != null)
						lock.unlock();
				}
			}
		};
		new Thread(task1).start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		ConcurrentTask[] tasks = new ConcurrentTask[10];
		for (int i = 0; i < tasks.length; i++) {
			final int j = i;
			ConcurrentTask task3 = new DistributedLock2Test.ConcurrentTask() {
				public void run() {
					DistributedLock lock = null;
					try {
						lock = new DistributedLock(ZOOKEEPER_URI, "test2");
						lock.lock();
						count += j;
						System.out.println("Thread " + Thread.currentThread().getId() + " running");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						lock.unlock();
					}

				}
			};
			tasks[i] = task3;
		}
		new DistributedLock2Test(tasks);
	}

}