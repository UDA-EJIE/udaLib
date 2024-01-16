package com.ejie.x38.hdiv.controller.model;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejie.x38.hdiv.serialization.EjieSecureModule;

public class IdentifiableModelWrapperImpl<T> implements IdentifiableModelWrapper<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IdentifiableModelWrapperImpl.class);
	private static final String GET = "get";
	private T entity;
	
	private Method getId;
	
	private String identifiableParamName;

	private Class<?> target;
	
	public IdentifiableModelWrapperImpl() {
	}
	
	public IdentifiableModelWrapperImpl(T entity) {
		this(entity, "id");
	}
	
	public IdentifiableModelWrapperImpl(T entity, String paramName) {
		this.identifiableParamName = paramName;
		try {
			getId = entity.getClass().getMethod(GET+paramName.substring(0, 1).toUpperCase() + paramName.substring(1));
		}
		catch (Exception e) {
			throw new RuntimeException("Cannot obtain get method of param " + paramName + " from entity");
		}
		this.entity = entity;
	}
	
	public void setEntity(T entity) {
		this.entity = entity;
	}

	public void setGetId(Method getId) {
		this.getId = getId;
	}
	
	public void setIdentifiableParamName(String identifiableParamName) {
		this.identifiableParamName = identifiableParamName;
	}
	
	public void setTarget(Class<?> target) {
		this.target = target;
	}
	
	public IdentifiableModelWrapper<T> target(Class<?> target) {
		try {// se añade el target, para caos sacados del controller directamente
			EjieSecureModule.addSecureId(target, getId());
		} catch (Exception e) {
			LOGGER.error("Error add secure Id : " + target);
		}
		this.target = target;
		return this;
	}

	@Override
	public String getId() throws Exception {return String.valueOf(getId.invoke(entity));}
	
	@Override
	public T getEntity() {return entity;}

	@Override
	public Object getNid() throws Exception {
		return getId.invoke(entity);
	}

	@Override
	public String getIdentifiableParamName() {
		return identifiableParamName;
	}

	@Override
	public Class<?> getTarget() {
		if(target == null) {
			target = entity.getClass();
		}
		return target;
	}
	
}
