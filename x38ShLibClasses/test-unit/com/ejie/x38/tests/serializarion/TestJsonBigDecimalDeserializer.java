/**
 * 
 */
package com.ejie.x38.tests.serializarion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.serialization.JsonBigDecimalDeserializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/**
 * @author llaparra
 *
 */
public class TestJsonBigDecimalDeserializer {
	private static BigDecimal bigDecimal;
	private static String strBigDecimal;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		TestJsonBigDecimalDeserializer.bigDecimal = new BigDecimal("123.1234567890123456789");
		TestJsonBigDecimalDeserializer.strBigDecimal = "\"123,1234567890123456789\"";
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.JsonBigDecimalDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)}.
	 */
	@Test
	public final void testDeserializeJsonParserDeserializationContext() {
		BigDecimal resultBigDecimal = null;
		JsonFactory factory = new JsonFactory();
		JsonParser jsonParser = null;
		try {
			resultBigDecimal = deserializeBigDecimal(TestJsonBigDecimalDeserializer.strBigDecimal, factory, jsonParser);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Ejecución de los métodos de prueba

		assertTrue("No se ha realizado la serialización del BigDecimal", resultBigDecimal != null);

		assertEquals("La serialización del BigDecimal no es correcta", resultBigDecimal, bigDecimal);
	}

	private BigDecimal deserializeBigDecimal(String strBigDecimal, JsonFactory factory, JsonParser jsonParser)
			throws IOException {
		BigDecimal bd = null;
		try {
			jsonParser = factory.createParser(strBigDecimal);
			jsonParser.nextToken();
			bd = new JsonBigDecimalDeserializer().deserialize(jsonParser, null);
			jsonParser.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jsonParser != null) {
				jsonParser.close();
			}
		}
		return bd;
	}

}
