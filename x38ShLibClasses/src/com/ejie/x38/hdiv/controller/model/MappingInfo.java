package com.ejie.x38.hdiv.controller.model;

import java.util.Set;

public class MappingInfo<T> {

	private final LinkPredicate<T> allowerMethod;

	private final Set<String> mappings;

	MappingInfo(final LinkPredicate<T> allowerMethod, final Set<String> mappings) {
		this.allowerMethod = allowerMethod;
		this.mappings = mappings;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "hiding" })
	public <T> LinkPredicate<T> getAllowerMethod() {
		return (LinkPredicate) allowerMethod;
	}

	public Set<String> getMappings() {
		return mappings;
	}

}
