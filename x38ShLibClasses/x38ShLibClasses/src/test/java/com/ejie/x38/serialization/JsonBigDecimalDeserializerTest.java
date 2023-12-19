package com.ejie.x38.serialization;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonBigDecimalDeserializerTest {

	private ObjectMapper mapper;
    	
	
    @Before
    public void setup() {
        mapper = new ObjectMapper();
    }

    
	@Test
	public void testDeserialize() throws JsonProcessingException, IOException {

		// Creación de datos de prueba
		String valueBigDecimal1 = "123,1234567890123456789";
		String valueBigDecimal2 = "123,9876543210987654321";

		// Declaración del resultado esperado
		BigDecimal expectedBigDecimal1 = new BigDecimal("123.1234567890123456789");
		BigDecimal expectedBigDecimal2 = new BigDecimal("123.9876543210987654321");

		// Ejecución de los métodos de prueba
		BigDecimal resultBigDecimal1 = this.deserialiseBigDecimal(valueBigDecimal1);
		BigDecimal resultBigDecimal2 = this.deserialiseBigDecimal(valueBigDecimal2);

		// Verificación del resultado
		assertTrue(resultBigDecimal1.equals(expectedBigDecimal1));
		assertTrue(resultBigDecimal2.equals(expectedBigDecimal2));

	}

	
    private BigDecimal deserialiseBigDecimal(String value) throws JsonParseException, IOException {
    	BigDecimalTestObject testObject = mapper.readValue("{\"prop\":\"" + value + "\"}", BigDecimalTestObject.class);
		
		return testObject.getProp();
    }
    
	
}
