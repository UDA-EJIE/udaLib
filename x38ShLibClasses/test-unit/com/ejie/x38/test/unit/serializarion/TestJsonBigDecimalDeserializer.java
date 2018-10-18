package com.ejie.x38.test.unit.serializarion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import com.ejie.x38.serialization.JsonBigDecimalDeserializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/**
 * @author Eurohelp S.L.
 */
public class TestJsonBigDecimalDeserializer {
	private static BigDecimal bigDecimal;
	private static String strBigDecimalEs;
	private static String strBigDecimalEu;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		TestJsonBigDecimalDeserializer.bigDecimal = new BigDecimal("9123000.1234567890123456789");
		TestJsonBigDecimalDeserializer.strBigDecimalEs = "\"9.123.000,1234567890123456789\"";
		TestJsonBigDecimalDeserializer.strBigDecimalEu = "\"9.123.000,1234567890123456789\"";
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
			LocaleContextHolder.setLocale(new Locale("es"));
			resultBigDecimal = deserializeBigDecimal(TestJsonBigDecimalDeserializer.strBigDecimalEs, factory,
					jsonParser);
		} catch (IOException e) {
			fail("IOException deserializando el BigDecimal en castellano");
		}
		// Ejecución de los métodos de prueba
		assertTrue("No se ha realizado la serialización del BigDecimal en castellano", resultBigDecimal != null);
		assertEquals("La serialización del BigDecimal en castellano no es correcta", bigDecimal, resultBigDecimal);

		try {
			LocaleContextHolder.setLocale(new Locale("eu"));
			resultBigDecimal = deserializeBigDecimal(TestJsonBigDecimalDeserializer.strBigDecimalEu, factory,
					jsonParser);
		} catch (IOException e) {
			fail("IOException deserializando el BigDecimal en euskera");
		}
		// Ejecución de los métodos de prueba
		assertTrue("No se ha realizado la serialización del BigDecimal en euskera", resultBigDecimal != null);
		assertEquals("La serialización del BigDecimal en euskera no es correcta", bigDecimal, resultBigDecimal);
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
			fail("Exception deserializando el BigDecimal");
		} finally {
			if (jsonParser != null) {
				jsonParser.close();
			}
		}
		return bd;
	}

}
