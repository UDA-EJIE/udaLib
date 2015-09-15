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

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.DelegatingFilterProxy;

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
		try {
			
			HttpServletRequest httpServletRequest = (HttpServletRequest)request;
			
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
				
//				HashMap<?, ?> map = new ObjectMapper().readValue(rupMultiModelHeader, HashMap.class);
//				for(Entry<?, ?> entry:map.entrySet()){
//					ThreadSafeCache.addValue((String)entry.getKey(), (String)entry.getValue());
//				}
			}
			
			filterChain.doFilter(request, response);
			logger.debug( "Request with UDA identificator "+ThreadStorageManager.getCurrentThreadId()+" has ended");			
		} catch (Exception exception) {
			logger.error(StackTraceManager.getStackTrace(exception));

			try {
				if (!response.isCommitted()){
					
					HttpServletRequest req = (HttpServletRequest) request;
					HttpServletResponse res = (HttpServletResponse) response;
					
					StringBuilder error = new StringBuilder("?");
					error.append("exception_name=").append(exception.getClass().getName());
					error.append("&exception_message=").append(exception.getMessage());
					error.append("&exception_trace=");
					for (StackTraceElement trace : exception.getStackTrace()) {
						error.append(trace.toString()).append("</br>");
					}
					
					res.sendRedirect(req.getContextPath() + "/error"+error);
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