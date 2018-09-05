/**
 * 
 */
package com.ejie.x38.tests.serializarion;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.serialization.JsonBigDecimalDeserializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/**
 * @author llaparra
 *
 */
public class TestJsonNumberDeserializer {

	private static BigDecimal number;
	private static String strNumber;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		number = BigDecimal.valueOf(42);
		strNumber = "42";
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.JsonNumberDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testDeserializeJsonParserDeserializationContext() {
		JsonFactory factory = new JsonFactory();
		BigDecimal resNumber = null;

		try {
			resNumber = deserializeNumber(strNumber, factory);

			assertNotNull("El resultado no debe ser nulo", resNumber);

			if (resNumber != null) {
				assertTrue("Debe devolver el BigDecimal", resNumber.equals(number));
			}
		} catch (IOException e) {
			fail("Exception al deserializar el BigDecimal");
		}
	}

	private BigDecimal deserializeNumber(String strNum, JsonFactory factory) throws IOException {
		BigDecimal ret = null;
		JsonParser jsonParser = null;
		try {
			jsonParser = factory.createParser("\"" + strNum + "\"");
			jsonParser.nextToken();
			ret = new JsonBigDecimalDeserializer().deserialize(jsonParser, null);
			jsonParser.close();
		} catch (Exception e) {
			fail("Exception al deserializar el BigDecimal");
		} finally {
			if (jsonParser != null) {
				jsonParser.close();
			}
		}
		return ret;
	}

}
