package com.ejie.x38.dto;

/** Entidad que permite construir la estructura necesaria para los componentes autocomplete y combo.
 * @since 4.5.0
 * 
*/
public class SelectGenericPOJO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String value;
	private String label;
	private String style;
	
	public SelectGenericPOJO() {
		super();
	}

	public SelectGenericPOJO(String value, String label) {
		super();
		this.value = value;
		this.label = label;
	}

	public SelectGenericPOJO(String value, String label, String style) {
		super();
		this.value = value;
		this.label = label;
		this.style = style;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}
}