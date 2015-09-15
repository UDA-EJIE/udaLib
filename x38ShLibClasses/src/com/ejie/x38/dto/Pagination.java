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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.StringUtils;

import com.ejie.x38.json.JSONArray;

/**
 * 
 * @author UDA
 *
 */
public class Pagination implements java.io.Serializable{

	private static final long serialVersionUID = 2127819481595995328L;
	
	private Long rows;
	private Long page;

	//Mapeo directo parametros jQGrid -> Pagination
	private String sidx;
	private String sord;
	
	//Jerarquia
	private String tree;	//Nombre elementos expandidos/contraídos
	private String mult;	//Elemento checkeado en multiselección

	// Ordenacion
	private String multiselectionIds;
	private List<Object> multiselectionIdsArray;
	private Boolean selectAll;
	
	public Pagination(){}
	public Pagination(Long rows, Long page, String sidx, String sord){
		this.rows = rows;
		this.page = page;
		this.sidx = sidx;
		this.sord = sord;
	}
	public Pagination(Long rows, Long page, String sidx, String sord, String multiselectionIds, Boolean selectAll){
		this.rows = rows;
		this.page = page;
		this.sidx = sidx;
		this.sord = sord;
		this.multiselectionIds = multiselectionIds;
		this.selectAll = selectAll;
	}
	
	public Long getRows() {
		return rows;
	}
	public void setRows(Long rows) {
		this.rows = rows;
	}
	public Long getPage() {
		return page;
	}
	public void setPage(Long page) {
		this.page = page;
	}
	public String getSidx() {
		return sidx;
	}
	public void setSidx (String sidx) {
		if (!"".equals(sidx)){ //Posible vacío en petición Ajax (jQuery >= 1.8)
			this.sidx = sidx;
		}
	}
	public String getSord() {
		return sord;
	}
	public void setSord (String sord) {
		this.sord = sord;
	}
	public String getTree() {
		return tree;
	}
	public void setTree(String tree) {
		this.tree = tree;
	}
	public String getMult() {
		return mult;
	}
	public void setMult(String mult) {
		this.mult = mult;
	}
	public String getMultiselectionIds() {
		return multiselectionIds;
	}
	public void setMultiselectionIds(String multiselectionIds) {
		this.multiselectionIds = multiselectionIds;
		
		JSONArray jsonArray = new JSONArray(multiselectionIds);
		this.multiselectionIdsArray = jsonArray.getList();
	}
	public Boolean getSelectAll() {
		return selectAll;
	}
	public void setSelectAll(Boolean selectAll) {
		this.selectAll = selectAll;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(this.getClass().getName()).append(" Object {");
		result.append(" [ rows: ").append(this.rows).append(" ]");
		result.append(" [ page: ").append(this.page).append(" ]");
		result.append(" [ sidx: ").append(this.sidx).append(" ]");
		result.append(" [ sord: ").append(this.sord).append(" ]");
		result.append(" [ tree: ").append(this.tree).append(" ]");
		result.append("}");
		return result.toString();
	}
	
	//Utilidades
	private String tokn = "'/'";
	public String getTokn() {
		return tokn;
	}
	public void setTokn(String tokn) {
		this.tokn = "'"+tokn+"'";
	}
	
