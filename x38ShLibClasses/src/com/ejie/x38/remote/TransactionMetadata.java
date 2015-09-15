package com.ejie.x38.remote;

import java.io.Serializable;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ejie.x38.util.ThreadStorageManager;

/**
 * 
 * @author UDA
 *
 * Metadata DTO.
 * 
 */

public class TransactionMetadata implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long uniqueId;
	
	private SecurityContext securityContext;
	
	public TransactionMetadata(){
		this.setUniqueId(ThreadStorageManager.getCurrentThreadId());
		this.setSecurityContext(SecurityContextHolder.getContext());
	}

	public Long getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Long uniqueId) {
		this.uniqueId = uniqueId;
	}

	public SecurityContext getSecurityContext() {
		return securityContext;
	}

	public void setSecurityContext(SecurityContext securityContext) {
		this.securityContext = securityContext;
	}
	
	public void install (){
		if(this.getUniqueId()!=null)ThreadStorageManager.setCurrentThreadId(this.getUniqueId());
		if(this.getSecurityContext()!=null)SecurityContextHolder.setContext(this.getSecurityContext());
	}
	
	public void clear (){
		ThreadStorageManager.clearCurrentThreadId();
		SecurityContextHolder.clearContext();
	}
}