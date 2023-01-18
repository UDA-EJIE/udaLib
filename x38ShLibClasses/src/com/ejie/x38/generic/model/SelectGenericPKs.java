package com.ejie.x38.generic.model;

import java.util.ArrayList;

import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.TrustAssertion;

/** Entidad que permite construir la estructura necesaria para el componente select.
 * @since 5.2.0
*/
public class SelectGenericPKs implements java.io.Serializable, SecureIdContainer {
	private static final long serialVersionUID = 1L;
	
	@TrustAssertion(idFor = SelectGenericPKs.class)
	private String id;
	private String text;
	private ArrayList<Object> children;
	private String style;
	
	public SelectGenericPKs() {
		super();
	}

	public SelectGenericPKs(String value, String label) {
		super();
		this.id = value;
		this.text = label;
	}

	public SelectGenericPKs(String value, String label, ArrayList<Object> children) {
		super();
		this.id = value;
		this.text = label;
		this.children = children;
	}

	public SelectGenericPKs(String value, String label, String style) {
		super();
		this.id = value;
		this.text = label;
		this.style = style;
	}

	public SelectGenericPKs(String value, String label, ArrayList<Object> children, String style) {
		super();
		this.id = value;
		this.text = label;
		this.children = children;
		this.style = style;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ArrayList<Object> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Object> children) {
		this.children = children;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
}
