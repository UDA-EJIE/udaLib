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

import java.io.File;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * 
 * Class responsible for setting logback. Perform the following actions:
 * 1- Is responsible for collecting the settings for logback and applied to the system's log
 *  
 * @author UDA
 * 
 */
public abstract class LogbackConfigurer
{
	public static final String CLASSPATH_URL_PREFIX = "classpath:";
	public static final String XML_FILE_EXTENSION = ".xml";
	final static Logger logger = LoggerFactory.getLogger(LogbackConfigurer.class);

	public static void initLogging(String location, boolean printState) throws FileNotFoundException {
    
		logger.info("Begins the initialization of system logs");
		String resolvedLocation = null;
		File file = null;
		StringBuilder compositeLocator = new StringBuilder(CLASSPATH_URL_PREFIX);
		compositeLocator.append(location);
		
		//Validates that the URL is an XML file
		if(location.contains(XML_FILE_EXTENSION)){
			
			//Is collected, as may be absolute or relative to classpath, and validates the correctness of the URL of the configuration file.
			try {
				resolvedLocation = SystemPropertyUtils.resolvePlaceholders(compositeLocator.toString());
								
				file = ResourceUtils.getFile(resolvedLocation);
				
				logger.info("Read file => "+file.toString());
				
			} catch (FileNotFoundException fileNotFoundException) {	
					try{
						resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
												
						file = ResourceUtils.getFile(resolvedLocation);
						
						logger.info("Read file => "+file.toString());
						
					} catch (FileNotFoundException fileNotFoundExceptionAbsolut) {
						logger.error("There has been an error of incorrect path. The route is not included in the application classpath or an absolute path is correct.", fileNotFoundExceptionAbsolut);
					}
			}	    
		    
			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		    
		    try {
		      JoranConfigurator configurator = new JoranConfigurator();
		      configurator.setContext(lc);
		      
		      //The context was probably already configured by default configuration rules
		      lc.reset(); 
		      
		      //Applies the new configuration
		      configurator.doConfigure(file);
		      
		    } catch (JoranException je) {		    	
		    	FileNotFoundException fnfe = new FileNotFoundException("There was an error initializing the system logs (logback). The specified file is incorrect or corrupt. "+ je.getMessage()); 
		    	throw(fnfe);
		    }
		    
		    if (printState){
		    	lc.getStatusManager();
		    }
		    
		} else {
			//Not being a path to an xml file an exception is raised and does not load  
			FileNotFoundException exc = new FileNotFoundException("The file specified for logback settings should be an xml file.");
			logger.error("There has been an error of incorrect path. The route is not included in the application classpath or an absolute path is correct.", exc);
			throw exc;
		}
		
		file = null;
		
		logger.info("Ends the initialization of system logs");
  }
	
}
