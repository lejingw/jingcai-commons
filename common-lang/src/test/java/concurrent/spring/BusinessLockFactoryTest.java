package concurrent.spring;

import com.jingcai.apps.common.lang.concurrent.BusinessLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;

/**
 * Created by lejing on 15/10/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ServletTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@ContextConfiguration(locations = {"classpath:BusinessLocakFactory.xml"})
public class BusinessLockFactoryTest {
	@Autowired
	private ApplicationContext context;
	@Qualifier("lock1")
	@Autowired
	private BusinessLock lock1;
	@Qualifier("lock2")
	@Autowired
	private BusinessLock lock2;

	@Test
	public void test1() {
		//ApplicationContext context = new ClassPathXmlApplicationContext("BusinessLocakFactory.xml");
//		System.out.println(context.getBean("lock1", BusinessLock.class));
//		System.out.println(context.getBean(BusinessLock.class));

		System.out.println(context.getBean("&businessLock"));
		System.out.println("-------------------------");
		System.out.println(context.getBean("lock1"));
		System.out.println(context.getBean("lock2"));
		System.out.println("-------------------------");

		System.out.println(context.getBean("lock1"));
		System.out.println(context.getBean("lock2"));
		System.out.println("-------------------------");

		System.out.println(lock1);
		System.out.println(lock2);
	}
}
