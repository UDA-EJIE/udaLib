/**
 * 
 */
package com.ejie.x38.tests.serializarion;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.serialization.ThreadSafeCache;

/**
 * @author llaparra
 *
 */
public class TestThreadSafeCache {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.ThreadSafeCache#addValue(java.lang.String, java.lang.String)}.
	 */
	@SuppressWarnings("static-access")
	@Test
	public final void testAddValue() {
		String val = "1";
		ThreadSafeCache.addValue("val1", val);
		assertTrue("Debe haber añadido el conjunto clave-valor", val.equals(ThreadSafeCache.getMap().get("val1")));
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.ThreadSafeCache#getMap()}.
	 */
	@Test
	public final void testGetMap() {
		Map expected = new HashMap();
		expected.put("val1", "1");
		Map obj = ThreadSafeCache.getMap();
		
		assertTrue("Debe devolver el mismo objeto", expected.equals(obj));
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.ThreadSafeCache#clearCurrentThreadCache()}.
	 */
	@Test
	public final void testClearCurrentThreadCache() {
		Map expected = new HashMap();
		ThreadSafeCache.clearCurrentThreadCache();
		Map obj = ThreadSafeCache.getMap();
		
		assertTrue("Debe devolver el mismo objeto", expected.equals(obj));
	}

}
