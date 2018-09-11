/**
 * 
 */
package com.ejie.x38.tests.serializarion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import com.ejie.x38.serialization.JsonDateDeserializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/**
 * @author llaparra
 *
 */
public class TestJsonDateDeserializer {

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
		Date resDate;
		JsonFactory factory = new JsonFactory();

		LocaleContextHolder.setLocale(new Locale("es"));
		resDate = dateDeserialize(strDateEs, factory);
		assertNotNull("El resultado de la deserialización en español no debe ser nulo", resDate);
		if (resDate != null) {
			assertEquals("Debe devolver la fecha en español", date, resDate);
		}

		LocaleContextHolder.setLocale(new Locale("eu"));
		resDate = dateDeserialize(strDateEu, factory);
		assertNotNull("El resultado de la deserialización en euskera no debe ser nulo", resDate);
		if (resDate != null) {
			assertEquals("Debe devolver la fecha en euskera", date, resDate);
		}
	}

	private Date dateDeserialize(String strDate2, JsonFactory factory) throws IOException {
		Date ret = null;
		JsonParser jsonParser = null;
		try {
			jsonParser = factory.createParser("\"" + strDate2 + "\"");
			jsonParser.nextToken();
			ret = new JsonDateDeserializer().deserialize(jsonParser, null);
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
