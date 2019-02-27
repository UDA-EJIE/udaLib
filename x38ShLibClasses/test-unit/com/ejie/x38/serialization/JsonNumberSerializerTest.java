package com.ejie.x38.serialization;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonNumberSerializerTest {

	
	@Test
	public void testSerialize() throws JsonProcessingException, IOException{
				
		// Creación de datos de prueba
		BigDecimal bigDecimal1 = new BigDecimal("123.1234567890123456789");
		BigDecimal bigDecimal2 = new BigDecimal("123.9876543210987654321");
		
		// Declaración del resultado esperado
		String expectedBigDecimal1 = "\"123,123\"";
		String expectedBigDecimal2 = "\"123,988\"";
		
		// Ejecución de los métodos de prueba
		String resultBigDecimal1 = this.serializeBigDecimal(bigDecimal1);
		String resultBigDecimal2 = this.serializeBigDecimal(bigDecimal2);
			
		// Verificación del resultado
	    assertTrue(resultBigDecimal1.equals(expectedBigDecimal1));
	    assertTrue(resultBigDecimal2.equals(expectedBigDecimal2));
   	}
	
	
	private String serializeBigDecimal(BigDecimal bigDecimal) throws IOException{
		Writer jsonWriter = new StringWriter();
		SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
		JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
		new JsonNumberSerializer().serialize(bigDecimal, jsonGenerator, serializerProvider);
	    jsonGenerator.flush();
	    
	    return jsonWriter.toString();
	    
	}
}
