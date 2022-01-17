package com.ejie.x38.hdiv.util;

import java.util.ArrayList;
import java.util.List;

import com.ejie.x38.hdiv.controller.model.IdentifiableModelWrapper;
import com.ejie.x38.hdiv.controller.model.IdentifiableModelWrapperImpl;

public class IdentifiableModelWrapperFactory<T> {

	public static <T> List<IdentifiableModelWrapper<T>> getInstance(List<T> entities) {
		return getInstance(entities, "getId");
	}
	
	public static <T> List<IdentifiableModelWrapper<T>> getInstance(List<T> entities, String paramName) {
		List<IdentifiableModelWrapper<T>> wrapper = new ArrayList<IdentifiableModelWrapper<T>>();
		for(T entity : entities) {
			wrapper.add(new IdentifiableModelWrapperImpl<T>(entity, paramName));
		}
		return wrapper;
	}
	
	public static <T> IdentifiableModelWrapper<T> getInstance(T entity) {
		return getInstance(entity, "getId");
	}
	
	public static <T> IdentifiableModelWrapper<T> getInstance(T entity, String paramName) {
		return new IdentifiableModelWrapperImpl<T>(entity, paramName);
	}
}
