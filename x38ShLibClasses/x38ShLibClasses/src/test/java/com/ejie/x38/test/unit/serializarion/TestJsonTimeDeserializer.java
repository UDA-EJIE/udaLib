package com.ejie.x38.test.unit.serializarion;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.serialization.JsonTimeDeserializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/**
 * @author Eurohelp S.L.
 */
public class TestJsonTimeDeserializer {

	private static Timestamp timestamp;
	private static String strTimestamp;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws ParseException {
		String strTime = "12:30:00";
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date date = sdf.parse(strTime);
		timestamp = new Timestamp(date.getTime());
		strTimestamp = strTime;
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.JsonTimeDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testDeserializeJsonParserDeserializationContext() throws IOException {
		Timestamp resTime = deserializeTime(strTimestamp, new JsonFactory());
		assertTrue("Debe devolver el Timestamp correcto", timestamp.equals(resTime));
	}

	private Timestamp deserializeTime(String strTime, JsonFactory factory) throws IOException {
		Timestamp ret = null;
		JsonParser jsonParser = null;
		try {
			jsonParser = factory.createParser("\"" + strTime + "\"");
			jsonParser.nextToken();
			ret = new JsonTimeDeserializer().deserialize(jsonParser, null);
			jsonParser.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jsonParser != null) {
				jsonParser.close();
			}
		}
		return ret;
	}

}
