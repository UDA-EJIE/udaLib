package com.ejie.x38.log.model;

import org.apache.commons.lang.ObjectUtils;
import com.ejie.hdiv.services.SecureIdContainer;
import com.ejie.hdiv.services.TrustAssertion;

public class LogModel implements java.io.Serializable, SecureIdContainer {
	private static final long serialVersionUID = 1L;
	
	@TrustAssertion(idFor = LogModel.class)
	private String nameLog; 
	private String levelLog;
	private String nameEscape;
	
	public LogModel() {
		super();
	}

	public LogModel(String nameLog) {
		super();
		this.nameLog = nameLog;
	}

	public LogModel(String nameLog, String levelLog) {
		super();
		this.nameLog = nameLog;
		this.levelLog = levelLog;
	}

	public LogModel(String nameLog, String levelLog, String nameEscape) {
		super();
		this.nameLog = nameLog;
		this.levelLog = levelLog;
		this.nameEscape = nameEscape;
	}
	
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

	public String getNameEscape() {
		return nameEscape;
	}

	public void setNameEscape(String nameEscape) {
		this.nameEscape = nameEscape;
	}
	
	public boolean compare(LogModel log2) {
		return (ObjectUtils.equals(this.nameLog, log2.getNameLog()) && ObjectUtils.equals(this.levelLog, log2.getLevelLog()) && ObjectUtils.equals(this.nameEscape, log2.getNameEscape()));
	}
}
