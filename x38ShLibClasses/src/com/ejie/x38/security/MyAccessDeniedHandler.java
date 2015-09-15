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
package com.ejie.x38.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * 
 * @author UDA
 *
 */

public class MyAccessDeniedHandler implements AccessDeniedHandler {

	//private static final Logger logger = LoggerFactory.getLogger(MyAccessDeniedHandler.class);
	private String errorPage;
	
	@Resource
	private ReloadableResourceBundleMessageSource messageSource;
	
	@Override
	public void handle(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			AccessDeniedException accessDeniedException)
			throws IOException, ServletException {
		
		if (!(httpServletResponse.isCommitted())){
		
			if (!httpServletRequest.getHeaders("X-Requested-With").hasMoreElements()){
		
				if (this.errorPage != null){
					httpServletRequest.setAttribute("SPRING_SECURITY_403_EXCEPTION", accessDeniedException);
	
					httpServletResponse.setStatus(403);
	
					RequestDispatcher dispatcher = httpServletRequest.getRequestDispatcher(this.errorPage);
					dispatcher.forward(httpServletRequest, httpServletResponse);
				} else {
					httpServletResponse.sendError(403, accessDeniedException.getMessage());
				}
				
			} else {
				String message = messageSource.getMessage("security.ajaxAccesError", null, LocaleContextHolder.getLocale());
				ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();

				httpServletResponse.setStatus(403);
				servletOutputStream.print(message);
				httpServletResponse.flushBuffer();		
			}
		}
		
	}
	
	public void setErrorPage(String errorPage) {
		if ((errorPage != null) && (!(errorPage.startsWith("/")))) {
			throw new IllegalArgumentException("errorPage must begin with '/'");
		}
	 
		this.errorPage = errorPage;
	}

		

}