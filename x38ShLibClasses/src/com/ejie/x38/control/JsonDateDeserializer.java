package com.ejie.x38.control;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.ejie.x38.util.DateTimeManager;

/**
 * Used to serialize Java.util.Date, which is not a common JSON type, so we have
 * to create a custom serialize method;.
 * 
 */
@Component
public class JsonDateDeserializer extends JsonDeserializer<Timestamp> {

	@Override
	public Timestamp deserialize(JsonParser par, DeserializationContext ctx)
			throws IOException, JsonProcessingException {
		try {
			Locale locale = LocaleContextHolder.getLocale();
			SimpleDateFormat format = DateTimeManager.getDateTimeFormat(locale);
			Date date = format.parse(par.getText());
			return new Timestamp(date.getTime());
		} catch (ParseException e) {
			throw new JsonParseException(null, null, e);
		}
	}
}