package junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		CalculatorTest.class,
		SquareTest.class
})
public class SuiteTests {
}