package com.ejie.x38;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.springframework.web.filter.DelegatingFilterProxy;

import com.ejie.x38.serialization.ThreadSafeCache;
import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.ThreadStorageManager;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * 
 * @author UDA
 *
 * Filtro principal que cumple las siguientes funciones:
 * 1- Inicializa la variable de ThreadLocal que asigna un identificador a cada hilo.
 * 2- Verifica si la peticion lleva la cabecera RUP, para activar la el mecanismo de serializacion a traves del UdaMappingJacksonHttpMessageConverter
 * 3- Si llevan excepciones no capturadas por los desarrolladores, redirige a la pagna de error
 * 4- Limpia el ThreadLocal
 */
public class UdaFilter extends DelegatingFilterProxy {

	private static final Logger logger = Logger.getLogger(UdaFilter.class);

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) {
		try {
			logger.log(Level.DEBUG, "New request with UDA identificator "+ThreadStorageManager.getCurrentThreadId()+" has started");
			if(((HttpServletRequest)request).getHeader("RUP")!=null){
				HashMap<?, ?> map = new ObjectMapper().readValue(((HttpServletRequest)request).getHeader("RUP"), HashMap.class);
				for(Entry<?, ?> entry:map.entrySet()){
					ThreadSafeCache.addValue((String)entry.getKey(), (String)entry.getValue());
				}
			}
			filterChain.doFilter(request, response);
			logger.log(Level.DEBUG, "Request with UDA identificator "+ThreadStorageManager.getCurrentThreadId()+" has ended");			
		} catch (Exception e) {
			logger.log(Level.FATAL, StackTraceManager.getStackTrace(e));
			HttpServletResponse resp = (HttpServletResponse)response;
			HttpServletRequest req = (HttpServletRequest)request;
			try {
				resp.sendRedirect(req.getContextPath() + "/error");
			} catch (IOException e1) {				
				logger.log(Level.FATAL, StackTraceManager.getStackTrace(e1));
			}
		}finally{
			ThreadStorageManager.clearCurrentThreadId();
			ThreadSafeCache.clearCurrentThreadCache();
		}
	}
}