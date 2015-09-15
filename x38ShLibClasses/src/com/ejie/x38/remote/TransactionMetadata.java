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
package com.ejie.x38.remote;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import ch.qos.logback.core.LogbackException;

import com.ejie.x38.log.LogConstants;
import com.ejie.x38.log.security.CurrentUserManager;
import com.ejie.x38.security.Credentials;

/**
 * 
 * Metadata DTO.
 * 
 * @author UDA
 * 
 */
public class TransactionMetadata implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger =  LoggerFactory.getLogger(TransactionMetadata.class);

	//private Long uniqueId;
	
	private SecurityContext securityContext = null;
	private String ipClient = "N/A";
	private String noInternalAcces = "N/A";
	private String serverInstance = "N/A";
	
	public TransactionMetadata(){
		TransactionMetadata auxTransactionMetadata = new TransactionMetadata("N/A", "N/A");
		this.setSecurityContext(auxTransactionMetadata.getSecurityContext());
		this.setIpClient(auxTransactionMetadata.getIpClient());
		this.setNoInternalAcces(auxTransactionMetadata.getNoInternalAcces());
		this.setSecurityContext(auxTransactionMetadata.getSecurityContext());
		
		auxTransactionMetadata = null;
	}
	
	public TransactionMetadata(String className, String methodName){

		//Get the data of the security context.
		try{
			this.setSecurityContext(SecurityContextHolder.getContext());
		} catch(Exception e) {
			if (!(e instanceof java.lang.NullPointerException)){
				throw new LogbackException("Error in the EJB remote calling system. Error accessing to the security context. The application doesn't have a security context",e);
			}
		}
		
		//Indicated the Ejb acces
		this.setNoInternalAcces(LogConstants.ACCESSTYPEEJB);
		
		//Recovered the server instance name 
		this.setServerInstance(System.getProperty("weblogic.Name"));
		
		//Http access-dependent data
		if((MDC.get(LogConstants.NOINTERNALACCES) != null)&&((MDC.get(LogConstants.NOINTERNALACCES).equals(LogConstants.ACCESSTYPEHTTP))||(MDC.get(LogConstants.NOINTERNALACCES).equals(LogConstants.ACCESSTYPEEJB)))){
			//IpAddres
			this. setIpClient(MDC.get("IPClient"));
		}
		
		logger.info("Make a remote call. The remote method "+methodName+" of the class "+className+" is invoked");
	}

	public String getNoInternalAcces() {
		return noInternalAcces;
	}

	public void setNoInternalAcces(String noInternalAcces) {
		this.noInternalAcces = noInternalAcces;
	}

	public String getServerInstance() {
		return serverInstance;
	}

	public void setServerInstance(String serverInstance) {
		this.serverInstance = serverInstance;
	}

	public SecurityContext getSecurityContext() {
		return securityContext;
	}

	public void setSecurityContext(SecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	public String getIpClient() {
		return ipClient;
	}

	public void setIpClient(String ipClient) {
		this.ipClient = ipClient;
	}
	
	public void install (){
		
		MDC.put(LogConstants.NOINTERNALACCES, this.getNoInternalAcces());
		MDC.put("IPClient", this.getIpClient());		
		
		if (securityContext != null){
			
			Credentials Credentials = (Credentials) this.getSecurityContext().getAuthentication().getCredentials();
			
			SecurityContextHolder.setContext(this.getSecurityContext());
			MDC.put(LogConstants.USER, CurrentUserManager.getCurrentUsername());
			MDC.put(LogConstants.SESSION, CurrentUserManager.getCurrentUserN38UidSesion(Credentials));
			MDC.put(LogConstants.POSITION, CurrentUserManager.getPosition(Credentials));
		} else {
			logger.info("Remote service has not received the security context of the caller server. If the application has security services, is likely to occur a exception");
		}
		
		logger.info("The "+System.getProperty("weblogic.Name")+" instance receives a request remote from the instance "+this.getServerInstance()); 
	}
	
	public void clear (){
		logger.info("The "+System.getProperty("weblogic.Name")+" has completed the response to the request remote from the instance "+this.getServerInstance());
		MDC.clear();
		SecurityContextHolder.clearContext();
	}
}