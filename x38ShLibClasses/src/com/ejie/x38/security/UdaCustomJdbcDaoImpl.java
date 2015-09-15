/*
* Copyright 2012 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.security;

import java.util.Vector;

import n38c.exe.N38API;

import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;


/**
 * 
 * @author UDA
 *
 */
public abstract class UdaCustomJdbcDaoImpl extends JdbcDaoImpl {
	
	private String positionByUserdataQuery = null;
	private String authoritiesByUserdataQuery = null;
	
	public UdaCustomJdbcDaoImpl(){
		super();
	}
		
	abstract protected String loadUserPosition(String userName, String dni, N38API n38Api);
	
	abstract protected Vector<String> loadUserAuthorities(String userName, String dni, N38API n38Api);
	
	
	//Getters & Setters
	
	protected String getPositionByUserdataQuery() {
		return this.positionByUserdataQuery;
	}
		
	public void setPositionByUserdataQuery(String positionByUserdataQuery)
	{
		this.positionByUserdataQuery = positionByUserdataQuery;
	}	
	
	protected String getAuthoritiesByUserdataQuery() {
		return this.authoritiesByUserdataQuery;
	}
		
	public void setAuthoritiesByUserdataQuery(String authoritiesByUserdataQuery)
	{
		this.authoritiesByUserdataQuery = authoritiesByUserdataQuery;
	}	
}