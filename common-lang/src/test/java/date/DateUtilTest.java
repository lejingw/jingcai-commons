package date;

import com.jingcai.apps.common.lang.date.DateUtil;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lejing on 17/1/12.
 */
public class DateUtilTest {
	@Test
	public void test1() {
		System.out.println(DateUtil.lastDayOfMonth(new Date()));
	}

	@Test
	public void test2() {
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		ca.set(Calendar.DAY_OF_WEEK, ca.getActualMaximum(Calendar.DAY_OF_WEEK));
		System.out.println(ca.getTime());
	}

	@Test
	public void test3() {
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		ca.set(Calendar.DAY_OF_WEEK, 1);
//		ca.set(Calendar.DAY_OF_WEEK, ca.getActualMinimum(Calendar.DAY_OF_WEEK));
		System.out.println(ca.getTime());
	}

	@Test
	public void testCleanTime() {
		Date date = new Date();
		Date date2 = DateUtil.cleanTime(date);
		System.out.println(date + "---" + date.getTime());
		System.out.println(date2 + "---" + date2.getTime());
	}
}
