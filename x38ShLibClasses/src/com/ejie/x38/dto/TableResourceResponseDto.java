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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.services.SecureIdContainer;
import org.springframework.hateoas.Resource;

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
 * @param <T> Tipo de bean que se va a representar en el table.
 */
@JsonInclude(Include.NON_NULL)
public class TableResourceResponseDto<T> implements SecureIdContainer {

	// Página actual
	private String page = null;

	// Datos de la página
	private List<Resource<T>> rows = null;

	// Número total de páginas
	private String total = null;

	// Número total de registros
	private Integer records = null;

	// Parámetros adicionales
	private final Map<String, Object> additionalParams = new HashMap<String, Object>();

	// Constantes para parámetros adicionales
	public static final String CHILDREN = "children";
	
	public static final String REORDER_SELECTION_KEY = "reorderedSelection";

	/**
	 * Constructor.
	 */
	public TableResourceResponseDto() {
		super();
	}

	/**
	 * Contructor.
	 * 
	 * @param pagination Objeto paginación.
	 * @param recordNum Numero de registros.
	 * @param rows Lista contenedora de los registros.
	 */
	public <U> TableResourceResponseDto(final TableRequestDto tableRequestDto, final Long recordNum, final List<T> rows) {
		this(tableRequestDto, recordNum, null, rows);
	}

	/**
	 * Contructor.
	 * 
	 * @param pagination Objeto paginación.
	 * @param recordNum Numero de registros.
	 * @param total Numero de resgistros totales.
	 * @param rows Lista contenedora de los registros.
	 */
	public <U> TableResourceResponseDto(final TableRequestDto tableRequestDto, final Long recordNum, final Long total, final List<T> rows) {
		this(tableRequestDto, recordNum, total, rows, null);
	}

	/**
	 * Contructor.
	 * 
	 * @param pagination Objeto paginación.
	 * @param recordNum Numero de registros.
	 * @param rows Lista contenedora de los registros.
	 * @param reorderedSelection Lista con la reordenación de los registros.
	 */
	public <U> TableResourceResponseDto(final TableRequestDto tableRequestDto, final Long recordNum, final List<T> rows,
			final List<TableRowDto<T>> reorderedSelection) {
		this(tableRequestDto, recordNum, null, rows, reorderedSelection);
	}

	/**
	 * Contructor.
	 * 
	 * @param pagination Objeto paginación.
	 * @param recordNum Numero de registros.
	 * @param total Numero de resgistros totales.
	 * @param rows Lista contenedora de los registros.
	 * @param reorderedSelection Lista con la reordenación de los registros.
	 */
	public <U> TableResourceResponseDto(final TableRequestDto tableRequestDto, final Long recordNum, final Long total, final List<T> rows,
			final List<TableRowDto<T>> reorderedSelection) {
		super();
		this.page = (tableRequestDto.getPage() != null) ? tableRequestDto.getPage().toString() : "";
		this.rows = fromListToResource(rows);
		this.setTotal(recordNum, (tableRequestDto.getRows() != null) ? tableRequestDto.getRows() : 0);
		this.records = total != null ? total.intValue() : recordNum.intValue();
		this.addAdditionalParam(REORDER_SELECTION_KEY, wrapReorderSelection(reorderedSelection));
		this.addAdditionalParam("selectedAll", tableRequestDto.getMultiselection().getSelectedAll());
	}
	
	private List<TableRowDto<Resource<T>>> wrapReorderSelection(List<TableRowDto<T>> reorderedSelection){
		
		if(reorderedSelection == null) {
			return null;
		}
		List<TableRowDto<Resource<T>>> wraped = new ArrayList<TableRowDto<Resource<T>>>();
		
		for(TableRowDto<T> tr : reorderedSelection) {
			wraped.add(new TableRowDto<Resource<T>>(tr.getPkMap(), tr.getPage(), tr.getPageLine(), tr.getTableLine(), new Resource<T>(tr.getModel())));
		}
		return wraped;
	}
	
