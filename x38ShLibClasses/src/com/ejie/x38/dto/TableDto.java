/*
* Copyright 2011 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, VersiÃ³n 1.1 exclusivamente (la Â«LicenciaÂ»);
* Solo podrÃ¡ usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislaciÃ³n aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye Â«TAL CUALÂ»,
* SIN GARANTÃ�AS NI CONDICIONES DE NINGÃšN TIPO, ni expresas ni implÃ­citas.
* VÃ©ase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Bean contenedor de las propiedades que utiliza el componente table.
 * 
 * @author UDA
 *
 * @param <T> Tipo de bean que se va a representar en el grid.
 */

@JsonInclude(Include.NON_NULL)
public class TableDto<T> {

	//PÃ¡gina actuals
	private String page = null;
	//Datos de la pÃ¡gina
	private List<T> rows = null;
	//NÃºmero total de pÃ¡ginas	
	private String total = null;
	//NÃºmero total de registros
	private Integer records = null;
	// ParÃ¡metros adicionales
	private Map<String,Object> additionalParams = new HashMap<String, Object>();
	
	//Constantes para parÃ¡metros adicionales
	public static final String CHILDREN = "children";
	
	/**
	 * Constructor.
	 */
	public TableDto() {
		super();
	}

	/**
	 * Contructor.
	 * 
	 * @param pagination
	 *            Objeto paginaciÃ³n.
	 * @param recordNum
	 *            Numero de registros.
	 * @param rows
	 *            Lista contenedora de los registros.
	 */
	public TableDto(Pagination pagination, Long recordNum, List<T> rows) {
		this(pagination, recordNum, null, rows);
	}
	
	/**
	 * Contructor.
	 * 
	 * @param pagination
	 *            Objeto paginaciÃ³n.
	 * @param recordNum
	 *            Numero de registros.
	 * @param total
	 *            Numero de resgistros totales.
	 * @param rows
	 *            Lista contenedora de los registros.
	 */
	public TableDto(Pagination pagination, Long recordNum, Long total, List<T> rows) {
		this(pagination, recordNum, total, rows, null);
	}
	
	/**
	 * Contructor.
	 * 
	 * @param pagination
	 *            Objeto paginaciÃ³n.
	 * @param recordNum
	 *            Numero de registros.
	 * @param rows
	 *            Lista contenedora de los registros.
	 * @param reorderedSelection
	 *            Lista con la reordenaciÃ³n de los registros.
	 */
	public TableDto(Pagination pagination, Long recordNum, List<T> rows, List<TableRowDto<T>> reorderedSelection) {
		this(pagination, recordNum, null, rows, reorderedSelection);
	}

	/**
	 * Contructor.
	 * 
	 * @param pagination
	 *            Objeto paginaciÃ³n.
	 * @param recordNum
	 *            Numero de registros.
	 * @param total
	 *            Numero de resgistros totales.
	 * @param rows
	 *            Lista contenedora de los registros.
	 * @param reorderedSelection
	 *            Lista con la reordenaciÃ³n de los registros.
	 */
	public TableDto(Pagination pagination, Long recordNum, Long total, List<T> rows, List<TableRowDto<T>> reorderedSelection) {
		super();
		this.page = (pagination.getPage()!=null)?pagination.getPage().toString():"";
		this.rows = rows;
		this.setTotal(recordNum, (pagination.getRows()!=null)?pagination.getRows():0);
		this.records = total!=null?total.intValue():recordNum.intValue();
		this.addAdditionalParam("reorderedSelection", reorderedSelection);
		this.addAdditionalParam("selectedAll", pagination.getMultiselection().getSelectedAll());
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
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * Calcula el numero total de paginas, segun los registros totales y el numero de regitros pro pÃ¡gina
	 * 
	 * @param total el nÃºmero de registros totales
	 * @param rows el nÃºmero de filas por pagina
	 */
	public void setTotal(Long total, Long rows) {
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
	
	/*
	 * Funciones asociadas a la gestiÃ³n de parÃ¡metros adicionales
	 */
	/**
	 * AÃ±ade un parÃ¡metro adicional.
	 * 
	 * @param key
	 *            Nombre del parÃ¡metro.
	 * @param param
	 *            Objeto a admacenar.
	 */
	public void addAdditionalParam(String key, Object param){
		this.additionalParams.put(key, param);
	}
	
	/**
	 * Recupera un parÃ¡metro adicional.
	 * 
	 * @param key
	 *            Nombre del parÃ¡metro.
	 * @return ParÃ¡metro almacenado.
	 */
	public Object getAdditionalParam(String name) {
		return this.additionalParams.get(name);
	}
	
	/**
	 * Elimina un parÃ¡metro adicional de estructura.
	 * 
	 * @param key
	 *            Nombre del parÃ¡metro.
	 */
	public void removeAdditionalParam(String name) {
		this.additionalParams.remove(name);
	}
	
	/**
	 * Devuelve el mapa que almacena los parÃ¡metros adicionales.
	 * 
	 * @param name
	 * @return
	 */
	@JsonIgnore
	public Map<String,Object> getAdditionalParamsMap() {
		return this.additionalParams;
	}

	/**
	 * MÃ©todo "any getter" necesario para la serializaciÃ³n del contenido del
	 * mapa.
	 * 
	 * @return Mapa que contiene los parÃ¡metros adicionales.
	 */
    @JsonAnyGetter
    public Map<String,Object> anyAdditionalParams() {
        return this.additionalParams;
    }

    /**
	 * MÃ©todo "any setter" necesario para la deserializaciÃ³n del parÃ¡metros al mapa
	 * 
	 * @return Mapa que contiene los parÃ¡metros adicionales.
	 */
    @JsonAnySetter
    public void set(String name, Object value) {
    	this.additionalParams.put(name, value);
    }
	
    /*
	 * Funciones auxiliares
	 */
    public void setReorderedSelection(List<TableRowDto<T>> reorderedSelection){
    	
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
	
//	/**
//	 * Procesa la estructura reorderedSelection para completar los datos a
//	 * partir de la informaciÃ³n de la paginaciÃ³n.
//	 * 
//	 * @param reorderedSelection
//	 *            Lista de registros reordenados.
//	 * @param pagination
//	 *            Objeto paginaciÃ³n.
//	 * @return Lista procesada de elementos reordenados.
//	 */
//	private List<TableRowDto<T>> processReorderedSelection(List<TableRowDto<T>> reorderedSelection, Pagination pagination){
//		if (reorderedSelection != null && pagination != null){
//			List<TableRowDto<T>> proccesedReorderedSelection = new ArrayList<TableRowDto<T>>(reorderedSelection);
//			for (TableRowDto<T> tableRow : proccesedReorderedSelection) {
//				Integer rowNum = tableRow.getTableLine()-1;
//				Long page = (rowNum/pagination.getRows())+1;
//				
//				tableRow.setPage(page.intValue());
//				Long rowLine = tableRow.getTableLine()%pagination.getRows();
//				tableRow.setTableLine(rowLine.intValue());
//			}
//			return proccesedReorderedSelection;
//		}
//		return reorderedSelection;
//	}
}