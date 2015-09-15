package com.ejie.x38;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ejie.x38.util.StackTraceManager;

/**
 * 
 * @author UDA
 *
 * Listener de UDA que se encarga de lo siguiente:
 * 1- Inicializa Log4j cuando arranca la aplicacion
 * 2- Gestiona el Timestamp que se vincula a las sesiones para gestionar el refresco de XLNetS 
 */
public class UdaListener implements ServletContextListener, HttpSessionListener {

	Logger logger = Logger.getLogger(UdaListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.log(Level.INFO, "WAR Context is being destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext ctx = servletContextEvent.getServletContext();
		Properties props = new Properties();
		String webAppName = ctx.getInitParameter("webAppName");
		
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(webAppName+"/log4j.properties");
			props.load(in);
			PropertyConfigurator.configure(props);
		} catch (Exception e) {
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent sessionEvent) {
		logger.log(Level.INFO, "Session "+sessionEvent.getSession().getId()+" has been created");
		sessionEvent.getSession().setAttribute("udaTimeStamp", System.currentTimeMillis());
		sessionEvent.getSession().setAttribute("udaVirgin", Boolean.TRUE);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		logger.log(Level.INFO, "Session "+sessionEvent.getSession().getId()+" has been destroyed");
		sessionEvent.getSession().removeAttribute("udaTimeStamp");
		sessionEvent.getSession().removeAttribute("udaVirgin");
	}
}