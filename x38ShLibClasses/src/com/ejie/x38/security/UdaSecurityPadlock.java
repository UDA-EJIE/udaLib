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

import java.util.concurrent.Semaphore;


/**
 * 
 * @author UDA
 *
 */
 
 public class UdaSecurityPadlock {
	 
   private Semaphore credentialLoad;
   private Long allowedAccessThread; 

   protected UdaSecurityPadlock(Long allowedAccessThread){
	   this.allowedAccessThread = allowedAccessThread;
	   this.credentialLoad = new Semaphore(1,true);
   } 
   
   protected UdaSecurityPadlock(Long allowedAccessThread, int semaphoreLimit){
	   this.allowedAccessThread = allowedAccessThread;
	   this.credentialLoad = new Semaphore(semaphoreLimit,true);
   } 
   
   //Getters & Setters
   protected Long getAllowedAccessThread(){
	   return this.allowedAccessThread;
   }
   
   protected void setAllowedAccessThread(Long accessThread){
	   this.allowedAccessThread = accessThread;
   }
   
   //Control functions
   protected boolean allowedAccess(Long accessThread){
	   return this.allowedAccessThread == accessThread;
   }
   
   protected void acquire() throws InterruptedException{
	   this.credentialLoad.acquire();
   }
   
   protected void freeAllThreads(){
	   this.credentialLoad.release(this.credentialLoad.getQueueLength());
   }
   
   protected void release(){
	   this.credentialLoad.release();
   }
   
   protected boolean tryAcquire(){
   		return (this.credentialLoad.tryAcquire());
   }
 }

