package com.ejie.x38.test.unit.serializarion;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.EnumMap;
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
 * @author Eurohelp S.L.
 */
public class TestUdaModule {

	private static final String EL_GET_DEBE_REFLEJAR_EL_SET = "El get debe reflejar el set";
	private static UdaModule udaModule = null;
	private static Map<SerializationFeature, Boolean> serializationMap = null;
	private static Map<DeserializationFeature, Boolean> deserializationMap = null;
	private static Map<MapperFeature, Boolean> mapperMap = null;
	private static List<Include> aList = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		udaModule = new UdaModule();
		serializationMap = new EnumMap<SerializationFeature, Boolean>(SerializationFeature.class);
		serializationMap.put(SerializationFeature.INDENT_OUTPUT, true);
		deserializationMap = new EnumMap<DeserializationFeature, Boolean>(DeserializationFeature.class);
		deserializationMap.put(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
		mapperMap = new EnumMap<MapperFeature, Boolean>(MapperFeature.class);
		mapperMap.put(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		aList = new ArrayList<Include>();
		aList.add(Include.ALWAYS);
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.UdaModule#getSerializationFeature()}.
	 */
	@Test
	public final void testGetSetSerializationFeature() {
		udaModule.setSerializationFeature(serializationMap);
		assertTrue(EL_GET_DEBE_REFLEJAR_EL_SET, udaModule.getSerializationFeature().equals(serializationMap));
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.UdaModule#getDeserializationFeature()}.
	 */
	@Test
	public final void testGetSetDeserializationFeature() {
		udaModule.setDeserializationFeature(deserializationMap);
		assertTrue(EL_GET_DEBE_REFLEJAR_EL_SET, udaModule.getDeserializationFeature().equals(deserializationMap));
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.UdaModule#getSerializationInclusions()}.
	 */
	@Test
	public final void testGetSetSerializationInclusions() {
		udaModule.setSerializationInclusions(aList);
		assertTrue(EL_GET_DEBE_REFLEJAR_EL_SET, udaModule.getSerializationInclusions().equals(aList));
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.UdaModule#getMapperFeature()}.
	 */
	@Test
	public final void testGetSetMapperFeature() {
		udaModule.setMapperFeature(mapperMap);
		assertTrue(EL_GET_DEBE_REFLEJAR_EL_SET, udaModule.getMapperFeature().equals(mapperMap));
	}

}
