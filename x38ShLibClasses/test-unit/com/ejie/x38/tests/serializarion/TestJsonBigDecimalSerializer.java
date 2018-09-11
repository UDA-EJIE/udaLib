/**
 * 
 */
package com.ejie.x38.tests.serializarion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import com.ejie.x38.serialization.JsonBigDecimalSerializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author llaparra
 *
 */
public class TestJsonBigDecimalSerializer {
	private static BigDecimal bigDecimal;
	private static String strBigDecimalEs;
	private static String strBigDecimalEu;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		TestJsonBigDecimalSerializer.bigDecimal = new BigDecimal("9123000.1234567890123456789");
		TestJsonBigDecimalSerializer.strBigDecimalEs = "\"9.123.000,1234567890123456789\"";
		TestJsonBigDecimalSerializer.strBigDecimalEu = "\"9.123.000,1234567890123456789\"";
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.JsonBigDecimalSerializer#serialize(java.math.BigDecimal, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 */
	@Test
	public final void serializeBigDecimal() {
		String resultBigDecimal = null;
		try {
			LocaleContextHolder.setLocale(new Locale("es"));
			resultBigDecimal = this.serializeBigDecimalDo(TestJsonBigDecimalSerializer.bigDecimal);
		} catch (IOException e) {
			fail("IOException serializando el BigDecimal en castellano");
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal en castellano",
					StringUtils.isNotEmpty(resultBigDecimal));
			assertEquals("La serialización del BigDecimal en castellano no es correcta",
					TestJsonBigDecimalSerializer.strBigDecimalEs, resultBigDecimal);
		}

		try {
			LocaleContextHolder.setLocale(new Locale("eu"));
			resultBigDecimal = this.serializeBigDecimalDo(TestJsonBigDecimalSerializer.bigDecimal);
		} catch (IOException e) {
			fail("IOException serializando el BigDecimal en euskera");
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal en euskera",
					StringUtils.isNotEmpty(resultBigDecimal));
			assertEquals("La serialización del BigDecimal en euskera no es correcta",
					TestJsonBigDecimalSerializer.strBigDecimalEu, resultBigDecimal);
		}
	}

	/**
	 * @param bigDecimal
	 * @return String
	 * @throws IOException
	 */
	private String serializeBigDecimalDo(BigDecimal bigDecimal) throws IOException {
		String json = "";
		Writer jsonWriter = new StringWriter();
		SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
		JsonGenerator jsonGenerator = null;

		try {
			jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
			new JsonBigDecimalSerializer().serialize(bigDecimal, jsonGenerator, serializerProvider);
		} catch (Exception e) {
			fail("Exception serializando el BigDecimal");
		} finally {
			if (jsonGenerator != null) {
				jsonGenerator.close();
				jsonGenerator.flush();

				json = String.valueOf(jsonWriter.toString());

				jsonWriter.close();
				jsonWriter.flush();
			}
		}
		return json;
	}

}
