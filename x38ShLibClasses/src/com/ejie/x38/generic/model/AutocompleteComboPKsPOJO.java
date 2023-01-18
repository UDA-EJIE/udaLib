package com.ejie.x38.generic.model;

import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.TrustAssertion;

/** Entidad que permite construir la estructura necesaria para los componentes autocomplete y combo. Se diferencia de la entidad AutocompleteComboGenericPOJO en que esta cifrará el campo value, permitiendo su uso sobre campos que sean clave primaria.
 * @since 5.0.0
 * @deprecated
*/
public class AutocompleteComboPKsPOJO implements java.io.Serializable, SecureIdContainer {
	private static final long serialVersionUID = 1L;
	
	@TrustAssertion(idFor = AutocompleteComboPKsPOJO.class)
	private String value;
	private String label;
	private String style;
	
	public AutocompleteComboPKsPOJO() {
		super();
	}

	public AutocompleteComboPKsPOJO(String value, String label) {
		super();
		this.value = value;
		this.label = label;
	}

	public AutocompleteComboPKsPOJO(String value, String label, String style) {
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
