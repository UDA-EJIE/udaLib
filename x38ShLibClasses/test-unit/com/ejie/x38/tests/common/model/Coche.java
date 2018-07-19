/**
 * 
 */
package com.ejie.x38.tests.common.model;

import java.io.Serializable;

/**
 * @author llaparra
 *
 */
public class Coche implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String modelo;
	private Marca marca;
	private Integer numPuertas;
	private String tipoMotor;

	/**
	 * @param modelo
	 * @param marca
	 * @param numPuertas
	 * @param tipoMotor
	 */
	public Coche(String modelo, Marca marca, Integer numPuertas, String tipoMotor) {
		super();
		this.modelo = modelo;
		this.marca = marca;
		this.numPuertas = numPuertas;
		this.tipoMotor = tipoMotor;
	}

	/**
	 * @return the modelo
	 */
	public String getModelo() {
		return modelo;
	}

	/**
	 * @param modelo the modelo to set
	 */
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	/**
	 * @return the marca
	 */
	public Marca getMarca() {
		return marca;
	}

	/**
	 * @param marca the marca to set
	 */
	public void setMarca(Marca marca) {
		this.marca = marca;
	}

	/**
	 * @return the numPuertas
	 */
	public Integer getNumPuertas() {
		return numPuertas;
	}

	/**
	 * @param numPuertas the numPuertas to set
	 */
	public void setNumPuertas(Integer numPuertas) {
		this.numPuertas = numPuertas;
	}

	/**
	 * @return the tipoMotor
	 */
	public String getTipoMotor() {
		return tipoMotor;
	}

	/**
	 * @param tipoMotor the tipoMotor to set
	 */
	public void setTipoMotor(String tipoMotor) {
		this.tipoMotor = tipoMotor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Coche [modelo=").append(modelo).append(", marca=").append(marca).append(", numPuertas=")
				.append(numPuertas).append(", tipoMotor=").append(tipoMotor).append("]");
		return builder.toString();
	}
}
