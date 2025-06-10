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
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.ejie.x38.util.ManagementUrl;
import com.ejie.x38.util.StaticsContainer;

/**
 * 
 * @author UDA
 *
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint,
		Ordered {
	private static final Logger logger = LoggerFactory
			.getLogger(MyAuthenticationEntryPoint.class);

	private int order = Integer.MAX_VALUE;

	private PerimetralSecurityWrapper perimetralSecurityWrapper;

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		if (authException != null)
			logger.debug("Authentication Exception: "+ authException.getMessage());
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		String url;
		Cookie requestCookies[] = request.getCookies();
		StringBuilder portalData = new StringBuilder("/");
		boolean isAjax = request.getHeaders("X-Requested-With").hasMoreElements();
		boolean isPortal = false;
		
		//valoracion del acelerador
		String originalURL = ManagementUrl.getUrl(httpRequest);
		
		if(StaticsContainer.aplicInPortal){
			if (requestCookies != null){
				for (int i = 0; i < requestCookies.length; i++) {
					if (requestCookies[i].getName().equals("r01PortalInfo")){
						isPortal = true;
						
						portalData.append(requestCookies[i].getValue ());
						StringBuilder host = new StringBuilder(httpRequest.getServerName());
						
						if (originalURL.split(":").length > 1){
							host.append(":");
							host.append(httpRequest.getServerPort());
						}
						
						originalURL = originalURL.replaceAll(host.toString(), host.toString()+portalData.toString());
						break;
					}
		        }
			}
		}
		
		logger.info("XLNetS session isn't valid or not created!");
		
		if (isAjax && StaticsContainer.isXhrRedirectOnError()){
			if (StaticsContainer.getXhrUnauthorizedPage() != null && StaticsContainer.getXhrUnauthorizedPage().equals("referer")) {
				String referer = request.getHeader("Referer");
				
				if (referer.contains(";jsessionid=")) {
					referer = referer.substring(0, referer.indexOf(";jsessionid="));
				}
				
				StaticsContainer.setXhrUnauthorizedPage(getPerimetralSecurityWrapper().getURLLogin(referer, isAjax));
			}
			
			url = this.getUrlAjax(StaticsContainer.getXhrUnauthorizedPage() != null ? StaticsContainer.getXhrUnauthorizedPage() : getPerimetralSecurityWrapper().getURLLogin(originalURL, isAjax), isPortal);
			
			// Se detecta si es una petición AJAX
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpResponse.setHeader("LOCATION", url);
		}else{
			url = getPerimetralSecurityWrapper().getURLLogin(originalURL , isAjax);
			if(isAjax){
				url = this.getUrlAjax(url, isPortal);
			}
			
			if (!url.matches("^https?://.*$")) {
				url = new URL(httpRequest.getScheme(), httpRequest.getServerName(), httpRequest.getServerPort(), url).toString();
			}
			
			logger.info("Redirecting to next URL:" + url);
			httpResponse.sendRedirect(url);
		}	
	}

	@Override
	public int getOrder() {
		return order;
	}

	// Private 
	private String getUrlAjax(String url, boolean isPortal){
		String sep = url.indexOf('?') > -1 ? "&" : "?";
		return isPortal ? url.concat(sep).concat("R01HNoPortal=true") : url;
	}
	
	// Getters & Setters
	public PerimetralSecurityWrapper getPerimetralSecurityWrapper() {
		return perimetralSecurityWrapper;
	}

	public void setPerimetralSecurityWrapper(
			PerimetralSecurityWrapper perimetralSecurityWrapper) {
		this.perimetralSecurityWrapper = perimetralSecurityWrapper;
	}
}
