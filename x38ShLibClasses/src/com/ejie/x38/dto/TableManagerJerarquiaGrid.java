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

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.StringUtils;

/**
 * 
 * @author UDA
 *
 */
public class TableManagerJerarquiaGrid implements java.io.Serializable{

	private static final long serialVersionUID = 2127819481595995328L;
	
	/**
	 * NORMAL - GRID
	 */
	public static <T> StringBuilder getQuery(
			JQGridRequestDto jqGridRequestDto,
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla
	){
		return getQuery(jqGridRequestDto, query, mapaWhere, columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, new StringBuilder(""), null, null);
	}
	public static <T> StringBuilder getQuery(
			JQGridRequestDto jqGridRequestDto,
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla, StringBuilder joins
	){
		return getQuery(jqGridRequestDto, query, mapaWhere, columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, joins, null, null);
	}
	public static <T> StringBuilder getQuery(
			JQGridRequestDto jqGridRequestDto,
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla, StringBuilder joins,
			StringBuilder businessFilters, List<?> businessParams
	){
		//GRID - JERARQUIA
		query.append("\n\t").append("-- Campos JERARQUIA (grid)");
		query.append("\n\t").append(", decode((select count(1) from ").append(tabla.get(0));
			query.append(" subquery where subquery.").append(columnaPadre).append("=").append(aliasTabla.get(0)).append(".").append(columna);
			query.append(" ), 0, 'false', 'true') as HASCHILDREN");
		query.append("\n\t").append(", sys_connect_by_path(").append(columna).append(", ").append(jqGridRequestDto.getJerarquia().getToken()).append(") as TREENODES ");
		return JQGridManagerJerarquia.getQuery(jqGridRequestDto, query, mapaWhere, columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, joins, businessFilters, businessParams);
	}
	
	
	/**
	 * SELECTED
	 */
	public static <T> StringBuilder getQuerySelectedGrid(
			JQGridRequestDto jqGridRequestDto,
			StringBuilder query,  Map<String, ?> mapaWhere,
			Object bean, 
			String columna, String columnaPadre,
			List<String> tabla, List<String> aliasTabla
	){
		return getQuerySelectedGrid(jqGridRequestDto, query, mapaWhere, bean, columna, columnaPadre, tabla, aliasTabla, new StringBuilder(""), null, null, new ArrayList<String>());
	}
	public static <T> StringBuilder getQuerySelectedGrid(
			JQGridRequestDto jqGridRequestDto,
			StringBuilder query,  Map<String, ?> mapaWhere,
			Object bean, 
			String columna, String columnaPadre,
			List<String> tabla, List<String> aliasTabla, StringBuilder joins
	){
		return getQuerySelectedGrid(jqGridRequestDto, query, mapaWhere, bean, columna, columnaPadre, tabla, aliasTabla, joins, null, null, new ArrayList<String>());
	}
	public static <T> StringBuilder getQuerySelectedGrid(
			JQGridRequestDto jqGridRequestDto,
			StringBuilder query,  Map<String, ?> mapaWhere,
			Object bean, 
			String columna, String columnaPadre,
			List<String> tabla, List<String> aliasTabla, StringBuilder joins,
			StringBuilder businessFilters, List<?> businessParams, List<String> businessNames
	){
		
		List<Object> queryParams = new ArrayList<Object>();
		
		//Calcular campos de filtrado (evitar el elemento por el que se ordena)
		StringBuilder filterNames = new StringBuilder("");
		try {
	        PropertyDescriptor[] props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();  
	        for (PropertyDescriptor pd : props) {  
	            String name = pd.getName();  
	            if (pd.getReadMethod().invoke(bean)!=null && !name.equals(jqGridRequestDto.getSidx())){
	            	filterNames.append(name).append(", ");
	            }
	        }
		} catch(Exception e){
			throw new RuntimeException();
		}

		//Calcular campos de negocio
		for (String name : businessNames) {  
			filterNames.append(name).append(", ");
		}
		
		query.append("\n").append("select * from ( ");
		query.append("\n\t").append("select ").append(columna).append(" as pk, ceil(rownum/?) page, case when (mod(rownum,?)=0) then ? else TO_CHAR(mod(rownum,?)) end as line ");
		queryParams.add(jqGridRequestDto.getRows());
		queryParams.add(jqGridRequestDto.getRows());
		queryParams.add(jqGridRequestDto.getRows().toString()); //case requiere literal
		queryParams.add(jqGridRequestDto.getRows());
		query.append("\n\t").append("from ( ");
		String sidx = jqGridRequestDto.getSidx();
		if (sidx.contains(",")){
			sidx = sidx.replaceAll("asc", " ");
			sidx = sidx.replaceAll("desc", " ");
		}
		query.append("\n\t\t").append("select ").append(filterNames).append(columna).append(", ").append(columnaPadre).append(", ").append(sidx).append(", rownum ");
		query.append("\n\t\t").append("from ").append(StringUtils.collectionToCommaDelimitedString(tabla));
		query.append("\n\t\t").append("-- Relacion JERARQUIA");
		query.append("\n\t\t").append("start with ").append(columnaPadre).append(" is null "); 
		query.append("\n\t\t").append("connect by prior ").append(columna).append(" = ").append(columnaPadre).append(" ");
		query.append("\n\t\t").append("order siblings by ").append(jqGridRequestDto.getSidx()).append(" ").append(jqGridRequestDto.getSord());
		query.append("\n\t").append(") ").append(aliasTabla.get(0)).append(", (");
		
		//Subqueries
			//PADRES
				query.append("\n\t\t").append("-- PADRES");
				query = JQGridManagerJerarquia.querySubquery(jqGridRequestDto, query, mapaWhere, queryParams, columna, tabla, aliasTabla, joins, businessFilters, businessParams);
				query.append("\n\t\t").append("connect by prior ").append(columnaPadre).append(" = ").append(columna);
		query.append("\n\t\t").append("union");
			//HIJOS
				query.append("\n\t\t").append("-- HIJOS");
				query = JQGridManagerJerarquia.querySubquery(jqGridRequestDto, query, mapaWhere, queryParams, columna, tabla, aliasTabla, joins, businessFilters, businessParams);
				query.append("\n\t\t").append("connect by prior ").append(columna).append(" = ").append(columnaPadre);
		query.append("\n\t").append(") jerarquia");	
		
		//CONDICIONES
		query.append("\n\t").append("where 1=1 ");
		query.append("\n\t").append("-- JOIN JERARQUIA");
		query.append("\n\t").append("and ").append(aliasTabla.get(0)).append(".").append(columna).append("=jerarquia.PK_JERARQUIA");
		if (!"".equals(joins.toString())){
			query.append("\n\t").append("-- JOINS");
			query.append("\n\t").append(joins);
		}
		if (businessFilters!=null){
			query.append("\n\t").append("-- Condiciones NEGOCIO");
			query.append("\n\t").append(businessFilters.toString().trim());
			queryParams.addAll(businessParams);
		}
		
		query.append("\n\t").append("-- Relacion JERARQUIA");
		query.append("\n\t").append("start with ").append(columnaPadre).append(" is null "); 
		query.append("\n\t").append("connect by prior ").append(columna).append(" = ").append(columnaPadre).append(" ");

		//Nodos contraídos
		query = JQGridManagerJerarquia.filterUnexpanded(jqGridRequestDto, query, queryParams, columnaPadre);
		
		query.append("\n\t").append("order siblings by ").append(jqGridRequestDto.getSidx()).append(" ").append(jqGridRequestDto.getSord());
		query.append("\n").append(") ");

		//Filtrar seleccionados
		if (jqGridRequestDto.getJerarquia().getParentId()!=null){
			query.append("\n").append("-- Registros SELECCIONADOS");
			query.append("\n").append("where (1,pk) in (");
			StringBuilder selectedParams = new StringBuilder("");
			String[] selected = jqGridRequestDto.getJerarquia().getParentId().split(",");
			String parsedToken = jqGridRequestDto.getJerarquia().getToken().substring(1, jqGridRequestDto.getJerarquia().getToken().length()-1);
			int selected_length = selected.length;
			for (int i = 0; i < selected_length; i++) {
				String elem = selected[i];
				elem = elem.substring(elem.lastIndexOf(parsedToken)+parsedToken.length());
				selectedParams.append("(1,?),");
				queryParams.add(elem);
			}
			query.append(selectedParams.substring(0, selectedParams.length()-1));
			query.append(") ");
		}
		
		//Modificar parámetros
		@SuppressWarnings("unchecked")
		List<Object> whereParams = (List<Object>) mapaWhere.get("params");
		whereParams.clear();
		whereParams.addAll(queryParams);
		return query;
	}

	public static ResultSetExtractor<TreeMap<String, TreeMap<String, String>>> selectedExtractorGrid = new ResultSetExtractor<TreeMap<String, TreeMap<String, String>>>() {
		public TreeMap<String, TreeMap<String, String>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
			
			TreeMap<String, TreeMap<String, String>> selectedMap = new TreeMap<String, TreeMap<String, String>>();
			while (resultSet.next()) {
				String page = resultSet.getString("PAGE");
				String line = resultSet.getString("LINE");
				String pk = resultSet.getString("PK");
				
				TreeMap<String, String> pageMap = selectedMap.get(page);
				if (pageMap == null){
					pageMap = new TreeMap<String, String>();
				}
				pageMap.put(line, pk);
				
				selectedMap.put(page, pageMap);
			}
			return selectedMap;
		}
	};
}