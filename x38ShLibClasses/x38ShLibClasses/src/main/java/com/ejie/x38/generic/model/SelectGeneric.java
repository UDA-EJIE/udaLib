package com.ejie.x38.generic.model;

import java.util.ArrayList;

/** Entidad que permite construir la estructura necesaria para el componente select.
 * @since 5.2.0
*/
public class SelectGeneric implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String text;
	private String data;
	private ArrayList<Object> children;
	private String style;
	
	public SelectGeneric() {
		super();
	}

	public SelectGeneric(String value, String label) {
		super();
		this.id = value;
		this.text = label;
	}

	public SelectGeneric(String value, String label, ArrayList<Object> children) {
		super();
		this.id = value;
		this.text = label;
		this.children = children;
	}

	public SelectGeneric(String value, String label, String style) {
		super();
		this.id = value;
		this.text = label;
		this.style = style;
	}

	public SelectGeneric(String value, String label, ArrayList<Object> children, String style) {
		super();
		this.id = value;
		this.text = label;
		this.children = children;
		this.style = style;
	}

	public SelectGeneric(String value, String label, String data, ArrayList<Object> children, String style) {
		super();
		this.id = value;
		this.text = label;
		this.data = data;
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
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
