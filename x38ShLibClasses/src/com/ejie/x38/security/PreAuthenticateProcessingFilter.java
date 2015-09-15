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
import java.util.HashMap;
import java.util.Vector;

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
	private StockUdaSecurityPadlocksImpl stockUdaSecurityPadlocks;
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
			if ((!isAjax) || (((HttpServletRequest)request).getSession().getAttribute("userChange") == null)){
				super.doFilter(request, response, chain);
				logger.info("the request is exiting of the security system");
			} else {
				HttpSession httpSession = ((HttpServletRequest)request).getSession();
				
				//Delete security variables 
				httpSession.removeAttribute("name");
				httpSession.removeAttribute("surname");
				httpSession.removeAttribute("fullName");
				httpSession.removeAttribute("userName");
				httpSession.removeAttribute("reloadData");
				httpSession.removeAttribute("uidSession");
				httpSession.removeAttribute("userChange");
				httpSession.removeAttribute("destroyXLNetsSession");
				
				((HttpServletResponse) response).sendError(403, messageSource.getMessage("security.ajaxLoadError", null, LocaleContextHolder.getLocale()));
			}			
		
		} else if(valid.equals("false")) {
			chain.doFilter(request, response);
			logger.info("the request is exiting of the security system");
		} else {
			if(!isAjax){ 
				((HttpServletResponse) response).sendRedirect(valid);
				return;
			} else {
				((HttpServletResponse) response).sendError(403, messageSource.getMessage("security.ajaxError", null, LocaleContextHolder.getLocale()));
			}
		}
	}
	

	@Override
	protected synchronized Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		UserCredentials result = null;
		boolean isAjax = request.getHeaders("X-Requested-With").hasMoreElements();
		String uidSession = getPerimetralSecurityWrapper().getUserConnectedUidSession(request);
		String userName = getPerimetralSecurityWrapper().getUserConnectedUserName(request);
		String position = getPerimetralSecurityWrapper().getUserPosition(request);
		Vector <String> UserInstances = getPerimetralSecurityWrapper().getUserInstances(request);
		String udaValidateSessionId = getPerimetralSecurityWrapper().getUdaValidateSessionId(request);
		String policy = getPerimetralSecurityWrapper().getPolicy(request);
		boolean certificate = getPerimetralSecurityWrapper().getIsCertificate(request);
		String nif = getPerimetralSecurityWrapper().getNif(request);
		HashMap<String, String> userData = getPerimetralSecurityWrapper().getUserDataInfo(request);
		boolean destroySession = false;
		
		Object object = getPerimetralSecurityWrapper();
		
		if(object instanceof PerimetralSecurityWrapperN38Impl){
			destroySession = ((PerimetralSecurityWrapperN38Impl) object).getDestroyXLNetsSession();			 
		}
		
		result= new UserCredentials(UserInstances, userName, userData.get("name"), userData.get("surname"), userData.get("fullName"),nif, uidSession, position, udaValidateSessionId, policy, certificate, destroySession);
		logger.info( "The incoming user's Credentials are loading. The data of its credentials is: [uidSession = "+uidSession+" ] [userName = "+userName+" ] [position = "+position+"]");
				
		MDC.put(LogConstants.SESSION,uidSession);
		MDC.put(LogConstants.USER,userName);
		MDC.put(LogConstants.POSITION,position);
		
		if(isAjax){
			stockUdaSecurityPadlocks.setAllowedAccessThread(request.getSession().getId(), null);
			stockUdaSecurityPadlocks.release(request.getSession().getId());
		}
		
		request.getSession().removeAttribute("credentialsLoading");
				
		return result;
	}

	@Override
	protected synchronized Object getPreAuthenticatedPrincipal(HttpServletRequest httpRequest) {
		String principalUser = null;
		HttpSession session = httpRequest.getSession(false);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isAjax = httpRequest.getHeaders("X-Requested-With").hasMoreElements();
		
		if(!stockUdaSecurityPadlocks.existingSecurityPadlock(session.getId())){
			stockUdaSecurityPadlocks.createSecurityPadlock(session.getId(), null);
		}
		
		if(isAjax){
			if (!stockUdaSecurityPadlocks.allowedAccess(session.getId(), ThreadStorageManager.getCurrentThreadId())){
				try{
					stockUdaSecurityPadlocks.acquire(session.getId());
				} catch (InterruptedException e) {
					throw new SecurityException("UDA's Security system error", e.getCause());
				}
			} else {
			}
		}
				
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
			if (stockUdaSecurityPadlocks.existingSecurityPadlock(session.getId()) && !stockUdaSecurityPadlocks.allowedAccess(session.getId(), ThreadStorageManager.getCurrentThreadId())){
				stockUdaSecurityPadlocks.release(session.getId());
			}
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
			if(httpRequest.getHeaders("X-Requested-With").hasMoreElements()){
				stockUdaSecurityPadlocks.setAllowedAccessThread(session.getId(), ThreadStorageManager.getCurrentThreadId());
			}
			session.setAttribute("credentialsLoading", "true");
			session.removeAttribute("reloadData");
			return true;
		} else {
			return false;
		}
	}
	
	// Getters & Setters
	public PerimetralSecurityWrapper getPerimetralSecurityWrapper() {
		return perimetralSecurityWrapper;
	}

	public void setPerimetralSecurityWrapper(PerimetralSecurityWrapper perimetralSecurityWrapper) {
		this.perimetralSecurityWrapper = perimetralSecurityWrapper;
	}
	
	public StockUdaSecurityPadlocksImpl getStockUdaSecurityPadlocks() {
		return this.stockUdaSecurityPadlocks;
	}

	public void setStockUdaSecurityPadlocks(StockUdaSecurityPadlocksImpl stockUdaSecurityPadlocks) {
		this.stockUdaSecurityPadlocks = stockUdaSecurityPadlocks;
	}
}