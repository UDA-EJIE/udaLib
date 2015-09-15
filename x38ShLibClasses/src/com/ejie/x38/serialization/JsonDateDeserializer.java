/*
* Copyright 2011 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.serialization;

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
 * 
 * Used to serialize Java.util.Date, which is not a common JSON type, so we have
 * to create a custom serialize method;.
 * 
 * @author UDA
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
			
			String dateText = par.getText();
			
			if (dateText == null || "".equals(dateText)){
				return null;
			}
			
			Date date = format.parse(dateText);
			return new Timestamp(date.getTime());
		} catch (ParseException e) {
			throw new JsonParseException(null, null, e);
		}
	}
}