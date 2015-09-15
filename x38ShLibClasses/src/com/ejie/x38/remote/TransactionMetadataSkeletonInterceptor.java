package com.ejie.x38.remote;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ejie.x38.util.StackTraceManager;

/**
 * 
 * @author UDA
 *
 * Instala la informacion que contiene el Objeto Metadata en el contexto del servidor.
 */
public class TransactionMetadataSkeletonInterceptor {

	private static final Logger logger = Logger.getLogger(TransactionMetadataSkeletonInterceptor.class);
	
	@AroundInvoke
	public Object manageTransactionMetadata(InvocationContext ic) throws Exception {
		TransactionMetadata txMeta = null;
		try{
			Object[] params = ic.getParameters();
			txMeta = (TransactionMetadata) params[params.length-1];
			txMeta.install();
			logger.log(Level.DEBUG, "Inside Interceptor invoked for "+ ic.getTarget()+"."+ic.getMethod().getName());
			return ic.proceed();
		}catch(Exception e){
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
			throw e;
		} finally{
			logger.log(Level.DEBUG, "Leaving Interceptor invoked for "+ ic.getTarget()+"."+ic.getMethod().getName());
			txMeta.clear();			
		}
	}
}