	private List<Resource<T>> fromListToResource(List<T> list){
		List<Resource<T>> resources = new ArrayList<Resource<T>>();
		for(T object : list) {
			resources.add(new Resource<T>(object));
		}
		return resources;
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
	public void setPage(final String page) {
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
	public void setRows(final List<T> rows) {
		this.rows = fromListToResource(rows);
	}
	
	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * Calcula el numero total de paginas, segun los registros totales y el numero de regitros pro página
	 * 
	 * @param total el número de registros totales
	 * @param rows el número de filas por pagina
	 */
	public void setTotal(final Long total, final Long rows) {
		double dTotal = total.doubleValue();
		double dRows = rows.doubleValue();
		double totalPages = (total > 0) ? Math.ceil(dTotal / dRows) : 0;

		this.total = String.valueOf((int) totalPages);
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
	public void setRecords(final Integer records) {
		this.records = records;
	}

	/*
	 * Funciones asociadas a la gestión de parámetros adicionales
	 */
	/**
	 * Añade un parámetro adicional.
	 * 
	 * @param key Nombre del parámetro.
	 * @param param Objeto a admacenar.
	 */
	@SuppressWarnings("unchecked")
	public void addAdditionalParam(final String key, final Object param) {
		if(REORDER_SELECTION_KEY.equals(key) && param instanceof List 
				&& !((List<?>)param).isEmpty() && ((List<?>)param).get(0) instanceof TableRowDto 
				&& ((TableRowDto<?>)((List<?>)param).get(0)).getModel() instanceof Resource<?>) {
			this.additionalParams.put(key, wrapReorderSelection((List<TableRowDto<T>>)param));
		}else {
			this.additionalParams.put(key, param);
		}
	}

	/**
	 * Recupera un parámetro adicional.
	 * 
	 * @param key Nombre del parámetro.
	 * @return Parámetro almacenado.
	 */
	public Object getAdditionalParam(final String name) {
		return this.additionalParams.get(name);
	}

	/**
	 * Elimina un parámetro adicional de estructura.
	 * 
	 * @param key Nombre del parámetro.
	 */
	public void removeAdditionalParam(final String name) {
		this.additionalParams.remove(name);
	}

	/**
	 * Devuelve el mapa que almacena los parámetros adicionales.
	 * 
	 * @param name
	 * @return
	 */
	@JsonIgnore
	public Map<String, Object> getAdditionalParamsMap() {
		return this.additionalParams;
	}

	/**
	 * Método "any getter" necesario para la serialización del contenido del mapa.
	 * 
	 * @return Mapa que contiene los parámetros adicionales.
	 */
	@JsonAnyGetter
	public Map<String, Object> anyAdditionalParams() {
		return this.additionalParams;
	}

	/**
	 * Método "any setter" necesario para la deserialización del parámetros al mapa
	 * 
	 * @return Mapa que contiene los parámetros adicionales.
	 */
	@JsonAnySetter
	public void set(final String name, final Object value) {
		this.additionalParams.put(name, value);
	}

	/*
	 * Funciones auxiliares
	 */

	public void setReorderedSelection(final List<TableRowDto<T>> reorderedSelection) {

	}

	public void setReorderedSeeker(final List<TableRowDto<T>> reorderedSeeker) {

	}

	@Override
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

	// /**
	// * Procesa la estructura reorderedSelection para completar los datos a
	// * partir de la información de la paginación.
	// *
	// * @param reorderedSelection
	// * Lista de registros reordenados.
	// * @param pagination
	// * Objeto paginación.
	// * @return Lista procesada de elementos reordenados.
	// */
	// private List<TableRowDto<T>> processReorderedSelection(List<TableRowDto<T>> reorderedSelection, Pagination pagination){
	// if (reorderedSelection != null && pagination != null){
	// List<TableRowDto<T>> proccesedReorderedSelection = new ArrayList<TableRowDto<T>>(reorderedSelection);
	// for (TableRowDto<T> tableRow : proccesedReorderedSelection) {
	// Integer rowNum = tableRow.getTableLine()-1;
	// Long page = (rowNum/pagination.getRows())+1;
	//
	// tableRow.setPage(page.intValue());
	// Long rowLine = tableRow.getTableLine()%pagination.getRows();
	// tableRow.setTableLine(rowLine.intValue());
	// }
	// return proccesedReorderedSelection;
	// }
	// return reorderedSelection;
	// }
}