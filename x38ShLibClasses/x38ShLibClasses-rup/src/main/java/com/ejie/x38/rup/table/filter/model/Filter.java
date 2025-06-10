package com.ejie.x38.rup.table.filter.model;

import com.ejie.x38.generic.model.SelectGeneric;

public class Filter extends SelectGeneric implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String selector;
	private String user;
	private boolean active;
	private String feedback;

	public Filter() {
		super();
	}

	public Filter(String id, String text, String data, String selector, String user, boolean active, String feedback) {
		super(id, text, data, null, null);
		this.selector = selector;
		this.user = user;
		this.active = active;
		this.feedback = feedback;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(this.getClass().getName()).append(" Object {");
		result.append(" [ id: ").append(this.getId()).append(" ]");
		result.append(" [ text: ").append(this.getText()).append(" ]");
		result.append(" [ data: ").append(this.getData()).append(" ]");
		result.append(" [ selector: ").append(this.selector).append(" ]");
		result.append(" [ user: ").append(this.user).append(" ]");
		result.append(" [ active: ").append(this.active).append(" ]");
		result.append(" [ feedback: ").append(this.feedback).append(" ]");
		result.append("}");
		return result.toString();
	}

}