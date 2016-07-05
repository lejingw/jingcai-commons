import com.jingcai.apps.common.lang.id.UuidGen;
import com.jingcai.apps.common.lang.id.IdGenerator;
import com.jingcai.apps.common.lang.id.IdGenEntry;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IdGenDistributeLock {
	private static final Logger log = LoggerFactory.getLogger(IdGenDistributeLock.class);


	@Test
	public void testWithThreads() throws Exception {
		int QTY = 2;
		ExecutorService service = Executors.newFixedThreadPool(QTY);
		for (int i = 0; i < QTY; ++i) {
			Callable<Void> task = new Callable<Void>() {
				public Void call() throws Exception {
					test();
					return null;
				}
			};
			service.submit(task);
		}
		service.shutdown();
		service.awaitTermination(10, TimeUnit.MINUTES);
	}

	public IdGenerator create() throws Exception {
//		String connectString = "192.168.0.11:2181,192.168.0.18:2181,192.168.0.19:2181";
		String connectString = "101.200.184.166:2181,101.200.231.74:2181,101.200.157.23:2181";
		String prefix = "/qualitydev";
		IdGenerator idGenerator = new IdGenerator();
		idGenerator.setConnectString(connectString);
		idGenerator.setPrefix(prefix);
		idGenerator.setStepLength(2);

		idGenerator.init();
		return idGenerator;
	}
	private Class[] clsArr = new Class[]{UuidGen.class, IdGenEntry.class, IdGenDistributeLock.class};
	public void test() throws Exception {
		IdGenerator idGenerator = create();
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
class LoginLogService{}