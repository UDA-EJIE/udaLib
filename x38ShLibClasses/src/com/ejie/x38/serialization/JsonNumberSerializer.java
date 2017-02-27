package com.ejie.x38.serialization;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;


import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


public class JsonNumberSerializer extends JsonSerializer<BigDecimal> {

	@Override
	public void serialize(BigDecimal number, JsonGenerator jsonGenerator,
			SerializerProvider paramSerializerProvider) throws IOException,
			JsonProcessingException {
		
		NumberFormat numberFormatter = NumberFormat.getInstance(LocaleContextHolder.getLocale());
		jsonGenerator.writeString(numberFormatter.format(number));
	}
}
