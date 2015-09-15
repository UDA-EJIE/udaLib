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
package com.ejie.x38;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.ejie.x38.security.StockUdaSecurityPadlocksImpl;
import com.ejie.x38.serialization.ThreadSafeCache;
import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.ThreadStorageManager;

/**
 * 
 * Filtro principal que cumple las siguientes funciones:
 * 1- Inicializa la variable de ThreadLocal que asigna un identificador a cada hilo.
 * 2- Verifica si la peticion lleva la cabecera RUP, para activar el mecanismo de serializacion a traves del UdaMappingJacksonHttpMessageConverter
 * 3- Si llevan excepciones no capturadas por los desarrolladores, redirige a la pagna de error
 * 4- Limpia el ThreadLocal
 * 
 * @author UDA
 * 
 */
public class UdaFilter extends DelegatingFilterProxy {
	
	private static final Logger logger = LoggerFactory.getLogger(UdaFilter.class);

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) {

		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		
		try {
			
			
			logger.debug( "New request with UDA identificator "+ThreadStorageManager.getCurrentThreadId()+" has started");
			
			String rupHeader = httpServletRequest.getHeader("RUP");
			if(rupHeader!=null){
				ThreadSafeCache.addValue("RUP", "RUP");
				HashMap<?, ?> map = new ObjectMapper().readValue(rupHeader, HashMap.class);
				for(Entry<?, ?> entry:map.entrySet()){
					ThreadSafeCache.addValue((String)entry.getKey(), (String)entry.getValue());
				}
			}
			
			String rupMultiModelHeader = httpServletRequest.getHeader("RUP_MULTI_ENTITY");
			if(rupMultiModelHeader!=null){
				ThreadSafeCache.addValue("RUP_MULTI_ENTITY", "RUP_MULTI_ENTITY");
			}
			
			filterChain.doFilter(request, response);
			logger.debug( "Request with UDA identificator "+ThreadStorageManager.getCurrentThreadId()+" has ended");			
		} catch (Exception exception) {
			logger.error(StackTraceManager.getStackTrace(exception));
			
			HttpSession session = httpServletRequest.getSession();
			String sessionId = httpServletRequest.getSession().getId();

			try {
				
				if (!response.isCommitted()){
					ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
					StockUdaSecurityPadlocksImpl stockUdaSecurityPadlocks = (StockUdaSecurityPadlocksImpl)ctx.getBean("stockUdaSecurityPadlocks");
					if (stockUdaSecurityPadlocks != null && stockUdaSecurityPadlocks.existingSecurityPadlock(sessionId)){
						stockUdaSecurityPadlocks.setAllowedAccessThread(sessionId, null);
						stockUdaSecurityPadlocks.release(sessionId);
					}
					
					HttpServletRequest req = (HttpServletRequest) request;
					HttpServletResponse res = (HttpServletResponse) response;
					
					StringBuilder error = new StringBuilder(req.getContextPath());
					error.append("/error?exception_name=").append(exception.getClass().getName());
					error.append("&exception_message=").append(exception.getMessage());
					error.append("&exception_trace=");
					int outLength = error.length();
					
					for (StackTraceElement trace : exception.getStackTrace()) {
						outLength = outLength + 5 /* </br> */ + trace.toString().length();
						if (outLength <= 2044 /* IE Query String limit */){
							error.append(trace.toString()).append("</br>");
						} else {
							break;
						}
					}
					
//					res.sendRedirect(req.getContextPath() + "/error"+error);
					res.sendRedirect(error.toString());
				}
			} catch (Exception exc) {				
				logger.error("Problem with sending of the response",exc);
			}
		}finally{
			ThreadStorageManager.clearCurrentThreadId();
			ThreadSafeCache.clearCurrentThreadCache();
		}
	}
}