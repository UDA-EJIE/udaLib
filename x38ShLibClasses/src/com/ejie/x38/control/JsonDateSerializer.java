
package com.ejie.x38.control;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.ejie.x38.util.DateTimeManager;

/**
 * Used to serialize Java.util.Date, which is not a common JSON
 * type, so we have to create a custom serialize method;.
 *
 */
@Component
public class JsonDateSerializer extends JsonSerializer<Date>{

	@Override
	public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		Locale locale = LocaleContextHolder.getLocale();

		SimpleDateFormat dateFormat = DateTimeManager.getDateTimeFormat(locale);
		
		String formattedDate = dateFormat.format(date);

		gen.writeString(formattedDate);
	}
}