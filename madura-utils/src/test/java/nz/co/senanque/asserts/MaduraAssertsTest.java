package nz.co.senanque.asserts;

import org.junit.Test;

public class MaduraAssertsTest {

	@Test(expected = IllegalArgumentException.class)
	public void testAssertNotNullObjectString() {
		MaduraAsserts.assertNotNull("unexpected null value for test 1",null);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testAssertNullObjectString() {
		MaduraAsserts.assertNull("");
	}

	@Test(expected = NotEqualsException.class)
	public void testAssertEqualsObjectObjectString() {
		MaduraAsserts.assertEquals("xyz", "abc", "expected %s found %s");
	}

	@Test(expected = NotTrueException.class)
	public void testAssertTrue() {
		MaduraAsserts.assertTrue(false);
	}

	@Test(expected = NotEqualsException.class)
	public void testAssertSame() {
		String a = "abc";
		String b = "cde";
		MaduraAsserts.assertSame(a,b);
	}
	@Test(expected = SameException.class)
	public void testAssertNotSame() {
		String a = "abc";
		MaduraAsserts.assertNotSame(a,a);
	}

}
