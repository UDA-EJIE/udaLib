package com.ejie.x38.test.model;

public class Usuario {
	private String codigo = null;
	private String nombre = null;
	
	public Usuario(String cod, String nom){
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
