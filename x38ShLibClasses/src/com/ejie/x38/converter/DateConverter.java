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
package com.ejie.x38.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;

import com.ejie.x38.util.DateTimeManager;

/**
 * Conversor de fechas dependiendo del idioma.
 * 
 * @author UDA
 * 
 */
public class DateConverter implements Converter<String, Date> {

	@Override
	public Date convert(String strFecha) {
		try {
			Locale locale = LocaleContextHolder.getLocale();
			SimpleDateFormat format = DateTimeManager.getDateTimeFormat(locale);
			return format.parse(strFecha);
		} catch (ParseException ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException();
		}

	}
}

