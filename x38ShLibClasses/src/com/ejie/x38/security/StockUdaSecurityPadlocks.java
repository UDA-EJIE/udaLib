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


/**
 * 
 * @author UDA
 *
 */
public interface StockUdaSecurityPadlocks {
	
	   public void createSecurityPadlock(String sessionId, Long allowedAccessThread);
	   
	   public void createSecurityPadlock(String sessionId, Long allowedAccessThread, int semaphoreLimit);
	   
	   public Long getAllowedAccessThread(String sessionId);
	   
	   public void setAllowedAccessThread(String sessionId, Long accessThread);
	   
	   public boolean existingSecurityPadlock(String sessionId);
	   
	   public boolean allowedAccess(String sessionId, Long accessThread) throws NullPointerException;
	   
	   public void acquire(String sessionId) throws NullPointerException, InterruptedException;
	   
	   public void release(String sessionId) throws NullPointerException;
	   
	   public boolean tryAcquire(String sessionId) throws NullPointerException;
	   
	   public void deleteCredentialLoadObject(String sessionId);
}
 