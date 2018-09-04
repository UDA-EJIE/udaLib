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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.serialization.JsonTimeSerializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author llaparra
 *
 */
public class TestJsonTimeSerializer {

	private static Timestamp timestamp;
	private static String strTimestamp;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String strTime = "12:30:00";
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date date = sdf.parse(strTime);
		timestamp = new Timestamp(date.getTime());
		strTimestamp = strTime;       
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.JsonTimeSerializer#serialize(java.util.Date, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 * @throws IOException 
	 */
	@Test
	public final void testSerializeDateJsonGeneratorSerializerProvider() throws IOException {
		String parsed = serializeTime();
		String expected = "\"" + strTimestamp + "\"";
		assertTrue("Debe devolver la hora (entrecomillada) en string", parsed.equals(expected));
	}
	
	private String serializeTime() throws IOException {
		String ret = "";
		Writer jsonWriter = new StringWriter();
		SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
		JsonGenerator jsonGenerator = null;
		
		try {
			jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
			new JsonTimeSerializer().serialize(timestamp, jsonGenerator, serializerProvider);
		} catch (Exception e) {
			e.printStackTrace();
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
