package concurrent.spring;

import com.jingcai.apps.common.lang.id.UuidGen;
import com.jingcai.apps.common.lang.concurrent.DistributeLockService;
import org.junit.Test;

/**
 * Created by lejing on 16/1/5.
 */
public class DistributeLockServiceTest {
	@Test
	public void testLock(){
		String connectString = "192.168.0.11:2181,192.168.0.18:2181,192.168.0.19:2181";
		String prefix = "/examples";
		DistributeLockService service = new DistributeLockService();
		service.setConnectString(connectString);
		service.setPrefix(prefix);
		service.init();

		service.lock(UuidGen.class, "1");
		try{
			Thread.sleep(10000);
			System.out.println("----------------");
		}catch (Exception e){

		}finally {
			service.unlock();
		}
	}
}
