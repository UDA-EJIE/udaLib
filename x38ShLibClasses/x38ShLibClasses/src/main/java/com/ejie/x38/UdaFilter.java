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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.ejie.x38.serialization.ThreadSafeCache;
import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.StaticsContainer;
import com.ejie.x38.util.ThreadStorageManager;
import com.ejie.x38.util.WrappedRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Filtro principal que cumple las siguientes funciones:
 * 1- Inicializa la variable de ThreadLocal que asigna un identificador a cada hilo.
 * 2- Verifica si la petición lleva la cabecera RUP, para activar el mecanismo de serialización a través del UdaMappingJackson2HttpMessageConverter.
 * 3- Si llevan excepciones no capturadas por los desarrolladores, redirige a la página de error.
 * 4- Limpia el ThreadLocal.
 * 
 * @author UDA
 */
public class UdaFilter extends DelegatingFilterProxy {

	private static final Logger logger = LoggerFactory.getLogger(UdaFilter.class);
	private static final String validationPattern = "[\\p{L}0-9\\.,\\-\\+_:~\\(\\)\\\\/¿\\?@&%#\\$\\* ]*$";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		// Comprueba si ha sido referido por algún sistema de seguridad de EJIE.
		final boolean refersFromSecuritySystem;
		if (httpServletRequest.getHeader("referer") != null) {
			// Soporta XLNetS (tanto Linux como Windows) y OAM.
			refersFromSecuritySystem = Pattern.compile(
					"xlnets\\.servicios(?:\\.des|\\.pru)?\\.(?:ejgv(?:\\.euskalsarea\\.eus|\\.jaso)?|jakina\\.ejie(?:des|pru)?\\.net)?"
							+ "|(?:desant01\\.|pruebasnt01\\.)?jakina.ejgvdns"
							+ "|sargune(?:\\.sb|\\.des|\\.pru)?\\.(?:euskadi|ejgv\\.euskalsarea)?\\.eus",
					Pattern.CASE_INSENSITIVE).matcher(httpServletRequest.getHeader("referer")).find();
			logger.debug("Referer is {} and the pattern result is {}", httpServletRequest.getHeader("referer"), refersFromSecuritySystem);
		} else {
			refersFromSecuritySystem = false;
			logger.debug("Referer is null. If a value was expected, check if the protocol is still the same.");
		}