	/**
	 * PAGINACIÓN
	 */
	public StringBuilder getPaginationQuery(StringBuilder query){
		return getQueryForPagination(query, false);
    }
	public StringBuilder getPaginationQueryJerarquia(StringBuilder query){
		return getQueryForPagination(query, true);
	}
	private StringBuilder getQueryForPagination(StringBuilder query, boolean isJerarquia){
		//Order
		StringBuilder paginationQuery = new StringBuilder();
		if (this.getSidx() != null) {
			if (!isJerarquia){
				paginationQuery.append(" ORDER BY ");
			} else {
				paginationQuery.append("\n\t").append("order siblings by ");
			}
			paginationQuery.append(this.getSidx());
			paginationQuery.append(" ");
			paginationQuery.append(this.getSord());
			query.append(paginationQuery);
			if (isJerarquia){
				query.append("\n");
			}
		}
		
		//Limits
		paginationQuery = new StringBuilder();
		Long rows = this.getRows();	
		Long page = this.getPage();
		if (page!=null && rows!=null){
			paginationQuery.append("SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > " + (rows*(page-1)) +" and rnum < " + ((rows*page)+1));
		}else if (rows!=null) {
			paginationQuery.append("SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > 0 and rnum < " + (rows+1));
		}else{
			return query;
		}
		return paginationQuery;
    }
	
	/**
	 * NORMAL
	 */
	public StringBuilder getQueryJerarquia(
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla
	){
		return getQueryJerarquia(query, mapaWhere, columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, new StringBuilder(""), null, null);
	}
	public StringBuilder getQueryJerarquia(
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla, StringBuilder joins
	){
		return getQueryJerarquia(query, mapaWhere, columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, joins, null, null);
	}
	public StringBuilder getQueryJerarquia(
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla, StringBuilder joins,
			StringBuilder businessFilters, List<?> businessParams
	){
		
		List<Object> queryParams = new ArrayList<Object>();
		
		//Campos específicos de Jerarquía
		query.append("\n\t").append("-- Campos JERARQUIA");
		query.append("\n\t").append(", LEVEL");
		query.append("\n\t").append(", decode((select count(1) from ").append(tabla.get(0));
		query.append(" subquery where subquery.").append(columnaPadre).append("=").append(aliasTabla.get(0)).append(".").append(columna);
		query.append(" ), 0, 'false', 'true') as HASCHILDREN");
		query.append("\n\t").append(", sys_connect_by_path(").append(columnaParentNodes).append(", ").append(tokn).append(") as PARENTNODES ");
		query.append("\n\t").append(", sys_connect_by_path(").append(columna).append(", ").append(tokn).append(") as TREENODES ");
		
		//Decodes para destacar filtrados
		StringBuffer whereConditions = (StringBuffer) mapaWhere.get("query");
		if (whereConditions.length()==0){
			query.append("\n\t").append(", case when (1=0) then 'true' end as FILTER ");
		} else{
			whereConditions = new StringBuffer(whereConditions.substring(whereConditions.indexOf("AND")+4, whereConditions.length()));
			query.append("\n\t").append(", case when (").append(whereConditions).append(") then 'true' end as FILTER ");
			//Añadir los parametros al decode
			@SuppressWarnings("unchecked")
			List<Object> params = (List<Object>) mapaWhere.get("params");
			queryParams.addAll(params);
		}
		
		return getQueryForJerarquia(query, mapaWhere, queryParams, columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, joins, businessFilters, businessParams);
	}
	
	/**
	 * COUNT
	 */
	public StringBuilder getQueryJerarquiaCount(
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes,
			List<String> tabla, List<String> aliasTabla
	){
		return getQueryForJerarquia(query, mapaWhere, new ArrayList<Object>(), columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, new StringBuilder(""), null, null);
	}
	public StringBuilder getQueryJerarquiaCount(
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes,
			List<String> tabla, List<String> aliasTabla, StringBuilder joins
	){
		return getQueryForJerarquia(query, mapaWhere, new ArrayList<Object>(), columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, joins, null, null);
	}
	public StringBuilder getQueryJerarquiaCount(
			StringBuilder query,  Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla, StringBuilder joins,
			StringBuilder businessFilters, List<?> businessParams
	){
		return getQueryForJerarquia(query, mapaWhere, new ArrayList<Object>(), columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, joins, businessFilters, businessParams);
	}

