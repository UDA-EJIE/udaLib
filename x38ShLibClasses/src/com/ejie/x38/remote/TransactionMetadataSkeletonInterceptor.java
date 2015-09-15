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

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejie.x38.util.StackTraceManager;

/**
 * 
 * Instala la informacion que contiene el Objeto Metadata en el contexto del servidor.
 *
 * @author UDA
 * 
 */
public class TransactionMetadataSkeletonInterceptor {

	private static final Logger logger =  LoggerFactory.getLogger(TransactionMetadataSkeletonInterceptor.class);
	
	@AroundInvoke
	public Object manageTransactionMetadata(InvocationContext ic) throws Exception {
		TransactionMetadata txMeta = null;
		try{
			Object[] params = ic.getParameters();
			txMeta = (TransactionMetadata) params[params.length-1];
			txMeta.install();
			logger.info("Inside Interceptor invoked for "+ ic.getTarget()+"."+ic.getMethod().getName());
			return ic.proceed();
		}catch(Exception e){
			logger.error(StackTraceManager.getStackTrace(e));
			throw e;
		} finally{
			logger.info("Leaving Interceptor invoked for "+ ic.getTarget()+"."+ic.getMethod().getName());
			txMeta.clear();			
		}
	}
}