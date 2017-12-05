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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.DelegatingFilterProxy;

import com.ejie.x38.util.IframeXHREmulationUtils;

/**
 * Filtro encargado de modificar la request en caso de que sea necesaria emular
 * el comportamiento de la gestión de errores HTTP al utilizar iframes.
 * 
 * @author UDA
 * 
 */
public class IframeXHREmulationFilter extends DelegatingFilterProxy {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		
		// Se comprueba si es necesario realizar la emulación.
		if (IframeXHREmulationUtils.isIframeEmulationRequired(request)) {
			// Se genera un wrapper de la response para poder insertar la
			// respuesta indicada en la response dentro de la estructura
			// necesaria.
			
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			
			HtmlResponseWrapper capturingResponseWrapper = new HtmlResponseWrapper((HttpServletResponse) response);
			
			try {

				// Se continúa con la ejecución de la petición.
				filterChain.doFilter(request, capturingResponseWrapper);

				String content = capturingResponseWrapper.getCaptureAsString();

				IframeXHREmulationUtils.writeIframeHttpStatus(httpServletResponse, content,
						capturingResponseWrapper.getStatus());

			} catch (Exception e) {

				String content = capturingResponseWrapper.getCaptureAsString();

				IframeXHREmulationUtils.writeIframeHttpStatus(httpServletResponse, content,
						capturingResponseWrapper.getStatus());
			}
		} else {
			// En caso de no ser necesaria la emulación se continua con la
			// ejecución.
			filterChain.doFilter(request, response);
		}
	}

}
