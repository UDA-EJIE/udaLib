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
 * Registra la informacion relativa al tiempo que dura la llamada remota en el sistema de Logging.
 * 
 * @author UDA
 *
 */
public class TransactionMetadataStubInterceptor {

	private static final Logger logger =  LoggerFactory.getLogger(TransactionMetadataStubInterceptor.class);

	@AroundInvoke
	public Object manageTransactionMetadata(InvocationContext ic) throws Exception {
		long start = System.currentTimeMillis();
		try {
			logger.debug("Intercepting "+ ic.getTarget()+"."+ic.getMethod().getName());
			String params = (String) ic.getParameters().toString();
			if (params == null) ic.setParameters(new String[] { "default" });
			return ic.proceed();
		} catch (Exception e) {
			logger.error(StackTraceManager.getStackTrace(e));
			throw e;
		} finally {
			long time = System.currentTimeMillis() - start;
			logger.debug("Invocation of transaction " +ic.getTarget()+"."+ic.getMethod().getName()+ " took " + time + "ms");
		}
	}
}