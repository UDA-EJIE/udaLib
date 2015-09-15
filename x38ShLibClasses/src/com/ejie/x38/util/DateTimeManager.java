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
package com.ejie.x38.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.ejie.x38.log.LogConstants;

/**
 * 
 * @author UDA
 *
 */
public class DateTimeManager {
	
	public static SimpleDateFormat getDateTimeFormat(Locale locale){
		if(locale.getLanguage().equals(Constants.EUSKARA)){
			return Constants.YYYYMMDD_DATE_FORMAT;
		}else if(locale.getLanguage().equals(Constants.FRANCAIS) || locale.getLanguage().equals(Constants.CASTELLANO) || locale.getLanguage().equals(Constants.ENGLISH)){
			return Constants.DDMMYYYY_DATE_FORMAT;
		}else{
			return Constants.DDMMYYYY_DATE_FORMAT;
		}
	}
	
	public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat(LogConstants.DATETIMEFORMAT);
        Date date = new Date();
        return dateFormat.format(date);
    }
	
	
	public static String getHttpExpiredDate(){
		final Calendar calendar = Calendar.getInstance();
		//calendar.set(2010,8,25);
		calendar.add(Calendar.DAY_OF_YEAR, 3);

		final DateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		return httpDateFormat.format(calendar.getTime());
	}
	
	public static String getHttpExpiredDate(Date date){

		final DateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		return httpDateFormat.format(date);
	}
}