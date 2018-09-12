/**
 * 
 */
package com.ejie.x38.test.junit.unit.serializarion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import com.ejie.x38.serialization.JsonNumberDeserializer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/**
 * @author llaparra
 *
 */
public class TestJsonNumberDeserializer {

	private static final String EL_RESULTADO_NO_DEBE_SER_NULO = "El resultado no debe ser nulo";
	private static final String EXCEPTION_AL_DESERIALIZAR_EL_BIG_DECIMAL = "Exception al deserializar el BigDecimal";
	private static BigDecimal number;
	private static String strNumberEs;
	private static String strNumberEs1;
	private static String strNumberEu;
	private static String strNumberEu1;
	private static BigDecimal number2;
	private static String strNumberEs2;
	private static String strNumberEu2;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		number = BigDecimal.valueOf(9123000);
		number2 = BigDecimal.valueOf(9123000.56465465465412);

		strNumberEs = "9.123.000";
		strNumberEs1 = "9.123.000,00000000000000";
		strNumberEs2 = "9.123.000,56465465465412";

		strNumberEu = "9.123.000";
		strNumberEu1 = "9.123.000,00000000000000";
		strNumberEu2 = "9.123.000,56465465465412";
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

		LocaleContextHolder.setLocale(new Locale("es"));

		try {
			resNumber = deserializeNumber(strNumberEs, factory);

			assertNotNull(EL_RESULTADO_NO_DEBE_SER_NULO, resNumber);

			if (resNumber != null) {
				assertEquals("Debe devolver el BigDecimal parseado del castellano", number, resNumber);
			}
		} catch (IOException e) {
			fail(EXCEPTION_AL_DESERIALIZAR_EL_BIG_DECIMAL);
		}

		try {
			resNumber = deserializeNumber(strNumberEs1, factory);

			assertNotNull(EL_RESULTADO_NO_DEBE_SER_NULO, resNumber);

			if (resNumber != null) {
				assertEquals("Debe devolver el BigDecimal parseado del castellano sin parte decimal con ceros", number,
						resNumber);
			}
		} catch (IOException e) {
			fail(EXCEPTION_AL_DESERIALIZAR_EL_BIG_DECIMAL);
		}

		try {
			resNumber = deserializeNumber(strNumberEs2, factory);

			assertNotNull(EL_RESULTADO_NO_DEBE_SER_NULO, resNumber);

			if (resNumber != null) {
				assertEquals("Debe devolver el BigDecimal parseado del castellano con la parte decimal", number2,
						resNumber.round(MathContext.DECIMAL64));
			}
		} catch (IOException e) {
			fail(EXCEPTION_AL_DESERIALIZAR_EL_BIG_DECIMAL);
		}

		LocaleContextHolder.setLocale(new Locale("eu"));

		try {
			resNumber = deserializeNumber(strNumberEu, factory);

			assertNotNull(EL_RESULTADO_NO_DEBE_SER_NULO, resNumber);

			if (resNumber != null) {
				assertEquals("Debe devolver el BigDecimal parseado del euskera", number, resNumber);
			}
		} catch (IOException e) {
			fail(EXCEPTION_AL_DESERIALIZAR_EL_BIG_DECIMAL);
		}

		try {
			resNumber = deserializeNumber(strNumberEu1, factory);

			assertNotNull(EL_RESULTADO_NO_DEBE_SER_NULO, resNumber);

			if (resNumber != null) {
				assertEquals("Debe devolver el BigDecimal parseado del euskera sin parte decimal con ceros", number,
						resNumber);
			}
		} catch (IOException e) {
			fail(EXCEPTION_AL_DESERIALIZAR_EL_BIG_DECIMAL);
		}

		try {
			resNumber = deserializeNumber(strNumberEu2, factory);

			assertNotNull(EL_RESULTADO_NO_DEBE_SER_NULO, resNumber);

			if (resNumber != null) {
				assertEquals("Debe devolver el BigDecimal parseado del euskera con la parte decimal", number2,
						resNumber.round(MathContext.DECIMAL64));
			}
		} catch (IOException e) {
			fail(EXCEPTION_AL_DESERIALIZAR_EL_BIG_DECIMAL);
		}
	}

	private BigDecimal deserializeNumber(String strNum, JsonFactory factory) throws IOException {
		BigDecimal ret = null;
		JsonParser jsonParser = null;
		try {
			jsonParser = factory.createParser("\"" + strNum + "\"");
			jsonParser.nextToken();
			ret = new JsonNumberDeserializer().deserialize(jsonParser, null);
			jsonParser.close();
		} catch (Exception e) {
			fail(EXCEPTION_AL_DESERIALIZAR_EL_BIG_DECIMAL);
		} finally {
			if (jsonParser != null) {
				jsonParser.close();
			}
		}
		return ret;
	}

}
