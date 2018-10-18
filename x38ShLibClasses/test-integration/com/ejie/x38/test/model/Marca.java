/**
 * 
 */
package com.ejie.x38.test.model;

import java.io.Serializable;
import java.util.List;

import com.ejie.x38.serialization.CustomSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author llaparra
 *
 */
public class Marca implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
	private NoraPais pais;
	private List<Empleado> empleados;

	public Marca() {
	}

	/**
	 * @param nombre
	 * @param pais
	 * @param empleados
	 */
	public Marca(String nombre) {
		super();
		this.nombre = nombre;
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
	 * @return the pais
	 */
	@JsonSerialize(using = CustomSerializer.class)
	public NoraPais getPais() {
		return pais;
	}

	/**
	 * @param pais the pais to set
	 */
	public void setPais(NoraPais pais) {
		this.pais = pais;
	}

	/**
	 * @return the empleados
	 */
	public List<Empleado> getEmpleados() {
		return empleados;
	}

	/**
	 * @param empleados the empleados to set
	 */
	public void setEmpleados(List<Empleado> empleados) {
		this.empleados = empleados;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Marca [nombre=").append(nombre).append(", pais=").append(pais).append(", empleados=")
				.append(empleados).append("]");
		return builder.toString();
	}
}
