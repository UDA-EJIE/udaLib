/*
* Copyright 2011 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, VersiÃ³n 1.1 exclusivamente (la Â«LicenciaÂ»);
* Solo podrÃ¡ usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislaciÃ³n aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye Â«TAL CUALÂ»,
* SIN GARANTÃ�AS NI CONDICIONES DE NINGÃšN TIPO, ni expresas ni implÃ­citas.
* VÃ©ase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.serialization;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.ejie.x38.util.DateTimeManager;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * 
 * Used to serialize Java.util.Date, which is not a common JSON type, so we have
 * to create a custom serialize method;.
 * 
 * @author UDA
 * 
 */
@Component
public class JsonDateDeserializer extends JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonParser par, DeserializationContext ctx)
			throws IOException, JsonProcessingException {
		try {
			Locale locale = LocaleContextHolder.getLocale();
			SimpleDateFormat format = DateTimeManager.getDateTimeFormat(locale);
			
			String dateText = par.getText();
			
			if (dateText == null || "".equals(dateText)){
				return null;
			}
			
			return format.parse(dateText);
		} catch (ParseException e) {
			throw new JsonParseException(null, null, e);
		}
	}
}