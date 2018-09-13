/**
 * 
 */
package com.ejie.x38.test.common.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.ejie.x38.serialization.JsonBigDecimalDeserializer;
import com.ejie.x38.serialization.JsonBigDecimalSerializer;
import com.ejie.x38.serialization.JsonDateTimeDeserializer;
import com.ejie.x38.serialization.JsonDateTimeSerializer;
import com.ejie.x38.serialization.JsonNumberDeserializer;
import com.ejie.x38.serialization.JsonNumberSerializer;
import com.ejie.x38.serialization.JsonTimeDeserializer;
import com.ejie.x38.serialization.JsonTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
	private Timestamp tiempoConstruccion;
	private BigDecimal coste;
	private BigDecimal precio;

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
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	public Date getFechaConstruccion() {
		return fechaConstruccion;
	}

	/**
	 * @param fechaConstruccion the fechaConstruccion to set
	 */
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	public void setFechaConstruccion(Date fechaConstruccion) {
		this.fechaConstruccion = fechaConstruccion;
	}

	/**
	 * @return the coste
	 */
	@JsonSerialize(using = JsonBigDecimalSerializer.class)
	public BigDecimal getCoste() {
		return coste;
	}

	/**
	 * @param coste the coste to set
	 */
	@JsonDeserialize(using = JsonBigDecimalDeserializer.class)
	public void setCoste(BigDecimal coste) {
		this.coste = coste;
	}

	/**
	 * @return the tiempoConstruccion
	 */
	@JsonSerialize(using = JsonTimeSerializer.class)
	public Timestamp getTiempoConstruccion() {
		return tiempoConstruccion;
	}

	/**
	 * @param tiempoConstruccion the tiempoConstruccion to set
	 */
	@JsonDeserialize(using = JsonTimeDeserializer.class)
	public void setTiempoConstruccion(Timestamp tiempoConstruccion) {
		this.tiempoConstruccion = tiempoConstruccion;
	}

	/**
	 * @return the precio
	 */
	@JsonSerialize(using = JsonNumberSerializer.class)
	public BigDecimal getPrecio() {
		return precio;
	}

	/**
	 * @param precio the precio to set
	 */
	@JsonDeserialize(using = JsonNumberDeserializer.class)
	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
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
