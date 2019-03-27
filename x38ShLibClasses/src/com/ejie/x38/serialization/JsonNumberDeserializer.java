package com.ejie.x38.serialization;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonNumberDeserializer extends JsonDeserializer<BigDecimal> {

	@Override
	public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext ctx)
	        throws IOException, JsonProcessingException {
		try {
			NumberFormat numberFormatter = NumberFormat.getInstance(LocaleContextHolder.getLocale());
			String numberText = jsonParser.getText();

			if (numberText == null || "".equals(numberText)) {
				return null;
			}

			return new BigDecimal(numberFormatter.parse(numberText).doubleValue());
		} catch (ParseException e) {
			throw new JsonParseException(jsonParser, "JsonNumberDeserializer.deserialize", e);
		}
	}
}
