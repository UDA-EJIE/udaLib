package com.ejie.x38.util;

import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

public class WebContextParameterManager implements ApplicationContextAware {

	private static Logger logger = Logger.getLogger("com.ejie.x38.util.WebContextParameterManager");
	
	private WebApplicationContext webApplicationContext;
	
	@PostConstruct
	public void init(){
		Properties props = new Properties();
		StaticsContainer.webAppName = webApplicationContext.getServletContext().getInitParameter("webAppName");
		logger.log(Level.DEBUG, "Applications name is: "+StaticsContainer.webAppName);
		StaticsContainer.webId = webApplicationContext.getId();
		logger.log(Level.DEBUG, "Applications Id is: "+StaticsContainer.webId);
		try{
			logger.log(Level.DEBUG, "Loading properties from: "+StaticsContainer.webAppName+"/"+StaticsContainer.webAppName+".properties");
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(StaticsContainer.webAppName+"/"+StaticsContainer.webAppName+".properties");
			props.load(in);
			in.close();
		}catch(Exception e){
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
		}
		logger.info("Loading static parameters");
		logger.log(Level.DEBUG, "WARs specific Static Content URL is: "+props.getProperty("statics.path"));
		StaticsContainer.staticsUrl = props.getProperty("statics.path");
//		logger.log(Level.DEBUG, "WARs default layout is: "+props.getProperty("statics.layout"));
//		StaticsContainer.layout = props.getProperty("statics.layout");
//		logger.log(Level.DEBUG, "WARs default language is: "+props.getProperty("statics.language"));
//		StaticsContainer.language = props.getProperty("statics.language");
		logger.log(Level.DEBUG, "Applications Model Package is: "+"com.ejie."+StaticsContainer.webAppName+".model.");
		StaticsContainer.modelPackageName = "com.ejie."+StaticsContainer.webAppName+".model.";
		logger.log(Level.DEBUG, "Applications Login URL is: "+props.getProperty("xlnets.path"));
		StaticsContainer.loginUrl = props.getProperty("xlnets.path");
		if (StaticsContainer.loginUrl==null){
			logger.log(Level.ERROR, "Login URL is not Set!");
		}
		String weblogicInstance = System.getProperty("weblogic.Name");
		logger.log(Level.DEBUG, "WebLogic Instance Name is: "+weblogicInstance);
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