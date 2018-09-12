/**
 * 
 */
package com.ejie.x38.test.common.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author llaparra
 *
 */
public class Coche implements Serializable {
	private static final long serialVersionUID = 1L;
	private String modelo;
	private Marca marca;
	private Integer numPuertas;
	private String tipoMotor;
	private Date fechaConstruccion;
	private BigDecimal coste;

	public Coche() {
	}

	/**
	 * @param modelo
	 * @param marca
	 * @param numPuertas
	 * @param tipoMotor
	 */
	public Coche(String modelo, Marca marca) {
		super();
		this.modelo = modelo;
		this.marca = marca;
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

	/**
	 * @return the fechaConstruccion
	 */
	public Date getFechaConstruccion() {
		return fechaConstruccion;
	}

	/**
	 * @param fechaConstruccion the fechaConstruccion to set
	 */
	public void setFechaConstruccion(Date fechaConstruccion) {
		this.fechaConstruccion = fechaConstruccion;
	}

	/**
	 * @return the coste
	 */
	public BigDecimal getCoste() {
		return coste;
	}

	/**
	 * @param coste the coste to set
	 */
	public void setCoste(BigDecimal coste) {
		this.coste = coste;
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
				.append(numPuertas).append(", tipoMotor=").append(tipoMotor).append(", fechaConstruccion=")
				.append(fechaConstruccion).append(", coste=").append(coste).append("]");
		return builder.toString();
	}

}
