/**
 * 
 */
package com.ejie.x38.tests.serializarion;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.serialization.JsonDateTimeSerializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author llaparra
 *
 */
public class TestJsonDateTimeDeserializer {

	private static Timestamp dateTime;
	private static String strDateTime;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		Date date;
		String strDate;
		strDate = "02/06/1995 00:00:00";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		try {
			date = sdf.parse(strDate);
			dateTime = new Timestamp(date.getTime());
		} catch (ParseException e) {
			fail("ParseException inicializando el caso de prueba");
		}
		strDateTime = "02/06/1995 00:00:00";
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.JsonDateSerializer#serialize(java.util.Date, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testSerialize() throws IOException {
		String parsed = dateTimeSerialize();
		String expected = "\"" + strDateTime + "\"";
		assertTrue("Debe devolver la fecha (entrecomillada) en string", parsed.equals(expected));
	}

	private String dateTimeSerialize() throws IOException {
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
