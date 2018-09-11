/**
 * 
 */
package com.ejie.x38.tests.serializarion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import com.ejie.x38.serialization.JsonDateSerializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author llaparra
 *
 */
public class TestJsonDateSerializer {

	private static Date date;
	private static String strDateEs;
	private static String strDateEu;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		strDateEs = "02/06/1995";
		strDateEu = "1995/06/02";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			date = sdf.parse(strDateEs);
		} catch (ParseException e) {
			fail("ParseException inicializando el caso de prueba");
		}
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.JsonDateSerializer#serialize(java.util.Date, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testSerialize() throws IOException {
		LocaleContextHolder.setLocale(new Locale("es"));
		String parsedEs = dateSerialize();
		String expectedEs = "\"" + strDateEs + "\"";
		assertEquals("Debe devolver la fecha (entrecomillada) en español en string", expectedEs, parsedEs);

		LocaleContextHolder.setLocale(new Locale("eu"));
		String parsedEu = dateSerialize();
		String expectedEu = "\"" + strDateEu + "\"";
		assertEquals("Debe devolver la fecha (entrecomillada) en euskera en string", expectedEu, parsedEu);
	}

	private String dateSerialize() throws IOException {
		String ret = "";
		Writer jsonWriter = new StringWriter();
		SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
		JsonGenerator jsonGenerator = null;

		try {
			jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
			new JsonDateSerializer().serialize(date, jsonGenerator, serializerProvider);
		} catch (Exception e) {
			fail("Exception serializando la fecha");
		} finally {
			if (jsonGenerator != null) {
				jsonGenerator.close();
				jsonGenerator.flush();

				ret = String.valueOf(jsonWriter);

				jsonWriter.close();
				jsonWriter.flush();
			}
		}

		return ret;
	}

}
