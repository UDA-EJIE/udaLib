package com.ejie.x38.hdiv.controller.model;

import java.util.Set;

public class MappingInfo<T> {

	private final LinkPredicate<T> allowerMethod;

	private final Set<String> mappings;
	
	private final Set<String> noEntityParams;

	MappingInfo(final LinkPredicate<T> allowerMethod, final Set<String> mappings, final Set<String> noEntityParams) {
		this.allowerMethod = allowerMethod;
		this.mappings = mappings;
		this.noEntityParams = noEntityParams;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "hiding" })
	public <T> LinkPredicate<T> getAllowerMethod() {
		return (LinkPredicate) allowerMethod;
	}

	public Set<String> getMappings() {
		return mappings;
	}
	
	public boolean isNotEntityParam(String paramName) {
		return noEntityParams.contains(paramName);
	}

}
