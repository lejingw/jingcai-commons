import com.jingcai.apps.common.lang.id.IdGenEntry;
import com.jingcai.apps.common.lang.id.IdGenerator;
import com.jingcai.apps.common.lang.id.UuidGen;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.*;

public class IdGenDistributeLock {
	private static final Logger log = LoggerFactory.getLogger(IdGenDistributeLock.class);


	@Test
	public void testWithThreads() throws Exception {
		IdGenerator idGenerator = create();

		int QTY = 20;
		CountDownLatch latch = new CountDownLatch(1);
		CountDownLatch finishlatch = new CountDownLatch(QTY);
		ExecutorService service = Executors.newFixedThreadPool(QTY);
		for (int i = 0; i < QTY; ++i) {
			service.submit(() -> {
				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				test(idGenerator);
			});
		}
		Thread.sleep(1000);
		System.out.println("-------------------start");
		latch.countDown();
		finishlatch.await();
		System.out.println("-------------------finish");
		idGenerator.destroy();
		service.shutdown();
	}

	public IdGenerator create() throws Exception {
//		String connectString = "192.168.0.11:2181,192.168.0.18:2181,192.168.0.19:2181";
//		String connectString = "101.200.184.166:2181,101.200.231.74:2181,101.200.157.23:2181";
		String connectString = "djip:2181";
		String prefix = "/qualitydev";
		IdGenerator idGenerator = new IdGenerator();
		idGenerator.setConnectString(connectString);
		idGenerator.setPrefix(prefix);
		idGenerator.setStepLength(2);

		idGenerator.init();
		return idGenerator;
	}

//	private Class[] clsArr = new Class[]{UuidGen.class, IdGenEntry.class, IdGenDistributeLock.class};

	public void test(IdGenerator idGenerator) {
		Random random = new Random();
		for (int i = 0; i < 1000; i++) {
//			Class cls = clsArr[random.nextInt(clsArr.length)];
			Class cls = LoginLogService.class;
			log.info("{}\t\t{}", cls.getSimpleName(), idGenerator.nextId(cls));
		}
	}

	//@Test
//	public void test2() throws Exception {
//		curatorFramework.inTransaction().check().forPath(lockPath).and().setData().forPath(lockPath, String.valueOf("abc").getBytes(Charset.forName("UTF-8"))).and().commit();
//	}
}

class LoginLogService {
}