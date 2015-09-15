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
package com.ejie.x38.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;


/**
 *
 * Filter responsible for exclude logs messages in "Exception loggers" and in "Normal loggers"
 * 
 * @author UDA
 *  
 */
public class UdaLogFilter extends Filter<ILoggingEvent> {

	private boolean logIncidences = false;
	
	public void setLogIncidences(boolean logIncidences) {
	    this.logIncidences = logIncidences;
	}
	
	@Override
	public FilterReply decide(ILoggingEvent event) {
		
		//The log entry is accepted if has not error level or warning level and is not an incidences logger
		if (!logIncidences && (!(Level.ERROR.levelInt == event.getLevel().levelInt || Level.WARN.levelInt == event.getLevel().levelInt))){
			return FilterReply.ACCEPT;
		//Will also accept if has error level or warning level and is an incidences logger
		} else if (logIncidences && (Level.ERROR.levelInt == event.getLevel().levelInt || Level.WARN.levelInt == event.getLevel().levelInt)){
			return FilterReply.ACCEPT;
		//In other cases will be deny
		} else {
			return FilterReply.DENY;
		}
	}
}
