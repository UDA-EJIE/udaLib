/**
 * 
 */
package com.ejie.x38.test.junit.unit.serializarion;

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

import com.ejie.x38.serialization.JsonNumberSerializer;
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
	private static BigDecimal bigDecimal3;
	private static String strBigDecimalEs1;
	private static String strBigDecimalEs2;
	private static String strBigDecimalEs3;
	private static String strBigDecimalEu1;
	private static String strBigDecimalEu2;
	private static String strBigDecimalEu3;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		TestJsonNumberSerializer.bigDecimal1 = new BigDecimal("9123000.000000");
		TestJsonNumberSerializer.bigDecimal2 = new BigDecimal("9123000.9876543210987654321");
		TestJsonNumberSerializer.bigDecimal3 = new BigDecimal("9123000.9873543210987654321");
		TestJsonNumberSerializer.strBigDecimalEs1 = "\"9.123.000\"";
		TestJsonNumberSerializer.strBigDecimalEs2 = "\"9.123.000,988\"";
		TestJsonNumberSerializer.strBigDecimalEs3 = "\"9.123.000,987\"";
		TestJsonNumberSerializer.strBigDecimalEu1 = "\"9.123.000\"";
		TestJsonNumberSerializer.strBigDecimalEu2 = "\"9.123.000,988\"";
		TestJsonNumberSerializer.strBigDecimalEu3 = "\"9.123.000,987\"";
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.JsonNumberSerializer#serialize(java.math.BigDecimal, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 */
	@Test
	public final void testSerialize() {
		String resultBigDecimal1 = null;
		String resultBigDecimal2 = null;
		String resultBigDecimal3 = null;

		LocaleContextHolder.setLocale(new Locale("es"));

		try {
			resultBigDecimal1 = this.serializeDo(TestJsonNumberSerializer.bigDecimal1);
		} catch (IOException e) {
			fail("IOException realizando la serialización del BigDecimal quitando ceros a la derecha en castellano");
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal quitando ceros a la derecha en castellano",
					StringUtils.isNotEmpty(resultBigDecimal1));
			assertEquals("La serialización del BigDecimal quitando ceros a la derecha en castellano no es correcta",
					TestJsonNumberSerializer.strBigDecimalEs1, resultBigDecimal1);
		}

		try {
			resultBigDecimal2 = this.serializeDo(TestJsonNumberSerializer.bigDecimal2);
		} catch (IOException e) {
			fail("IOException realizando la serialización del BigDecimal con dedondeo arriba en castellano");
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal con dedondeo arriba en castellano",
					StringUtils.isNotEmpty(resultBigDecimal2));
			assertEquals("La serialización del BigDecimal con dedondeo arriba en castellano no es correcta",
					TestJsonNumberSerializer.strBigDecimalEs2, resultBigDecimal2);
		}

		try {
			resultBigDecimal3 = this.serializeDo(TestJsonNumberSerializer.bigDecimal3);
		} catch (IOException e) {
			fail("IOException realizando la serialización del BigDecimal con dedondeo abajo en castellano");
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal con dedondeo abajo en castellano",
					StringUtils.isNotEmpty(resultBigDecimal3));
			assertEquals("La serialización del BigDecimal con dedondeo abajo en castellano no es correcta",
					TestJsonNumberSerializer.strBigDecimalEs3, resultBigDecimal3);
		}

		LocaleContextHolder.setLocale(new Locale("eu"));

		try {
			resultBigDecimal1 = this.serializeDo(TestJsonNumberSerializer.bigDecimal1);
		} catch (IOException e) {
			fail("IOException realizando la serialización del BigDecimal quitando ceros a la derecha en euskera");
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal quitando ceros a la derecha en euskera",
					StringUtils.isNotEmpty(resultBigDecimal1));
			assertEquals("La serialización del BigDecimal quitando ceros a la derecha en euskera no es correcta",
					TestJsonNumberSerializer.strBigDecimalEu1, resultBigDecimal1);
		}

		try {
			resultBigDecimal2 = this.serializeDo(TestJsonNumberSerializer.bigDecimal2);
		} catch (IOException e) {
			fail("IOException realizando la serialización del BigDecimal con dedondeo arriba en euskera");
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal con dedondeo arriba en euskera",
					StringUtils.isNotEmpty(resultBigDecimal2));
			assertEquals("La serialización del BigDecimal con dedondeo arriba en euskera no es correcta",
					TestJsonNumberSerializer.strBigDecimalEu2, resultBigDecimal2);
		}

		try {
			resultBigDecimal3 = this.serializeDo(TestJsonNumberSerializer.bigDecimal3);
		} catch (IOException e) {
			fail("IOException realizando la serialización del BigDecimal con dedondeo abajo en euskera");
		} finally {
			assertTrue("No se ha realizado la serialización del BigDecimal con dedondeo abajo en euskera",
					StringUtils.isNotEmpty(resultBigDecimal3));
			assertEquals("La serialización del BigDecimal con dedondeo abajo en euskera no es correcta",
					TestJsonNumberSerializer.strBigDecimalEu3, resultBigDecimal3);
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
			new JsonNumberSerializer().serialize(bigDecimal, jsonGenerator, serializerProvider);
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
