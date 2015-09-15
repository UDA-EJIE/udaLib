package com.ejie.x38.serialization;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.springframework.context.i18n.LocaleContextHolder;

public class JsonNumberDeserializer extends JsonDeserializer<BigDecimal>{

	@Override
	public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext ctx)
			throws IOException, JsonProcessingException {
		try {
			NumberFormat numberFormatter = NumberFormat.getInstance(LocaleContextHolder.getLocale());
			String numberText = jsonParser.getText();
			
			if (numberText == null || "".equals(numberText)){
				return null;
			}
			
			return new BigDecimal(numberFormatter.parse(numberText).doubleValue());
		} catch (ParseException e) {
			throw new JsonParseException(null, null, e);
		}
	}
}
