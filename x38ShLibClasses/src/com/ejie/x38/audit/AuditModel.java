package com.ejie.x38.audit;

import java.sql.Timestamp;

public class AuditModel {

	// Url desde la que se llama a la auditoría
	String url;
	//La versión de RUP utilizada
	String versionRup;
	//Nombre del componente RUP auditado
	String nombreComponente;
	//Código identificador de la aplicación auditada
	String codApp;
	//Timestamp del momento que se realiza la auditoría
	Timestamp timeStamp;
	//Qué se audita (_init, getRupValue,...)
	String auditing;
	
	/**
	 * Devuelve la Url desde la que se llama a la auditoría
	 * @return String url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Devuelve laversión de RUP utilizada
	 * @return String rupVersion
	 */
	public String getVersionRup() {
		return versionRup;
	}
	
	/**
	 * Devuelve el nombre del componente RUP auditado
	 * @return String nombreComponente
	 */
	public String getNombreComponente() {
		return nombreComponente;
	}
	
	/**
	 * Devuelve el código identificador de la aplicación auditada
	 * @return String codApp
	 */
	public String getCodApp() {
		return codApp;
	}
	
	/**
	 * Devuelve el timestamp del momento que se realiza la auditoría
	 * @return Timestamp timestamp
	 */
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * Devuelve el nombre de lo que se audita
	 * @return String auditado
	 */
	public String getAuditing() {
		return auditing;
	}
	
	
}
