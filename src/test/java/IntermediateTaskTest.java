import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntermediateTaskTest {
	@BeforeClass
	public static void setUpBeforeClass() {
	}

	@AfterClass
	public static void tearDownAfterClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	//just for demo purpose
	@Test
	public void test() {
		IntermediateTask.RequestStopWatch stopWatch = new IntermediateTask.RequestStopWatch();
		assertEquals(-1, stopWatch.getAverageTime());
		stopWatch.start();
		assertEquals(-1, stopWatch.getAverageTime());
		stopWatch.stop();
		assertTrue("stop watch should have positive average time", stopWatch.getAverageTime()>=0);
	}

}
