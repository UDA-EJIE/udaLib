package com.ejie.x38.test.common.model;

public class Provincia {
	private String codigo = null;
	private String nombre = null;
	
	public Provincia(String cod, String nom){
		this.codigo = cod;
		this.nombre = nom;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
