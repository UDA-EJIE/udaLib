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

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * EclipseLink Logger bridge to SLF4J.
 * 
 * Posibilita que EclipseLink se cominique con LogBack, ya que sino, solo funciona con commons-logging.
 * 
 * @author UDA
 */
public class SLF4JLogger extends AbstractSessionLog implements SessionLog {
	
	private static final String CATEGORY_DEFAULT = "org.eclipse.persistence";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void log(final SessionLogEntry logEntry) {
		if (logEntry != null) {
			final String logCategory = logEntry.getNameSpace() != null ?
					(CATEGORY_DEFAULT + "-" + logEntry.getNameSpace()) : CATEGORY_DEFAULT;
			final Logger logger = LoggerFactory.getLogger(logCategory);
			
			switch (logEntry.getLevel()) {
			case ALL:
			case FINEST:
				if (logger.isTraceEnabled()) {
					logger.trace("{}", formatMessage(logEntry));
				}
				break;
			case FINER:
			case FINE:
				if (logger.isDebugEnabled()) {
					logger.debug("{}", formatMessage(logEntry));
				}
				break;
			case CONFIG:
			case INFO:
				if (logger.isInfoEnabled()) {
					logger.info("{}", formatMessage(logEntry));
				}
				break;
			case WARNING:
				if (logger.isWarnEnabled()) {
					logger.warn("{}", formatMessage(logEntry));
				}
				break;
			case SEVERE:
				if (logger.isErrorEnabled()) {
					logger.error("{}", formatMessage(logEntry));
				}
				break;
			case OFF:
			default:
				break;
			}
		}
	}
}