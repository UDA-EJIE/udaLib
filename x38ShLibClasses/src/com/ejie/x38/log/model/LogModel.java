package com.ejie.x38.log.model;

import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.TrustAssertion;

public class LogModel implements java.io.Serializable, SecureIdContainer {
	private static final long serialVersionUID = 1L;
	
	@TrustAssertion(idFor = LogModel.class)
	private String nameLog; 
	private String levelLog;
//	private String nameEscape;
	
	public String getNameLog() {
		return nameLog;
	}

	public void setNameLog(String name) {
		this.nameLog = name;
	}

	public String getLevelLog() {
		return levelLog;
	}

	public void setLevelLog(String level) {
		this.levelLog = level;
	}

//	public String getNameEscape() {
//		return nameEscape;
//	}
//
//	public void setNameEscape(String nameEscape) {
//		this.nameEscape = nameEscape;
//	}	 
}
