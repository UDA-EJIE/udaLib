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
		TestJsonNumberSerializer.bigDecimal1 = new BigDecimal("123.000000");
		TestJsonNumberSerializer.bigDecimal2 = new BigDecimal("123.9876543210987654321");
		TestJsonNumberSerializer.strBigDecimal1 = "\"123\"";
		TestJsonNumberSerializer.strBigDecimal2 = "\"123,9876543210987654321\"";
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
			fail("IOException realizando la serialización del BigDecimal quitando ceros a la derecha");
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal quitando ceros a la derecha",
					StringUtils.isNotEmpty(resultBigDecimal1));
			assertEquals("La serialización del BigDecimal quitando ceros a la derecha no es correcta",
					TestJsonNumberSerializer.strBigDecimal1, resultBigDecimal1);
		}

		String resultBigDecimal2 = null;
		try {
			resultBigDecimal2 = this.serializeDo(TestJsonNumberSerializer.bigDecimal2);
		} catch (IOException e) {
			fail("IOException realizando la serialización del BigDecimal");
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal", StringUtils.isNotEmpty(resultBigDecimal2));
			assertEquals("La serialización del BigDecimal no es correcta", TestJsonNumberSerializer.strBigDecimal2,
					resultBigDecimal2);
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
			fail("Exception en la serialización");
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
