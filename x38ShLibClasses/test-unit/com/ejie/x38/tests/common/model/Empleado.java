/**
 * 
 */
package com.ejie.x38.tests.common.model;

import java.io.Serializable;

/**
 * @author llaparra
 *
 */
public class Empleado implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nombre;
	private String apellido1;
	private String apellido2;

	/**
	 * @param nombre
	 * @param apellido1
	 * @param apellido2
	 */
	public Empleado(String nombre, String apellido1, String apellido2) {
		super();
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the apellido1
	 */
	public String getApellido1() {
		return apellido1;
	}

	/**
	 * @param apellido1 the apellido1 to set
	 */
	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	/**
	 * @return the apellido2
	 */
	public String getApellido2() {
		return apellido2;
	}

	/**
	 * @param apellido2 the apellido2 to set
	 */
	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Empleado [nombre=").append(nombre).append(", apellido1=").append(apellido1)
				.append(", apellido2=").append(apellido2).append("]");
		return builder.toString();
	}
}
