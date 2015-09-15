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

import java.util.List;

/**
 * 
 * @author UDA
 *
 */
@Deprecated
public class JQGridJSONModel {

	private String page = null;
	private List<?> rows = null;
	private String total = null;
	private Integer records = null;
	
	public JQGridJSONModel() {
		super();
	}
	public JQGridJSONModel(Pagination pagination, Long recordNum, List<?> rows) {
		super();
		this.page = (pagination.getPage()!=null)?pagination.getPage().toString():"";
		this.rows = rows;
		this.setTotal(recordNum, (pagination.getRows()!=null)?pagination.getRows():0);
		this.records = recordNum.intValue();
	}
	
	public JQGridJSONModel(Pagination pagination, Long recordNum, Long total, List<?> rows) {
		super();
		this.page = (pagination.getPage()!=null)?pagination.getPage().toString():"";
		this.rows = rows;
		this.setTotal(recordNum, (pagination.getRows()!=null)?pagination.getRows():0);
		this.records = total.intValue();
	}
	
	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}
	/**
	 * @param page the page to set
	 */
	public void setPage(String page) {
		this.page = page;
	}
	/**
	 * @return the rows
	 */
	public List<?> getRows() {
		return rows;
	}
	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<?> rows) {
		this.rows = rows;
	}
	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}
	/**
	 * Calcula el numero total de paginas, segun los registros totales y el numero de regitros pro página
	 * @param total el número de registros totales
	 * @param rows el número de filas por pagina
	 */
	public void setTotal(Long total, Long rows) {//String total) {
		double dTotal = total.doubleValue();
		double dRows = rows.doubleValue();
		double totalPages = (total > 0) ? Math.ceil(dTotal / dRows) : 0;
		
		this.total = String.valueOf((int)totalPages);
	}
	/**
	 * @return the records
	 */
	public Integer getRecords() {
		return records;
	}
	/**
	 * @param records the records to set
	 */
	public void setRecords(Integer records) {
		this.records = records;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(this.getClass().getName()).append(" Object {");
		result.append(" [ page: ").append(this.page).append(" ]");
		result.append(" [ rows: ").append(this.rows).append(" ]");
		result.append(" [ total: ").append(this.total).append(" ]");
		result.append(" [ records: ").append(this.records).append(" ]");
		result.append("}");
		return result.toString();
	}
}