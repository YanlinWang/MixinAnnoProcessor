package UnitTest;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import test.TestExpression;
import test.TestLombok;

public class AutoTest {
	@Test
	public void testExpression() {
		String s = "3" + "\n";
		s += "7" + "\n";
		s += "-1" + "\n";
		s += "3 = 3" + "\n";
		s += "(3 + 4) = 7" + "\n";
		assertEquals(s, TestExpression.runTest());
	}
	@Test
	public void testLombok() {
		String s = "p1 = Point.of(2, 3);" + "\n";
		s += "p2 = p1.clone();" + "\n";
		s += "print p1: 2 3" + "\n";
		s += "p1.x(4); p1.y(5);" + "\n";
		s += "print p1: 4 5" + "\n";
		s += "print p2: 2 3" + "\n";
		assertEquals(s, TestLombok.runTest());
	}
}