	/**
	 * GENERAL (privada)
	 */
	private StringBuilder getQueryForJerarquia(
			StringBuilder query, Map<String, ?> mapaWhere, List<Object> queryParams,
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla, StringBuilder joins,
			StringBuilder businessFilters, List<?> businessParams
	){
		query.insert(0,"\n\t");
		
		//FROM (tabla + aliasTabla)
		query.append("\n\t").append("from ");
		StringBuilder from = new StringBuilder("");
		int size = tabla.size();
		for (int i = 0; i < size; i++) {
			from.append(tabla.get(i).trim()).append(" ").append(aliasTabla.get(i).trim()).append(", ");
		}
		query.append(StringUtils.trimTrailingCharacter(from.toString().trim(), ',')).append(", (");
		
		//Subqueries
			//PADRES
				query.append("\n\t\t").append("-- PADRES");
				query = querySubquery(query, mapaWhere, queryParams, columna, tabla, aliasTabla, joins, businessFilters, businessParams);
				query.append("\n\t\t").append("connect by prior ").append(columnaPadre).append(" = ").append(columna);
		query.append("\n\t\t").append("union");
			//HIJOS
				query.append("\n\t\t").append("-- HIJOS");
				query = querySubquery(query, mapaWhere, queryParams, columna, tabla, aliasTabla, joins, businessFilters, businessParams);
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
		
		//Gestionar selección múltiple 
		query.append("\n\t").append("-- Relacion JERARQUIA");
		if (this.getMult()==null || this.getMult().equals("")){
			query.append("\n\t").append("start with ").append(columnaPadre).append(" is null");
		} else {
			query.append("\n\t").append("start with ").append(columnaPadre).append(" = ").append(this.getMult());
		}
		query.append("\n\t").append("connect by prior ").append(columna).append(" = ").append(columnaPadre);
		
		//Nodos contraídos
		query = queryUnexpanded(query, queryParams, columnaPadre);
		
		//Modificar parámetros
		@SuppressWarnings("unchecked")
		List<Object> whereParams = (List<Object>) mapaWhere.get("params");
		whereParams.clear();
		whereParams.addAll(queryParams);
		return query;
	}
	
	
	/**
	 * SELECTED
	 */
	public StringBuilder getQuerySelectedGrid(
			StringBuilder query,  Map<String, ?> mapaWhere,
			Object bean, 
			String columna, String columnaPadre,
			List<String> tabla, List<String> aliasTabla
	){
		return getQuerySelectedGrid(query, mapaWhere, bean, columna, columnaPadre, tabla, aliasTabla, new StringBuilder(""), null, null, new ArrayList<String>());
	}
	public StringBuilder getQuerySelectedGrid(
			StringBuilder query,  Map<String, ?> mapaWhere,
			Object bean, 
			String columna, String columnaPadre,
			List<String> tabla, List<String> aliasTabla, StringBuilder joins
	){
		return getQuerySelectedGrid(query, mapaWhere, bean, columna, columnaPadre, tabla, aliasTabla, joins, null, null, new ArrayList<String>());
	}
	public StringBuilder getQuerySelectedGrid(
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
	            if (pd.getReadMethod().invoke(bean)!=null && !name.equals(this.getSidx())){
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
		queryParams.add(this.getRows());
		queryParams.add(this.getRows());
		queryParams.add(this.getRows().toString()); //case requiere literal
		queryParams.add(this.getRows());
		query.append("\n\t").append("from ( ");
		String sidx = this.getSidx();
		if (sidx.contains(",")){
			sidx = sidx.replaceAll("asc", " ");
			sidx = sidx.replaceAll("desc", " ");
		}
		query.append("\n\t\t").append("select ").append(filterNames).append(columna).append(", ").append(columnaPadre).append(", ").append(sidx).append(", rownum ");
		query.append("\n\t\t").append("from ").append(StringUtils.collectionToCommaDelimitedString(tabla));
		query.append("\n\t\t").append("-- Relacion JERARQUIA");
		query.append("\n\t\t").append("start with ").append(columnaPadre).append(" is null "); 
		query.append("\n\t\t").append("connect by prior ").append(columna).append(" = ").append(columnaPadre).append(" ");
		query.append("\n\t\t").append("order siblings by ").append(this.getSidx()).append(" ").append(this.getSord());
		query.append("\n\t").append(") ").append(aliasTabla.get(0)).append(", (");
		
		//Subqueries
			//PADRES
				query.append("\n\t\t").append("-- PADRES");
				query = querySubquery(query, mapaWhere, queryParams, columna, tabla, aliasTabla, joins, businessFilters, businessParams);
				query.append("\n\t\t").append("connect by prior ").append(columnaPadre).append(" = ").append(columna);
		query.append("\n\t\t").append("union");
			//HIJOS
				query.append("\n\t\t").append("-- HIJOS");
				query = querySubquery(query, mapaWhere, queryParams, columna, tabla, aliasTabla, joins, businessFilters, businessParams);
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
		query = queryUnexpanded(query, queryParams, columnaPadre);
		
		query.append("\n\t").append("order siblings by ").append(this.getSidx()).append(" ").append(this.getSord());
		query.append("\n").append(") ");

		//Filtrar seleccionados
		if (this.getMult()!=null){
			query.append("\n").append("-- Registros SELECCIONADOS");
			query.append("\n").append("where (1,pk) in (");
			StringBuilder selectedParams = new StringBuilder("");
			String[] selected = this.getMult().split(",");
			String parsedTokn = tokn.substring(1, tokn.length()-1);
			int selected_length = selected.length;
			for (int i = 0; i < selected_length; i++) {
				String elem = selected[i];
				elem = elem.substring(elem.lastIndexOf(parsedTokn)+parsedTokn.length());
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

	public ResultSetExtractor<TreeMap<String, TreeMap<String, String>>> selectedExtractorGrid = new ResultSetExtractor<TreeMap<String, TreeMap<String, String>>>() {
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

	private StringBuilder querySubquery(
			StringBuilder query, Map<String, ?> mapaWhere, List<Object> queryParams,
			String columna, 
			List<String> tabla, List<String> aliasTabla, StringBuilder joins,
			StringBuilder businessFilters, List<?> businessParams){
		
		@SuppressWarnings("unchecked")
		List<Object> whereParams = (List<Object>) mapaWhere.get("params");
		StringBuffer whereConditions = (StringBuffer) mapaWhere.get("query");
		
		//Condiciones
		String filterSubquery = whereConditions.toString();
		String businessSubquery = (businessFilters!=null) ? businessFilters.toString() : "";
		String joinsSubquery = joins.toString();
		for (String aliasTablaStr : aliasTabla) {
			filterSubquery = filterSubquery.replaceAll("(?i)"+aliasTablaStr+"\\.", "").trim();
			businessSubquery = businessSubquery.replaceAll("(?i)"+aliasTablaStr+"\\.", "").trim();
			joinsSubquery = joinsSubquery.toString().replaceAll("(?i)"+aliasTablaStr+"\\.", "").trim();
		}
		
		query.append("\n\t\t").append("select distinct substr(sys_connect_by_path(").append(columna).append(", ").append(tokn).append("),"); 
		query.append("\n\t\t").append("instr(sys_connect_by_path(").append(columna).append(", ").append(tokn).append("), ").append(tokn).append(", -1)+").append(tokn.substring(1, tokn.length()-1).length()).append(") PK_JERARQUIA");
		query.append("\n\t\t").append("from ").append(StringUtils.collectionToCommaDelimitedString(tabla));
		query.append("\n\t\t").append("where 1=1 ");
			if (!"".equals(joinsSubquery)){
				query.append("\n\t\t").append("-- JOINS");
				query.append("\n\t\t").append(joinsSubquery);
			}
			if (!"".equals(businessSubquery)){
				query.append("\n\t\t").append("-- Condiciones NEGOCIO");
				query.append("\n\t\t").append(businessSubquery);
				queryParams.addAll(businessParams);
			}
		//Jerarquia
		query.append("\n\t\t").append("start with ").append(columna).append(" in ( "); 
			query.append("\n\t\t\t").append("select ").append(columna);
			query.append("\n\t\t\t").append("from ").append(StringUtils.collectionToCommaDelimitedString(tabla));
			query.append("\n\t\t\t").append("where 1=1 ");
			if (!"".equals(joinsSubquery)){
				query.append("\n\t\t\t").append("-- JOINS");
				query.append("\n\t\t\t").append(joinsSubquery);
			}
			if (!"".equals(businessSubquery)){
				query.append("\n\t\t\t").append("-- Condiciones NEGOCIO");
				query.append("\n\t\t\t").append(businessSubquery);
				queryParams.addAll(businessParams);
			}
			if (!"".equals(filterSubquery)){
				query.append("\n\t\t\t").append("-- Condiciones FILTRO");
				query.append("\n\t\t\t").append(filterSubquery);
				queryParams.addAll(whereParams);
			}
		query.append("\n\t\t").append(")");
		return query;
	}
	
	private StringBuilder queryUnexpanded(StringBuilder query, List<Object> params, String columnaPadre){
		if (this.getTree()!=null && !this.getTree().equals("")) {
			StringBuilder elems = new StringBuilder();
			String[] arrTree = this.getTree().split(",");
			for (int i = 0; i < arrTree.length; i++) {
				elems.append("?,");
			}
			elems = new StringBuilder(elems.substring(0, elems.length()-1));
			
			query.append("\n\t").append("-- Registros CONTRAIDOS");
			query.append("\n\t").append("and ").append("").append(columnaPadre).append("").append(" not in (").append(elems).append(")");
			params.addAll(Arrays.asList(this.getTree().split(",")));
		}
		return query;
	}

	
	
	
	
	public List<?> getPaginationList(List<?> list){
		List <Object> returnList = new ArrayList<Object>();
		Long rows = this.getRows();	
		Long page = this.getPage();
		if (page!=null && rows!=null){
			for (int i = (int) (rows*(page-1)); i < (rows*page); i++) {
				returnList.add((Object)list.get(i));
			}
		}else if (rows!=null) {
			for (int i = 0; i < rows; i++) {
				returnList.add((Object)list.get(i));
			}
		}else{
			return list;
		}
		return returnList;
	}
	
	/*
	 * REORDENACION
	 */
	
	public StringBuilder getReorderQuery(StringBuilder query, String... pkCols){
		//Order
		StringBuilder reorderQuery = new StringBuilder();
		if (this.getSidx() != null) {
			reorderQuery.append(" ORDER BY ");
			reorderQuery.append(this.getSidx());
			reorderQuery.append(" ");
			reorderQuery.append(this.getSord());
			query.append(reorderQuery);
		}
		
		reorderQuery = new StringBuilder();
		//Limits
//		Long rows = this.getRows();	
//		Long page = this.getPage();
//		if (page!=null && rows!=null){
//		SELECT rownum rnum, a.*  FROM (
		reorderQuery.append("SELECT ");
		for (String pkCol : pkCols) {
			reorderQuery.append(pkCol).append(",");
		}
		reorderQuery.deleteCharAt(reorderQuery.length()-1);
		reorderQuery.append(" FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) ");
//		}else if (rows!=null) {
//			paginationQuery.append("SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > 0 and rnum < " + (rows+1));
//		}else{
//			return query;
//		}
		return reorderQuery;
    }

	
	//Retrocompatibilidad
	public String getSort() {
		return getSidx();
	}
	public void setSort(String sidx) {
		setSidx(sidx);
	}	
	public String getAscDsc() {
		return getSord();
	}
	public void setAscDsc(String sord) {
		setSord(sord);
	}
}