/*
* Copyright 2011 E.J.I.E., S.A.
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
package com.ejie.x38.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Bean contenedor de las propiedades que utiliza el componente table.
 * 
 * @author UDA
 *
 * 
 */

@JsonInclude(Include.NON_NULL)
public class PageDto {

	//Identificador primario
	private String id = null;
	//Número de la página
	private Integer page = null;
	//Número de la linea	
	private Integer line = null;

	
	/**
	 * Constructor.
	 */
	public PageDto() {
		super();
	}

	/**
	 * Contructor.
	 * 
	 * @param pagination
	 *            Objeto paginación.
	 * @param recordNum
	 *            Numero de registros.
	 * @param rows
	 *            Lista contenedora de los registros.
	 */
	public PageDto(String id, Integer page, Integer line) {
		this.id = id;
		this.page = page;
		this.line = line;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}
	
}