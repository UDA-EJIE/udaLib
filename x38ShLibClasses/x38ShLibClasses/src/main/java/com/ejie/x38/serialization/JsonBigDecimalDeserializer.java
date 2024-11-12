package com.ejie.x38.serialization;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.context.i18n.LocaleContextHolder;

import com.ejie.x38.util.ObjectConversionManager;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonBigDecimalDeserializer extends JsonDeserializer<BigDecimal>{

	@Override
	public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext ctx)
			throws IOException, JsonProcessingException {
				
			
			return ObjectConversionManager.stringToBigDecimal(jsonParser.getText(), LocaleContextHolder.getLocale());
		
	}
	
	
	
}
