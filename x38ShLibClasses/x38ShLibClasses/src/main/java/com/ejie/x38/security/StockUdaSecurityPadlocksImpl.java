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

import java.util.HashMap;

/**
 * 
 * @author UDA
 *
 */
public class StockUdaSecurityPadlocksImpl implements StockUdaSecurityPadlocks{
	
	   private final HashMap<String, UdaSecurityPadlock> SecurityPadlocks = new HashMap<String, UdaSecurityPadlock>();
	   
	   // Create fuctions
	   public synchronized void  createSecurityPadlock(String sessionId, Long allowedAccessThread){
		   this.SecurityPadlocks.put(getBaseSessionId(sessionId), new UdaSecurityPadlock(allowedAccessThread));
	   } 
	   
	   public synchronized void createSecurityPadlock(String sessionId, Long allowedAccessThread, int semaphoreLimit){
		   
		   this.SecurityPadlocks.put(getBaseSessionId(sessionId), new UdaSecurityPadlock(allowedAccessThread, semaphoreLimit));
	   }
	   
	   // Modify values
	   public Long getAllowedAccessThread(String sessionId){
		   UdaSecurityPadlock udaSecurityPadlock = this.SecurityPadlocks.get(getBaseSessionId(sessionId));
		   if (udaSecurityPadlock != null){
			   return udaSecurityPadlock.getAllowedAccessThread(); 
		   } else {
			   return null;
		   }
	   }
	   
	   public void setAllowedAccessThread(String sessionId, Long accessThread){
		   UdaSecurityPadlock udaSecurityPadlock = this.SecurityPadlocks.get(getBaseSessionId(sessionId));
		   if (udaSecurityPadlock != null){
			   udaSecurityPadlock.setAllowedAccessThread(accessThread); 
		   } else {
			   createSecurityPadlock(sessionId, accessThread); 
		   }
		   
	   }
	   
	   //Control functions
	   public boolean existingSecurityPadlock(String sessionId){
		   UdaSecurityPadlock udaSecurityPadlock = this.SecurityPadlocks.get(getBaseSessionId(sessionId));
		   if (udaSecurityPadlock != null){
			   return true;
		   } else {
			   return false;
		   }
	   }
	   
	   public boolean allowedAccess(String sessionId, Long accessThread) throws NullPointerException{
		   UdaSecurityPadlock udaSecurityPadlock = this.SecurityPadlocks.get(getBaseSessionId(sessionId));
		   if (udaSecurityPadlock != null){
			   return udaSecurityPadlock.allowedAccess(accessThread);
		   } else {
			   return false;
		   }
			   
	   }
	   
	   public void acquire(String sessionId) throws NullPointerException, InterruptedException{
		   UdaSecurityPadlock udaSecurityPadlock = this.SecurityPadlocks.get(getBaseSessionId(sessionId));
		   if (udaSecurityPadlock != null){
			   udaSecurityPadlock.acquire();
		   } else {
			   throw new NullPointerException("The session id don't have a correlation in the object. It need a previous creation.");
		   }
	   }
	   
	   public void release(String sessionId) throws NullPointerException{
		   UdaSecurityPadlock udaSecurityPadlock = this.SecurityPadlocks.get(getBaseSessionId(sessionId));
		   if (udaSecurityPadlock != null){
			   udaSecurityPadlock.release();
		   } else {
			   throw new NullPointerException("The session id don't have a correlation in the object. It need a previous creation.");
		   }
	   }
	   
	   public boolean tryAcquire(String sessionId) throws NullPointerException{
		   UdaSecurityPadlock udaSecurityPadlock = this.SecurityPadlocks.get(getBaseSessionId(sessionId));
		   if (udaSecurityPadlock != null){
			   return udaSecurityPadlock.tryAcquire();
		   } else {
			   throw new NullPointerException("The session id don't have a correlation in the object. It need a previous creation.");
		   }
	   }
	   
	   // Functions to control the Session's garbage
	   public void deleteCredentialLoadObject(String sessionId){
		   UdaSecurityPadlock udaSecurityPadlock = this.SecurityPadlocks.get(getBaseSessionId(sessionId));
			if (udaSecurityPadlock != null){
				udaSecurityPadlock.freeAllThreads();
				this.SecurityPadlocks.remove(getBaseSessionId(sessionId));
			}
		}
	   
	   private String getBaseSessionId(String sessionId){
		   String[] baseSessionId = sessionId.split("!"); 
		   return baseSessionId[0];
		   
	   }
}