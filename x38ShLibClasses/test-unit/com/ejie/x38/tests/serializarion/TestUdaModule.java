/**
 * 
 */
package com.ejie.x38.tests.serializarion;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.serialization.UdaModule;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author llaparra
 *
 */
public class TestUdaModule {

	private static UdaModule udaModule = null;
	private static Map<SerializationFeature, Boolean> serializationMap = null;
	private static Map<DeserializationFeature, Boolean> deserializationMap = null;
	private static Map<MapperFeature, Boolean> mapperMap = null;
	private static List<Include> aList = null;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		udaModule = new UdaModule();
		serializationMap = new HashMap<SerializationFeature, Boolean>();
		serializationMap.put(SerializationFeature.INDENT_OUTPUT, true);
		deserializationMap = new HashMap<DeserializationFeature, Boolean>();
		deserializationMap.put(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
		mapperMap = new HashMap<MapperFeature, Boolean>();
		mapperMap.put(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		aList = new ArrayList<Include>();
		aList.add(Include.ALWAYS);
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.UdaModule#getSerializationFeature()}.
	 */
	@Test
	public final void testGetSerializationFeature() {
		udaModule.setSerializationFeature(serializationMap);
		assertTrue("El get debe reflejar el set", udaModule.getSerializationFeature().equals(serializationMap));
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.UdaModule#setSerializationFeature(java.util.Map)}.
	 */
	@Test
	public final void testSetSerializationFeature() {
		udaModule.setSerializationFeature(serializationMap);
		assertTrue("El get debe reflejar el set", udaModule.getSerializationFeature().equals(serializationMap));
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.UdaModule#getDeserializationFeature()}.
	 */
	@Test
	public final void testGetDeserializationFeature() {
		udaModule.setDeserializationFeature(deserializationMap);
		assertTrue("El get debe reflejar el set", udaModule.getDeserializationFeature().equals(deserializationMap));
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.UdaModule#setDeserializationFeature(java.util.Map)}.
	 */
	@Test
	public final void testSetDeserializationFeature() {
		udaModule.setDeserializationFeature(deserializationMap);
		assertTrue("El get debe reflejar el set", udaModule.getDeserializationFeature().equals(deserializationMap));
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.UdaModule#getSerializationInclusions()}.
	 */
	@Test
	public final void testGetSerializationInclusions() {
		udaModule.setSerializationInclusions(aList);
		assertTrue("El get debe reflejar el set", udaModule.getSerializationInclusions().equals(aList));
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.UdaModule#setSerializationInclusions(java.util.List)}.
	 */
	@Test
	public final void testSetSerializationInclusions() {
		udaModule.setSerializationInclusions(aList);
		assertTrue("El get debe reflejar el set", udaModule.getSerializationInclusions().equals(aList));
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.UdaModule#getMapperFeature()}.
	 */
	@Test
	public final void testGetMapperFeature() {
		udaModule.setMapperFeature(mapperMap);
		assertTrue("El get debe reflejar el set", udaModule.getMapperFeature().equals(mapperMap));
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.UdaModule#setMapperFeature(java.util.Map)}.
	 */
	@Test
	public final void testSetMapperFeature() {
		udaModule.setMapperFeature(mapperMap);
		assertTrue("El get debe reflejar el set", udaModule.getMapperFeature().equals(mapperMap));
	}

}
