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

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.serialization.JsonBigDecimalSerializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author llaparra
 *
 */
public class TestJsonNumberSerializer {
	private static BigDecimal bigDecimal1;
	private static BigDecimal bigDecimal2;
	private static String strBigDecimal1;
	private static String strBigDecimal2;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		TestJsonNumberSerializer.bigDecimal1 = new BigDecimal("123.1234567890123456789");
		TestJsonNumberSerializer.bigDecimal2 = new BigDecimal("123.9876543210987654321");
		TestJsonNumberSerializer.strBigDecimal1 = "\"123,123\"";
		TestJsonNumberSerializer.strBigDecimal2 = "\"123,988\"";
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.JsonNumberSerializer#serialize(java.math.BigDecimal, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 */
	@Test
	public final void testSerialize() {
		String resultBigDecimal1 = null;
		try {
			resultBigDecimal1 = this.serializeDo(TestJsonNumberSerializer.bigDecimal1);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal de dedondeo hacia abajo",
					StringUtils.isNotEmpty(resultBigDecimal1));
			assertEquals("La serialización del BigDecimal de dedondeo hacia abajo no es correcta", resultBigDecimal1,
					TestJsonNumberSerializer.strBigDecimal1);
		}

		String resultBigDecimal2 = null;
		try {
			resultBigDecimal1 = this.serializeDo(TestJsonNumberSerializer.bigDecimal2);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal de dedondeo hacia arriba",
					StringUtils.isNotEmpty(resultBigDecimal2));
			assertEquals("La serialización del BigDecimal de dedondeo hacia arriba no es correcta", resultBigDecimal2,
					TestJsonNumberSerializer.strBigDecimal2);
		}
	}

	/**
	 * @param bigDecimal
	 * @return String
	 * @throws IOException
	 */
	private String serializeDo(BigDecimal bigDecimal) throws IOException {
		String json = "";
		Writer jsonWriter = new StringWriter();
		SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
		JsonGenerator jsonGenerator = null;

		try {
			jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
			new JsonBigDecimalSerializer().serialize(bigDecimal, jsonGenerator, serializerProvider);
		} catch (Exception e) {
			e.printStackTrace();
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
