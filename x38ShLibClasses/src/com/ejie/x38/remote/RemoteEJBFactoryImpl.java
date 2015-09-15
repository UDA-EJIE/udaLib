package com.ejie.x38.remote;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import weblogic.jndi.Environment;

import com.ejie.x38.util.StackTraceManager;

/**
 * 
 * @author UDA
 * 
 * This class is used for caching references to remote EJBs' Contexts and Remote interfaces.
 * This classes is being mainly consumed by a 3.0 Stateless Enterprise Java Bean.
 * Because EJB apps tend to run multiple beans, and because JNDI lookups are often present in many 
 * components, much of an application's performance overhead can be spent on these lookups.
 * 
 * Here, the main purpose is to avoid those overheads by caching Object Instances.
 *
 */
public class RemoteEJBFactoryImpl implements RemoteEJBFactory {

	private static final Logger logger = Logger.getLogger(RemoteEJBFactoryImpl.class);	
	private Map<Class<?>, Object> remoteInterfaces;
	private Properties appConfiguration;

    private RemoteEJBFactoryImpl() {
    	remoteInterfaces = new HashMap<Class<?>, Object>();
    }

    @Override
	public Object lookup(String serverName, Class<?> remoteInterfaceClass) throws Exception {
        // See if we already have this interface cached
    	String jndiName = appConfiguration.getProperty(serverName+"."+remoteInterfaceClass.getSimpleName()+".jndi");
    	logger.log(Level.DEBUG, "Looking Up EJB with JNDI Name: "+jndiName);
        Object remoteInterface = (Object)remoteInterfaces.get(remoteInterfaceClass);
        // If not, look up with the supplied JNDI name
        if (remoteInterface == null) {
        	remoteInterface = this.getInitialContext(serverName).lookup(jndiName);
            // If this is a new ref, save for caching purposes
            remoteInterfaces.put(remoteInterfaceClass, remoteInterface);
        }
        return remoteInterface;
    }
      
  	private Context getInitialContext(String serverName){
		String url = appConfiguration.getProperty(serverName+".url");
		String user = appConfiguration.getProperty(serverName+".user");
		String password = appConfiguration.getProperty(serverName+".password");
		Hashtable<String,String> ht = new Hashtable<String,String>();
		try {
			ht.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
			ht.put(Context.PROVIDER_URL,url);
			if (user != null) {
				ht.put(Context.SECURITY_PRINCIPAL, user);
				ht.put(Context.SECURITY_CREDENTIALS, password == null ? "" : password);
			}
			Environment env = new Environment();
			env.setDelegateEnvironment(ht);
			return env.getInitialContext();
		}catch(Exception e){
			logger.log(Level.ERROR, "Failed to initialize InitialContext with error: "+StackTraceManager.getStackTrace(e));
			return null;
		}
	}

  	@Override
	public void clearInstance (Class<?> remoteInterfaceClass){
  		remoteInterfaces.remove(remoteInterfaceClass);
  	}

	public Properties getAppConfiguration() {
		return appConfiguration;
	}

	public void setAppConfiguration(Properties appConfiguration) {
		this.appConfiguration = appConfiguration;
	}
}