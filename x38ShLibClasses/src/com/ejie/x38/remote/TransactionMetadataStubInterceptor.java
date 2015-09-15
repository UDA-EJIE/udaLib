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
 * Registra la informacion relativa al tiempo que dura la llamada remota en el sistema de Logging.
 */
public class TransactionMetadataStubInterceptor {

	private static final Logger logger = Logger.getLogger(TransactionMetadataStubInterceptor.class);

	@AroundInvoke
	public Object manageTransactionMetadata(InvocationContext ic) throws Exception {
		long start = System.currentTimeMillis();
		try {
			logger.log(Level.DEBUG, "Intercepting "+ ic.getTarget()+"."+ic.getMethod().getName());
			String params = (String) ic.getParameters().toString();
			if (params == null) ic.setParameters(new String[] { "default" });
			return ic.proceed();
		} catch (Exception e) {
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
			throw e;
		} finally {
			long time = System.currentTimeMillis() - start;
			logger.log(Level.DEBUG, "Invocation of transaction " +ic.getTarget()+"."+ic.getMethod().getName()+ " took " + time + "ms");
		}
	}
}