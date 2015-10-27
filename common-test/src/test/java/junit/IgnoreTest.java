package junit;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.annotation.Repeat;

@Ignore
public class IgnoreTest {
	@Ignore
	@Test
	public void aa() {
		System.out.println("----1-----");
	}
	@Test
	public void bb() {
		System.out.println("----2-----");
	}
}