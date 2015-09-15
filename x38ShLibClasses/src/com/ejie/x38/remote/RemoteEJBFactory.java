package com.ejie.x38.remote;

public interface RemoteEJBFactory {

	public abstract Object lookup(String serverName, Class<?> remoteInterfaceClass) throws Exception;

	public abstract void clearInstance(Class<?> remoteInterfaceClass);

}