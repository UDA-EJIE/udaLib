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
import java.nio.charset.Charset;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.ejie.x38.log.LogConstants;
import com.ejie.x38.util.ThreadStorageManager;

/**
 * 
 * @author UDA
 *
 */
public  class PreAuthenticateProcessingFilter extends
		AbstractPreAuthenticatedProcessingFilter {

	@Resource
	private ReloadableResourceBundleMessageSource messageSource;
	
	private static final Logger logger = LoggerFactory
			.getLogger(PreAuthenticateProcessingFilter.class);

	//Semaphore for not concurrent access
//	protected StockUdaSecurityPadlocksImpl stockUdaSecurityPadlocks;
	private PerimetralSecurityWrapper perimetralSecurityWrapper;

	/**
	 * Try to authenticate a pre-authenticated user with Spring Security if the
	 * user has not yet been authenticated.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		boolean isAjax = ((HttpServletRequest)request).getHeaders("X-Requested-With").hasMoreElements();
		logger.info("the request is entering in the security system");

		String valid = getPerimetralSecurityWrapper().validateSession((HttpServletRequest)request, (HttpServletResponse) response);

		if(valid.equals("true")){
			if ((!isAjax) || (((HttpServletRequest)request).getSession(false).getAttribute("userChange") == null)){
				super.doFilter(request, response, chain);
				logger.info("the request is exiting of the security system");
			} else {
				HttpSession httpSession = ((HttpServletRequest)request).getSession(false);
				
				//Delete security variables 
				httpSession.removeAttribute("name");
				httpSession.removeAttribute("surname");
				httpSession.removeAttribute("fullName");
				httpSession.removeAttribute("userName");
				httpSession.removeAttribute("reloadData");
				httpSession.removeAttribute("uidSession");
				httpSession.removeAttribute("userChange");
				httpSession.removeAttribute("destroySessionSecuritySystem");
				
				String content = messageSource.getMessage("security.ajaxLoadError", null, LocaleContextHolder.getLocale());
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
				httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
				httpServletResponse.setContentLength(content.getBytes(Charset.forName(httpServletResponse.getCharacterEncoding())).length);
				httpServletResponse.getWriter().print(content);
				httpServletResponse.flushBuffer();
			}			
		
		} else if(valid.equals("false")) {
			chain.doFilter(request, response);
			logger.info("the request is exiting of the security system");
		} else {
			if(!isAjax){ 
				((HttpServletResponse) response).sendRedirect(valid);
				return;
			} else {
				String content = messageSource.getMessage("security.ajaxError", null, LocaleContextHolder.getLocale());
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
				httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
				httpServletResponse.setContentLength(content.getBytes(Charset.forName(httpServletResponse.getCharacterEncoding())).length);
				httpServletResponse.getWriter().print(content);
				httpServletResponse.flushBuffer();
			}
		}
	}
	

	@Override
	protected synchronized Object getPreAuthenticatedCredentials(HttpServletRequest request) {

		Credentials result = this.perimetralSecurityWrapper.getCredentials();
		
		try{ 
			//Load data's credential of user
			result.loadCredentialsData(this.perimetralSecurityWrapper, request);
			
			//Code associated to the security system of UDA. It is very important to operation of the internal gestion of the system.    
			MDC.put(LogConstants.SESSION,result.getUidSession());
			MDC.put(LogConstants.USER,result.getUserName());
			MDC.put(LogConstants.POSITION,result.getPosition());
			
		} finally {
//			stockUdaSecurityPadlocks.setAllowedAccessThread(request.getSession(false).getId(), null);
//			stockUdaSecurityPadlocks.release(request.getSession(false).getId());
			
			request.getSession(false).removeAttribute("credentialsLoading");
			//[END] Code associated to the security system of UDA.
		}

		return result;
	}

	@Override
	protected synchronized Object getPreAuthenticatedPrincipal(HttpServletRequest httpRequest) {
		String principalUser = null;
		HttpSession session = httpRequest.getSession(false);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
//		if(!stockUdaSecurityPadlocks.existingSecurityPadlock(session.getId())){
//			stockUdaSecurityPadlocks.createSecurityPadlock(session.getId(), null);
//		}
		
//		if (!stockUdaSecurityPadlocks.allowedAccess(session.getId(), ThreadStorageManager.getCurrentThreadId())){
//			try{
//				stockUdaSecurityPadlocks.acquire(session.getId());
//			} catch (InterruptedException e) {
//				throw new SecurityException("UDA's Security system error", e.getCause());
//			}
//		}
				
		if(isReloadData(httpRequest,ThreadStorageManager.getCurrentThreadId())){
			
			if(authentication != null){
				authentication.setAuthenticated(false);
			}
			
			//if the user changes then the user session is invalidate  
			if (session.getAttribute("userChange") == null){
				logger.info("The cache of user's credentials is expired. Proceeds to recharge the user's credentials");
				setInvalidateSessionOnPrincipalChange(false);
			
			} else {
				logger.info("The incoming user and the authenticated user are not equal. Proceed to load the new user's credentials");
				session.removeAttribute("userChange");
			}
			
			SecurityContextHolder.clearContext();
			return "##udaReloadUser##";
		} else {
//			if (stockUdaSecurityPadlocks.existingSecurityPadlock(session.getId()) && !stockUdaSecurityPadlocks.allowedAccess(session.getId(), ThreadStorageManager.getCurrentThreadId())){
//				stockUdaSecurityPadlocks.release(session.getId());
//			}
			setInvalidateSessionOnPrincipalChange(true);
		}
		
		principalUser = this.perimetralSecurityWrapper.getUserConnectedUserName(httpRequest);
		logger.info( "The incoming user is: "+principalUser);
		
		return principalUser;
	}
	
	private synchronized boolean isReloadData(HttpServletRequest httpRequest, Long currentThreadId){
		HttpSession session = httpRequest.getSession(false);
		Long reloadDataId = (Long)session.getAttribute("reloadData");
		
		if(session != null && session.getAttribute("reloadData") !=null && currentThreadId.equals(reloadDataId)){
//			if(httpRequest.getHeaders("X-Requested-With").hasMoreElements()){
//				stockUdaSecurityPadlocks.setAllowedAccessThread(session.getId(), ThreadStorageManager.getCurrentThreadId());
//			}
			session.setAttribute("credentialsLoading", "true");
			session.removeAttribute("reloadData");
			return true;
		} else {
			return false;
		}
	}
	
	// Getters & Setters
	public PerimetralSecurityWrapper getPerimetralSecurityWrapper() {
		return this.perimetralSecurityWrapper;
	}

	public void setPerimetralSecurityWrapper(PerimetralSecurityWrapper perimetralSecurityWrapper) {
		this.perimetralSecurityWrapper = perimetralSecurityWrapper;
	}
	
//	public StockUdaSecurityPadlocksImpl getStockUdaSecurityPadlocks() {
//		return this.stockUdaSecurityPadlocks;
//	}
//
//	public void setStockUdaSecurityPadlocks(StockUdaSecurityPadlocksImpl stockUdaSecurityPadlocks) {
//		this.stockUdaSecurityPadlocks = stockUdaSecurityPadlocks;
//	}
}