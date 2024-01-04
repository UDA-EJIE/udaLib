package com.ejie.x38.hdiv.controller.model;

import java.util.List;

public class ReferencedObject {

	private final String ref;
	
	private final Object entity;
	
	private final List<SecureClassInfo> secureClassInfo;

	public ReferencedObject(String ref, Object entity, List<SecureClassInfo> secureClassInfo) {
		this.ref = ref;
		this.entity = entity;
		this.secureClassInfo = secureClassInfo;
	}

	public String getRef() {
		return ref;
	}

	public Object getEntity() {
		return entity;
	}
	
	public List<SecureClassInfo> getSecureClassInfo() {
		return secureClassInfo;
	}
	
}
