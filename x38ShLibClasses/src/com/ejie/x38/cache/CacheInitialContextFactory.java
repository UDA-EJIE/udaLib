package com.ejie.x38.cache;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * 
 * @author UDA
 *
 */
public class CacheInitialContextFactory  implements InitialContextFactory {

	/**
	 * Creates an initial context with {@inheritDoc}
	 */
	@Override
	public Context getInitialContext(Hashtable<?,?> environment)
			throws NamingException {

		Context ctx = new InitialContext();
		return ctx;
	}
}
