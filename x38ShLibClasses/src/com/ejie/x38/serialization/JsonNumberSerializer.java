package com.ejie.x38.serialization;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.springframework.context.i18n.LocaleContextHolder;


public class JsonNumberSerializer extends JsonSerializer<BigDecimal> {

	@Override
	public void serialize(BigDecimal number, JsonGenerator jsonGenerator,
			SerializerProvider paramSerializerProvider) throws IOException,
			JsonProcessingException {
		
		NumberFormat numberFormatter = NumberFormat.getInstance(LocaleContextHolder.getLocale());
		jsonGenerator.writeString(numberFormatter.format(number));
	}
}
