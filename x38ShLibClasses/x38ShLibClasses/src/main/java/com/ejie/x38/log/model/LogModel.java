package com.ejie.x38.log.model;

import java.io.Serializable;

public class LogModel implements Serializable {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nameLog;
	 
	 private String levelLog;
//	 private String nameEscape;
	 
	 

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
