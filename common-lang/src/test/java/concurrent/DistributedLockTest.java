package concurrent;

import com.jingcai.apps.common.lang.concurrent.DistributedLock;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ExceptionDepthComparator;

import java.util.concurrent.CountDownLatch;

/**
 * ConcurrentTask[] tasks = new ConcurrentTask[5];
 * for(int i=0;i<tasks.length;i++){
 * 		tasks[i] = new ConcurrentTask(){
 * 			public void run() {
 * 				System.out.println("==============");
 * 			}};
 * }
 * new ConcurrentTest(tasks);
 */
public class DistributedLockTest {
	private static final Logger logger = LoggerFactory.getLogger(DistributedLockTest.class);
	public static final String ZOOKEEPER_URI = "192.168.0.11:2181,192.168.0.18:2181,192.168.0.19:2181";

	@Ignore
	@Test
	public void test2(){
		new Thread(new Runnable() {
			public void run() {
				logger.debug("----------------------b1");
				//try {Thread.sleep(1000);} catch (InterruptedException e) {}

				final DistributedLock lock = new DistributedLock(ZOOKEEPER_URI, "test2");
				try{
					lock.lock();
					logger.debug("----------------------b getlock");
					//try {startSignal.await();} catch (InterruptedException e) {e.printStackTrace();}
				}catch (Exception e){
					logger.error("b", e);
				}finally {
					logger.debug("----------------------b releaselock");
					lock.unlock();
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				//try {startSignal.await();} catch (InterruptedException e) {e.printStackTrace();}
				logger.debug("----------------------a1");

				final DistributedLock lock = new DistributedLock(ZOOKEEPER_URI, "test2");
				try{
					lock.lock();
					logger.debug("----------------------a getlock");
					try {Thread.sleep(3000);} catch (InterruptedException e) {}
					//startSignal.countDown();
				}catch (Exception e){
					logger.error("a", e);
				}finally {
					logger.debug("----------------------a releaselock");
					lock.unlock();
				}
			}
		}).start();
		try {Thread.sleep(10000000);} catch (InterruptedException e) {}
	}


}