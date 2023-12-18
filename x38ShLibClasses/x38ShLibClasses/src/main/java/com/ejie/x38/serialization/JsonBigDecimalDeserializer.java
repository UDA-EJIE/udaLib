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
				
			String formatted = jsonParser.getText();
			
			if(LocaleContextHolder.getLocale().getLanguage() == "eu") {
				formatted = formatted.replace(",", "&PUNTO&").replace(".", ",").replace("&PUNTO&", ".");
			}
			
			return ObjectConversionManager.stringToBigDecimal(formatted, LocaleContextHolder.getLocale());
		
	}
	
	
	
}
