package com.ejie.x38.serialization;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.context.i18n.LocaleContextHolder;

import com.ejie.x38.util.ObjectConversionManager;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;



public class JsonBigDecimalSerializer extends JsonSerializer<BigDecimal> {

	/**
	 *
	 * @param number
	 *            BigDecimal
	 * @param jsonGenerator
	 *            JsonGenerator
	 * @param paramSerializerProvider
	 *            SerializerProvider
	 * @throws IOException
	 *             e
	 * @throws JsonProcessingException
	 *             e
	 *
	 */
	@Override
	public void serialize(BigDecimal number, JsonGenerator jsonGenerator, SerializerProvider paramSerializerProvider)
			throws IOException, JsonProcessingException {
		if(LocaleContextHolder.getLocale().getLanguage() == "eu") {
			String numberStr = ObjectConversionManager.bigDecimalToString(number, number.stripTrailingZeros().scale(), LocaleContextHolder.getLocale());
			numberStr = numberStr.replace(",", "&PUNTO&").replace(".",",").replace("&PUNTO&",".");
			jsonGenerator.writeString(numberStr);			
		} else {
			jsonGenerator.writeString(ObjectConversionManager.bigDecimalToString(number, number.stripTrailingZeros().scale(), LocaleContextHolder.getLocale()));
		}
	}
}
