/*******************************************************************************
 * Copyright (c)2015 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.asserts;

import java.lang.reflect.Constructor;

/**
 * @author Roger Parkinson
 *
 */
public class MaduraAsserts {

	private MaduraAsserts() {
		// never instantiate
	}
	
	private static RuntimeException getRuntimeException(String message, Class<? extends RuntimeException> clazz) {
		RuntimeException runtimeException;
		try {
			Constructor<?> constructor = clazz.getConstructor(String.class);
			runtimeException = (RuntimeException)constructor.newInstance(message);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return runtimeException;
	}

	// NotNull
	public static void assertNotNull(Object arg) {
		assertNotNull("unexpected null value",arg,IllegalArgumentException.class);
	}

	public static void assertNotNull(String message,Object arg) {
		assertNotNull(message,arg,IllegalArgumentException.class);
	}

	public static void assertNotNull(String message,Object arg, Class<? extends RuntimeException> clazz)  {
		if (arg == null) {
			throw getRuntimeException(String.format(message),clazz);
		}
	}
	// Null
	public static void assertNull(Object arg) {
		assertNull("unexpected not-null value %s",arg,IllegalArgumentException.class);
	}

	public static void assertNull(String message,Object arg) {
		assertNull(message,arg,IllegalArgumentException.class);
	}

	public static void assertNull(String message,Object arg, Class<? extends RuntimeException> clazz)  {
		if (arg != null) {
			throw getRuntimeException(String.format(message,arg),clazz);
		}
	}
	// Equals (Object)
	public static void assertEquals(Object arg0,Object arg1)  {
		assertEquals("expected %s found %s",arg0,arg1,NotEqualsException.class);
	}
	public static void assertEquals(String message,Object arg0,Object arg1)  {
		assertEquals(message,arg0,arg1,NotEqualsException.class);
	}
	public static void assertEquals(String message,Object arg0,Object arg1, Class<? extends RuntimeException> clazz)  {
		if (arg0 == null && arg1 == null) {
			return;
		}
		if (arg1 == null || arg0 == null || !arg1.equals(arg0)) {
			throw getRuntimeException(String.format(message,arg0,arg1),clazz);
		}
	}
	// Equals (int)
	public static void assertEquals(int arg0,int arg1)  {
		assertEquals("expected %s found %s",arg0,arg1,NotEqualsException.class);
	}
	public static void assertEquals(String message,int arg0,int arg1)  {
		assertEquals(message,arg0,arg1,NotEqualsException.class);
	}
	public static void assertEquals(String message,int arg0,int arg1, Class<? extends RuntimeException> clazz)  {
		if (arg0 != arg1) {
			throw getRuntimeException(String.format(message,arg0,arg1),clazz);
		}
	}
	// Equals (long)
	public static void assertEquals(long arg0,long arg1)  {
		assertEquals("expected %s found %s",arg0,arg1,NotEqualsException.class);
	}
	public static void assertEquals(String message,long arg0,long arg1)  {
		assertEquals(message,arg0,arg1,NotEqualsException.class);
	}
	public static void assertEquals(String message,long arg0,long arg1, Class<? extends RuntimeException> clazz)  {
		if (arg0 != arg1) {
			throw getRuntimeException(String.format(message,arg0,arg1),clazz);
		}
	}

	// Same
	public static void assertSame(Object arg0,Object arg1)  {
		assertSame("%s is not the same as %s",arg0,arg1,NotEqualsException.class);
	}
	public static void assertSame(String message,Object arg0,Object arg1)  {
		assertSame(message,arg0,arg1,NotEqualsException.class);
	}
	public static void assertSame(String message,Object arg0,Object arg1, Class<? extends RuntimeException> clazz)  {
		if (arg0 == null && arg1 == null) {
			return;
		}
		if (arg1 == null || arg0 == null || System.identityHashCode(arg0) != System.identityHashCode(arg1)) {
			throw getRuntimeException(String.format(message,arg0,arg1),clazz);
		}
	}

	// NotSame
	public static void assertNotSame(Object arg0,Object arg1)  {
		assertNotSame("two objects are the same %s %s",arg0,arg1,SameException.class);
	}
	public static void assertNotSame(String message,Object arg0,Object arg1)  {
		assertNotSame(message,arg0,arg1,SameException.class);
	}
	public static void assertNotSame(String message, Object arg0,Object arg1,Class<? extends RuntimeException> clazz)  {
		if (arg0 == null && arg1 == null) {
			return;
		}
		if (arg1 == null || arg0 == null || System.identityHashCode(arg0) == System.identityHashCode(arg1)) {
			throw getRuntimeException(String.format(message,System.identityHashCode(arg0),arg1),clazz);
		}
	}

	// True
	public static void assertTrue(boolean arg0)  {
		assertTrue("Not true",arg0,NotTrueException.class);
	}
	public static void assertTrue(boolean arg0,String message)  {
		assertTrue(message,arg0,NotTrueException.class);
	}
	public static void assertTrue(String message,boolean arg0, Class<? extends RuntimeException> clazz)  {
		if (!arg0 ) {
			throw getRuntimeException(String.format(message),clazz);
		}
	}

	// NotTrue
	public static void assertNotTrue(boolean arg0)  {
		assertNotTrue("Is true",arg0,TrueException.class);
	}
	public static void assertNotTrue(boolean arg0,String message)  {
		assertNotTrue(message,arg0,TrueException.class);
	}
	public static void assertNotTrue(String message,boolean arg0, Class<? extends RuntimeException> clazz)  {
		if (arg0 ) {
			throw getRuntimeException(String.format(message),clazz);
		}
	}

}
