package com.ejie.x38.log;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UDA
 * 
 * EclipseLink Logger bridge to SLF4J.
 * 
 * Posibilita que EclipseLink se cominique con Log4J, ya que sino, solo funciona con commons-logging.
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