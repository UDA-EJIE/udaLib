package com.ejie.x38.test.common.model;

import org.hibernate.validator.constraints.NotEmpty;

public class UploadBean {
	@NotEmpty(message="validacion.required")
	private String apellido1;
	@NotEmpty(message="validacion.required")
	private String apellido2;
	private byte[] fotoPadre;
	private String nombreFotoPadre;
	private byte[] fotoMadre;
	private String nombreFotoMadre;

	public UploadBean() {
		super();
	}
	
	

	public UploadBean(String apellido1, String apellido2, byte[] fotoPadre,
			String nombreFotoPadre, byte[] fotoMadre, String nombreFotoMadre) {
		super();
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.fotoPadre = fotoPadre;
		this.nombreFotoPadre = nombreFotoPadre;
		this.fotoMadre = fotoMadre;
		this.nombreFotoMadre = nombreFotoMadre;
	}



	public String getApellido1() {
		return apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2() {
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public byte[] getFotoPadre() {
		return fotoPadre;
	}

	public void setFotoPadre(byte[] fotoPadre) {
		this.fotoPadre = fotoPadre;
	}

	public String getNombreFotoPadre() {
		return nombreFotoPadre;
	}

	public void setNombreFotoPadre(String nombreFotoPadre) {
		this.nombreFotoPadre = nombreFotoPadre;
	}

	public byte[] getFotoMadre() {
		return fotoMadre;
	}

	public void setFotoMadre(byte[] fotoMadre) {
		this.fotoMadre = fotoMadre;
	}

	public String getNombreFotoMadre() {
		return nombreFotoMadre;
	}

	public void setNombreFotoMadre(String nombreFotoMadre) {
		this.nombreFotoMadre = nombreFotoMadre;
	}

}