		try {
			logger.debug("New request with UDA identificator {} has started", ThreadStorageManager.getCurrentThreadId());

			if (httpServletRequest.getHeader("RUP") != null) {
				ThreadSafeCache.addValue("RUP", "RUP");
				HashMap<?, ?> map = new ObjectMapper().readValue(httpServletRequest.getHeader("RUP"), HashMap.class);
				for (Entry<?, ?> entry : map.entrySet()) {
					ThreadSafeCache.addValue((String) entry.getKey(), (String) entry.getValue());
				}
			}

			if (httpServletRequest.getHeader("RUP_MULTI_ENTITY") != null) {
				ThreadSafeCache.addValue("RUP_MULTI_ENTITY", "RUP_MULTI_ENTITY");
			}

			if (!httpServletRequest.getParameterMap().isEmpty()) {
				// Si se cumplen las condiciones, se procederá a validar y almacenar en sesión los parámetros recibidos.
				// Esta gestión es necesaria para disponer de los datos una vez se obtenga una credencial válida a través del sistema de seguridad.
				if (SecurityContextHolder.getContext().getAuthentication() == null && !refersFromSecuritySystem) {
					Map<String, String[]> extraParams = new HashMap<String, String[]>();

					// Validar parámetros recibidos para evitar un "Trust boundary".
					for (Map.Entry<String, String[]> entry : ((Map<String, String[]>) httpServletRequest.getParameterMap()).entrySet()) {
						if (entry.getValue().length > 1) {
							List<String> values = new ArrayList<String>();
							for (int index = 0; index < entry.getValue().length; index++) {
								if (entry.getValue()[index].matches(validationPattern)) {
									values.add(entry.getValue()[index]);
									logger.debug("Added parameter with key {} and value {} from index {}", entry.getKey(), entry.getValue()[index], index);
								} else {
									logger.debug(
											"Parameter with key {} and value {} in index {} does not match the pattern",
											entry.getKey(), entry.getValue()[index], index);
								}
							}
							extraParams.put(entry.getKey(), values.toArray(new String[0]));
						} else {
							if (entry.getValue()[0].matches(validationPattern)) {
								extraParams.put(entry.getKey(), entry.getValue());
								logger.debug("Added parameter with key {} and value {}", entry.getKey(), entry.getValue()[0]);
							} else {
								logger.debug("Parameter with key {} and value {} does not match the pattern", entry.getKey(), entry.getValue()[0]);
							}
						}
					}

					// Se guardan los parámetros en sesión para disponer de ellos una vez se obtenga la credencial.
					httpServletRequest.getSession().setAttribute("REQUESTED_PARAMS", extraParams);
					httpServletRequest.getSession().setAttribute("REQUEST_METHOD", httpServletRequest.getMethod());
				}
			}

			// Cuando la sesión contenga los parámetros que se guardaron al llegar a la aplicación y el referido sea el sistema de seguridad,
			// se procederá a insertar esos datos en la petición o en caso de ser una petición de tipo GET, en el query string de la misma.
			if (httpServletRequest.getSession().getAttribute("REQUESTED_PARAMS") != null
					&& httpServletRequest.getSession().getAttribute("REQUEST_METHOD") != null
					&& !httpServletRequest.getSession().getAttribute("REQUEST_METHOD").equals("GET")
					&& refersFromSecuritySystem) {
				logger.debug(
							"Request will be wrapped using WrappedRequest because both REQUESTED_PARAMS and REQUEST_METHOD (with {} value) exist in session",
							httpServletRequest.getSession().getAttribute("REQUEST_METHOD"));
				filterChain.doFilter(
						new WrappedRequest(httpServletRequest,
								(Map<String, String[]>) httpServletRequest.getSession().getAttribute("REQUESTED_PARAMS"),
								httpServletRequest.getSession().getAttribute("REQUEST_METHOD").toString()),
						response);
			} else {
				logger.debug("Request won't be wrapped");
				filterChain.doFilter(request, response);
			}

			logger.debug("Request with UDA identificator {} has ended", ThreadStorageManager.getCurrentThreadId());
		} catch (Exception exception) {
			logger.error(StackTraceManager.getStackTrace(exception));

			try {
				if (!response.isCommitted()) {
					StringBuilder error = new StringBuilder(httpServletRequest.getContextPath());
					error.append("/error");
					
					if (StaticsContainer.isDetailedError()) {
						error.append("?exception_name=").append(exception.getClass().getName());
						error.append("&exception_message=").append(exception.getMessage());
						error.append("&exception_trace=");
						int outLength = error.length();

						for (StackTraceElement trace : exception.getStackTrace()) {
							outLength = outLength + 5 + trace.toString().length();
							// IE Query String limit
							if (outLength <= 2043) {
								error.append(trace.toString()).append("</br>");
							} else {
								break;
							}
						}
					}

					httpServletResponse.sendRedirect(error.toString());
				}
			} catch (Exception exc) {
				logger.error("Problem with sending of the response", exc);
			}
		} finally {
			ThreadStorageManager.clearCurrentThreadId();
			ThreadSafeCache.clearCurrentThreadCache();

			// Eliminar los parámetros que se hayan podido ingresar en la sesión antes de la obtención de una credencial.
			if (refersFromSecuritySystem) {
				httpServletRequest.getSession().removeAttribute("REQUESTED_PARAMS");
				httpServletRequest.getSession().removeAttribute("REQUEST_METHOD");
			}
		}
	}
}