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

/**
 * 
 * @author UDA
 *
 */
public class JQGrid extends Pagination implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String _search;
	private String nd;
	
	/**
	 * @return the _search
	 */
	public String get_search() {
		return _search;
	}

	/**
	 * @param _search the _search to set
	 */
	public void set_search(String _search) {
		this._search = _search;
	}

	/**
	 * @return the nd
	 */
	public String getNd() {
		return nd;
	}

	/**
	 * @param nd the nd to set
	 */
	public void setNd(String nd) {
		this.nd = nd;
	}

	public JQGrid(){}
	
	public JQGrid(String _search, String nd, String rows, Long page, String ascDsc, String sort) {
		super(new Long(rows), page, ascDsc, sort);
		this._search = _search;
		this.nd = nd;		
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		result.append(this.getClass().getName() + " Object {" + newLine);
		result.append(" _search: " + this._search + newLine);
		result.append(" nd: " + this.nd + newLine);
		result.append("}");
		return result.toString();
	}
}