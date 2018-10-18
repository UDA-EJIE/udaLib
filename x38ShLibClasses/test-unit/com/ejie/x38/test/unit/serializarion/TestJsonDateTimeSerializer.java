package com.ejie.x38.test.unit.serializarion;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import com.ejie.x38.serialization.JsonDateTimeSerializer;
import com.ejie.x38.util.DateTimeManager;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author Eurohelp S.L.
 */
public class TestJsonDateTimeSerializer {

	private static Timestamp dateTimeEs;
	private static String strDateTimeEs;
	private static Locale localeEs = new Locale("es");

	private static Timestamp dateTimeEu;
	private static String strDateTimeEu;
	private static Locale localeEu = new Locale("eu");

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		strDateTimeEs = "02/06/1995 13:45:11";
		SimpleDateFormat sdfEs = DateTimeManager.getTimestampFormat(localeEs);
		try {
			dateTimeEs = new Timestamp(sdfEs.parse(strDateTimeEs).getTime());
		} catch (ParseException e) {
			fail("ParseException inicializando el caso de prueba");
		}

		strDateTimeEu = "1995/06/02 13:45:11";
		SimpleDateFormat sdfEu = DateTimeManager.getTimestampFormat(localeEu);
		try {
			dateTimeEu = new Timestamp(sdfEu.parse(strDateTimeEu).getTime());
		} catch (ParseException e) {
			fail("ParseException inicializando el caso de prueba");
		}
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.JsonDateTimeSerializer#serialize(java.util.Date, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testSerialize() throws IOException {
		LocaleContextHolder.setLocale(localeEs);
		String parsedEs = dateTimeSerialize(dateTimeEs);
		String expectedEs = "\"" + strDateTimeEs + "\"";
		assertTrue("Debe devolver la fecha (entrecomillada) en string", parsedEs.equals(expectedEs));

		LocaleContextHolder.setLocale(localeEu);
		String parsedEu = dateTimeSerialize(dateTimeEu);
		String expectedEu = "\"" + strDateTimeEu + "\"";
		assertTrue("Debe devolver la fecha (entrecomillada) en string", parsedEu.equals(expectedEu));
	}

	private String dateTimeSerialize(Timestamp dateTime) throws IOException {
		String ret = "";
		Writer jsonWriter = new StringWriter();
		SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
		JsonGenerator jsonGenerator = null;

		try {
			jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
			new JsonDateTimeSerializer().serialize(dateTime, jsonGenerator, serializerProvider);
		} catch (Exception e) {
			fail("Exception deserializando la fecha");
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
