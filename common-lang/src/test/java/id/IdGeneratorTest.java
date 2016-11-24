package id;

import com.jingcai.apps.common.lang.id.IdGenerator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IdGeneratorTest {
	private static final Logger log = LoggerFactory.getLogger(IdGeneratorTest.class);
	private static final int THREAD = 20;
	private static final int COUNT_PER_THREAD = 1000;
	private static IdGenerator idGenerator;
	private static ExecutorService service;

	@BeforeClass
	public static void startup() throws Exception {
		idGenerator = create();
		service = Executors.newFixedThreadPool(THREAD);
	}

	@AfterClass
	public static void shutdown() {
		idGenerator.destroy();
		service.shutdown();
	}

	@Test
	public void testWithThreads() throws Exception {
		CountDownLatch startlatch = new CountDownLatch(1);
		CountDownLatch finishlatch = new CountDownLatch(THREAD);

		for (int i = 0; i < THREAD; ++i) {
			final int threadno = i;
			service.submit(() -> {
				try {
					startlatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				test(threadno);
				finishlatch.countDown();
			});
		}
		Thread.sleep(1000);
		System.out.println("-------------------start");
		long starttime = System.currentTimeMillis();
		startlatch.countDown();

		finishlatch.await();
		System.out.println("-------------------finish");
		System.out.println("QPS:" + THREAD * COUNT_PER_THREAD / ((System.currentTimeMillis() - starttime) / 1000D));
	}

	private static IdGenerator create() throws Exception {
//		String connectString = "192.168.0.11:2181,192.168.0.18:2181,192.168.0.19:2181";
//		String connectString = "101.200.184.166:2181,101.200.231.74:2181,101.200.157.23:2181";
//		String connectString = "djip:2181";
		String connectString = "localhost:2181";
		String prefix = "/caiwa";
		IdGenerator idGenerator = new IdGenerator();
		idGenerator.setConnectString(connectString);
		idGenerator.setPrefix(prefix);
		idGenerator.setStepLength(2);

		idGenerator.init();
		return idGenerator;
	}

//	private Class[] clsArr = new Class[]{UuidGen.class, IdGenEntry.class, IdGenDistributeLock.class};

	private void test(int threadno) {
//		Random random = new Random();
		for (int i = 0; i < COUNT_PER_THREAD; i++) {
//			Class cls = clsArr[random.nextInt(clsArr.length)];
			log.info("{}\t\t{}", threadno, idGenerator.nextId(LoginLogService.class));
		}
	}

	//@Test
//	public void test2() throws Exception {
//		curatorFramework.inTransaction().check().forPath(lockPath).and().setData().forPath(lockPath, String.valueOf("abc").getBytes(Charset.forName("UTF-8"))).and().commit();
//	}
	class LoginLogService {
	}
}
