package com.ejie.x38.hdiv.controller.model;

public interface IdentifiableModelWrapper<T> {

	public String getId() throws Exception;
	public T getEntity();
	public Object getNid() throws Exception;
	public String getIdentifiableParamName();
	public Class<?> getTarget();
}
