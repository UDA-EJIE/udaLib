/**
 * 
 */
package com.ejie.x38.tests.serializarion;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import com.ejie.x38.serialization.JsonDateTimeDeserializer;
import com.ejie.x38.util.DateTimeManager;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/**
 * @author llaparra
 *
 */
public class TestJsonDateTimeDeserializer {

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
	public final void testDeserialize() throws IOException {
		JsonFactory factory = new JsonFactory();

		try {
			LocaleContextHolder.setLocale(localeEs);
			Date deserializedEs = deserializeDateTime(strDateTimeEs, factory);
			assertNotNull("El resultado no debe ser nulo", deserializedEs);

			if (deserializedEs != null) {
				assertTrue("Debe devolver levolver la fecha con la hora en español", deserializedEs.equals(dateTimeEs));
			}

			LocaleContextHolder.setLocale(localeEu);
			Date deserializedEu = deserializeDateTime(strDateTimeEu, factory);
			assertNotNull("El resultado no debe ser nulo", deserializedEu);

			if (deserializedEu != null) {
				assertTrue("Debe devolver levolver la fecha con la hora en euskera", deserializedEu.equals(dateTimeEu));
			}
		} catch (IOException e) {
			fail("IOException al deserializar el DateTime");
		}
	}

	private Date deserializeDateTime(String datetime, JsonFactory factory) throws IOException {
		Date ret = null;
		JsonParser jsonParser = null;
		try {
			jsonParser = factory.createParser("\"" + datetime + "\"");
			jsonParser.nextToken();
			ret = new JsonDateTimeDeserializer().deserialize(jsonParser, null);
			jsonParser.close();
		} catch (Exception e) {
			fail("Exception deserializando la fecha");
		} finally {
			if (jsonParser != null) {
				jsonParser.close();
			}
		}
		return ret;
	}
}
