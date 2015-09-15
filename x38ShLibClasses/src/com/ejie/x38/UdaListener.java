/*
* Copyright 2012 E.J.I.E., S.A.
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
package com.ejie.x38;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ejie.x38.log.LogConstants;
import com.ejie.x38.security.StockUdaSecurityPadlocksImpl;
import com.ejie.x38.security.UserCredentials;
import com.ejie.x38.util.ManagementUrl;

/**
 * 
 * Listener de UDA que se encarga de lo siguiente:
 * 1- Facilita la gestión de logs de las peticiones entrantes
 * 2- Gestiona el Timestamp que se vincula a las sesiones para gestionar el refresco de XLNetS
 *  
 * @author UDA
 * 
 */
public class UdaListener implements ServletContextListener, HttpSessionListener, ServletRequestListener{
	
	Logger logger =  LoggerFactory.getLogger(UdaListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//logger.debug( "WAR Context is being destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.debug("WAR Context is being initialized");
	}

	@Override
	public void sessionCreated(HttpSessionEvent sessionEvent) {
		logger.debug( "Session "+sessionEvent.getSession().getId()+" has been created");
		sessionEvent.getSession().setAttribute("udaTimeStamp", System.currentTimeMillis());
		sessionEvent.getSession().setAttribute("udaVirgin", Boolean.TRUE);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		logger.debug( "Session "+sessionEvent.getSession().getId()+" has been destroyed");
		
		HttpSession session = sessionEvent.getSession();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
        StockUdaSecurityPadlocksImpl stockUdaSecurityPadlocks = (StockUdaSecurityPadlocksImpl)ctx.getBean("stockUdaSecurityPadlocks");
		stockUdaSecurityPadlocks.deleteCredentialLoadObject(sessionEvent.getSession().getId());
		
		sessionEvent.getSession().removeAttribute("udaTimeStamp");
		sessionEvent.getSession().removeAttribute("udaVirgin");
	}
	
	@Override
	//Called when the servlet request is going of scope.
	public void requestInitialized(ServletRequestEvent sre){
		
		ServletRequest request = sre.getServletRequest();
		HttpServletRequest httpServletRequest = null;
		StringBuilder logMessage = new StringBuilder();
		HttpSession httpSession = null;
		
		SecurityContextImpl securityContext  = null;
		UserCredentials credentials = null; 
		Authentication authentication = null;
		
	    //Used to get the IP of the new request for the loggin System  
		MDC.put("IPClient", request.getRemoteAddr());
		//Flag to mark http acces
		MDC.put(LogConstants.NOINTERNALACCES, LogConstants.ACCESSTYPEHTTP);
		
		if (request instanceof HttpServletRequest){
			
			httpServletRequest =(HttpServletRequest) request;
			
			if (httpServletRequest.getSession(false) != null){
				httpSession = ((HttpServletRequest) request).getSession(false);
				
				//Getting Authentication credentials
				securityContext = ((SecurityContextImpl)httpSession.getAttribute("SPRING_SECURITY_CONTEXT"));
				
				if (securityContext != null){					
					authentication = securityContext.getAuthentication();
					if (authentication != null){
						credentials = (UserCredentials)authentication.getCredentials();
					}
				}				
				
				if(credentials != null){
					MDC.put(LogConstants.USER,credentials.getUserName());
					MDC.put(LogConstants.SESSION,credentials.getUidSession());
					MDC.put(LogConstants.POSITION,credentials.getPosition());
					
				} else if (httpSession.getAttribute("UserName") != null){
					MDC.put(LogConstants.USER,(String)httpSession.getAttribute("UserName"));
					MDC.put(LogConstants.SESSION,(String)httpSession.getAttribute("UidSession"));
					MDC.put(LogConstants.POSITION,(String)httpSession.getAttribute("Position"));
				}
			}
			
			//Compose the acceses trace logs
			logMessage.append("The application has just received a HTTP request from the IP ");
			logMessage.append(request.getRemoteAddr());
			logMessage.append(" to the URL ");
			logMessage.append(ManagementUrl.getUrl(httpServletRequest));
		} else {
			logMessage.append("The application has just received a non-HTTP request from the IP ");
			logMessage.append(request.getRemoteAddr());
		}
		logger.info(logMessage.toString());
	}
	
	@Override
	//Called when the servlet request is going out of scope.
	public void requestDestroyed(ServletRequestEvent sre){
		
		ServletRequest request = sre.getServletRequest();
		HttpServletRequest httpServletRequest = null;
		StringBuilder logMessage = new StringBuilder();
		
		if (request instanceof HttpServletRequest){
			
			httpServletRequest =(HttpServletRequest) request;
			
			//Compose the acceses trace logs
			logMessage.append("The application has responded a HTTP request from the IP ");
			logMessage.append(request.getRemoteAddr());
			logMessage.append(" to the URL ");
			logMessage.append(ManagementUrl.getUrl(httpServletRequest));
		} else {
			logMessage.append("The application has responded a non-HTTP request from the IP ");
			logMessage.append(request.getRemoteAddr());
		}
		
		logger.info(logMessage.toString());
		
		//Clear MDC log Context
		MDC.clear();	  
	}//requestDestroyed
	
}