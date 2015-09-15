package com.ejie.x38.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ejie.x38.log.LogConstants;

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
}