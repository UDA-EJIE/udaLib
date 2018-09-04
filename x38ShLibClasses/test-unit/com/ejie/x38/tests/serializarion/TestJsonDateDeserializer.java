/**
 * 
 */
package com.ejie.x38.tests.serializarion;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.serialization.JsonDateDeserializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/**
 * @author llaparra
 *
 */
public class TestJsonDateDeserializer {

	private static Date date;
	private static String strDate;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		strDate = "02/06/1995";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		date = sdf.parse(strDate);
	}

	/**
	 * Test method for {@link com.ejie.x38.serialization.JsonDateSerializer#serialize(java.util.Date, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 * @throws IOException 
	 */
	@Test
	public final void testSerialize() throws IOException {
		Date resDate;
		JsonFactory factory = new JsonFactory();
		
		resDate = dateDeserialize(strDate, factory);
		
		assertTrue("Debe devolver la fecha", resDate.equals(date));
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
			e.printStackTrace();
		} finally {
			if (jsonParser != null) {
				jsonParser.close();
			}
		}
		return ret;
	}

}
