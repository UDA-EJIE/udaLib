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

import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

/**
 * 
 * @author UDA
 *
 */
public class WebContextParameterManager implements ApplicationContextAware {

	private static Logger logger =  LoggerFactory.getLogger("com.ejie.x38.util.WebContextParameterManager");
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@PostConstruct
	public void init(){
		Properties props = new Properties();
		logger.info("Loads the application context parameters");
		StaticsContainer.webAppName = webApplicationContext.getServletContext().getInitParameter("webAppName");
		logger.info("The applications name is: "+StaticsContainer.webAppName);
		StaticsContainer.webId = webApplicationContext.getId();
		logger.info("The applications Id is: "+StaticsContainer.webId);
		
		try{
			logger.debug("Loading properties from: "+StaticsContainer.webAppName+"/"+StaticsContainer.webAppName+".properties");
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(StaticsContainer.webAppName+"/"+StaticsContainer.webAppName+".properties");
			props.load(in);
			in.close();
		}catch(Exception e){
			logger.error(StackTraceManager.getStackTrace(e));
		}
		
		logger.info("WARs specific Static Content URL is: "+props.getProperty("statics.path"));
		StaticsContainer.staticsUrl = props.getProperty("statics.path");
//		logger.debug("WARs default layout is: "+props.getProperty("statics.layout"));
//		StaticsContainer.layout = props.getProperty("statics.layout");
//		logger.debug("WARs default language is: "+props.getProperty("statics.language"));
//		StaticsContainer.language = props.getProperty("statics.language");
		logger.info("Applications Model Package is: "+"com.ejie."+StaticsContainer.webAppName+".model.");
		StaticsContainer.modelPackageName = "com.ejie."+StaticsContainer.webAppName+".model.";
		logger.info("The URL to access the security provider of the application (\"XLNets\") is: "+props.getProperty("xlnets.path"));
		StaticsContainer.loginUrl = props.getProperty("xlnets.path");
		if(props.getProperty("xlnets.inPortal") != null && ((props.getProperty("xlnets.inPortal")).toLowerCase()).equals("true")){
			logger.info("The application "+StaticsContainer.webAppName+" is integrated in the portals of lote3");
			StaticsContainer.aplicInPortal = true;
		} else {
			logger.info("The application "+StaticsContainer.webAppName+" isn't integrated in the portals of lote3");
			StaticsContainer.aplicInPortal = false;
		}
		if (StaticsContainer.loginUrl==null){
			logger.error("Login URL is not Set!");
		}
		String weblogicInstance = System.getProperty("weblogic.Name");
		logger.info("The WebLogic Instance Name is: "+weblogicInstance);
		StaticsContainer.weblogicInstance = weblogicInstance;
	}

	//Getters & Setters	
	public WebApplicationContext getWebApplicationContext() {
		return webApplicationContext;
	}

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		webApplicationContext =(WebApplicationContext) context;
	}
}