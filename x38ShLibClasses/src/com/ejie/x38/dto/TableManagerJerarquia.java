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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * 
 * @author UDA
 *
 */
public class TableManagerJerarquia implements java.io.Serializable{

	private static final long serialVersionUID = 2127819481595995328L;
	
	/**
	 * NORMAL
	 */
	public static <T> StringBuilder getQuery(
			TableRequestDto tableRequestDto,
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla
	){
		return getQuery(tableRequestDto, query, mapaWhere, columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, new StringBuilder(""), null, null);
	}
	public static <T> StringBuilder getQuery(
			TableRequestDto tableRequestDto,
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla, StringBuilder joins
	){
		return getQuery(tableRequestDto, query, mapaWhere, columna, columnaPadre, columnaParentNodes, tabla, aliasTabla, joins, null, null);
	}
	public static <T> StringBuilder getQuery(
			TableRequestDto tableRequestDto,
			StringBuilder query, Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla, StringBuilder joins,
			StringBuilder businessFilters, List<?> businessParams
	){
		List<Object> queryParams = new ArrayList<Object>();
		
		//Campos específicos de Jerarquía
		query.append("\n\t").append("-- Campos JERARQUIA");
		query.append("\n\t").append(", LEVEL");
		query.append("\n\t").append(", sys_connect_by_path(").append(columnaParentNodes).append(", '").append(tableRequestDto.getJerarquia().getToken()).append("') as PARENTNODES ");
		query.append("\n\t").append(", decode(connect_by_isleaf, 0, 'false', 'true') as ISLEAF ");
		
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
		
		return getJerarquiaQuery(tableRequestDto, query, mapaWhere, queryParams, columna, columnaPadre, tabla, aliasTabla, joins, businessFilters, businessParams);
	}

	/**
	 * PAGINACIÓN
	 */
	
	public static <T> StringBuilder getPaginationQuery(TableRequestDto tableRequestDto, StringBuilder query){
		return TableManager.getPaginationQuery(tableRequestDto, query, true, null);
	}
	
	public static <T> StringBuilder getPaginationQuery(TableRequestDto tableRequestDto, StringBuilder query, String[] orderByWhiteList){
		return TableManager.getPaginationQuery(tableRequestDto, query, true, orderByWhiteList);
	}
	
	/**
	 * COUNT
	 */
	public static <T> StringBuilder getQueryCount(
			TableRequestDto tableRequestDto,
			Map<String, ?> mapaWhere, 
			String columna, String columnaPadre,
			List<String> tabla, List<String> aliasTabla
	){
		return getJerarquiaQuery(tableRequestDto, new StringBuilder("SELECT COUNT(1) "), mapaWhere, new ArrayList<Object>(), columna, columnaPadre, tabla, aliasTabla, new StringBuilder(""), null, null);
	}
	public static <T> StringBuilder getQueryCount(
			TableRequestDto tableRequestDto,
			Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes,
			List<String> tabla, List<String> aliasTabla, StringBuilder joins
	){
		return getJerarquiaQuery(tableRequestDto, new StringBuilder("SELECT COUNT(1) "), mapaWhere,  new ArrayList<Object>(), columna, columnaPadre, tabla, aliasTabla, joins, null, null);
	}
	public static <T> StringBuilder getQueryCount(
			TableRequestDto tableRequestDto,
			Map<String, ?> mapaWhere, 
			String columna, String columnaPadre, String columnaParentNodes, 
			List<String> tabla, List<String> aliasTabla, StringBuilder joins,
			StringBuilder businessFilters, List<?> businessParams
	){
		return getJerarquiaQuery(tableRequestDto, new StringBuilder("SELECT COUNT(1) "), mapaWhere,  new ArrayList<Object>(), columna, columnaPadre, tabla, aliasTabla, joins, businessFilters, businessParams);
	}

	/**
	 * GENERAL (interna)
	 */
	protected static <T> StringBuilder getJerarquiaQuery(
			TableRequestDto tableRequestDto,
			StringBuilder query, Map<String, ?> mapaWhere, List<Object> queryParams,
			String columna, String columnaPadre, 
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
		query.append(StringUtils.trimTrailingCharacter(from.toString().trim(), ','));
		
		//Si tiene criterios aplicar _subqueries_ PADRE/HIJOS
		if (!((List<?>) mapaWhere.get("params")).isEmpty()){
			query.append(", (");
			
			//Subqueries
				//PADRES
					query.append("\n\t\t").append("-- PADRES");
					query = querySubquery(tableRequestDto, query, mapaWhere, queryParams, columna, tabla, aliasTabla, joins, businessFilters, businessParams);
					query.append("\n\t\t").append("connect by prior ").append(columnaPadre).append(" = ").append(columna);
			query.append("\n\t\t").append("union");
				//HIJOS
					query.append("\n\t\t").append("-- HIJOS");
					query = querySubquery(tableRequestDto, query, mapaWhere, queryParams, columna, tabla, aliasTabla, joins, businessFilters, businessParams);
					query.append("\n\t\t").append("connect by prior ").append(columna).append(" = ").append(columnaPadre);
			query.append("\n\t").append(") jerarquia");	
		}
		
		//CONDICIONES
		query.append("\n\t").append("where 1=1 ");
		//Si tiene criterios aplicar _join_ PADRE/HIJOS
		if (!((List<?>) mapaWhere.get("params")).isEmpty()){
			query.append("\n\t").append("-- JOIN JERARQUIA");
			query.append("\n\t").append("and ").append(aliasTabla.get(0)).append(".").append(columna).append("=jerarquia.PK_JERARQUIA");
		}
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
		if (tableRequestDto.getJerarquia().getParentId()==null || tableRequestDto.getJerarquia().getParentId().equals("")){
			query.append("\n\t").append("start with ").append(columnaPadre).append(" is null");
		} else {
			query.append("\n\t").append("start with ").append(columnaPadre).append(" = ").append(tableRequestDto.getJerarquia().getParentId());
		}
		query.append("\n\t").append("connect by prior ").append(columna).append(" = ").append(columnaPadre);
		
		//Nodos contraídos
		query = filterUnexpanded(tableRequestDto, query, queryParams, columnaPadre);
		
		//Modificar parámetros
		@SuppressWarnings("unchecked")
		List<Object> whereParams = (List<Object>) mapaWhere.get("params");
		whereParams.clear();
		whereParams.addAll(queryParams);
		return query;
	}
	
	/**
	 * Subquery PADRES o HIJOS (interna)
	 */
	protected static <T> StringBuilder querySubquery(
			TableRequestDto tableRequestDto,
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
		
		String token = tableRequestDto.getJerarquia().getToken();
		query.append("\n\t\t").append("select distinct substr(sys_connect_by_path(").append(columna).append(", '").append(token).append("'),"); 
		query.append("\n\t\t").append("instr(sys_connect_by_path(").append(columna).append(", '").append(token).append("'), '").append(token).append("', -1)+").append(token.length()).append(") PK_JERARQUIA");
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
	
	/**
	 * Filtrar elementos contraidos en la query (interna
	 */
	protected static <T> StringBuilder filterUnexpanded(TableRequestDto tableRequestDto, StringBuilder query, List<Object> params, String columnaPadre){
		if (tableRequestDto.getJerarquia().getTree()!=null && !tableRequestDto.getJerarquia().getTree().equals("")) {
			StringBuilder elems = new StringBuilder();
			String[] arrTree = tableRequestDto.getJerarquia().getTree().split(",");
			for (int i = 0; i < arrTree.length; i++) {
				elems.append("?,");
			}
			elems = new StringBuilder(elems.substring(0, elems.length()-1));
			
			query.append("\n\t").append("-- Registros CONTRAIDOS");
			query.append("\n\t").append("and ").append("").append(columnaPadre).append("").append(" not in (").append(elems).append(")");
			params.addAll(Arrays.asList(tableRequestDto.getJerarquia().getTree().split(",")));
		}
		return query;
	}

	
	/**
	 * HIJOS
	 */
	public static <T> StringBuilder getQueryChildren(
			TableRequestDto tableRequestDto,
			Map<String, ?> mapaWhere, 
			String columna, String columnaPadre,
			List<String> tabla, List<String> aliasTabla
	){
		return getQueryChildren(tableRequestDto, mapaWhere, columna, columnaPadre, tabla, aliasTabla, new StringBuilder(""), null, null, null);
	}
	public static <T> StringBuilder getQueryChildren(
				TableRequestDto tableRequestDto,
				Map<String, ?> mapaWhere, 
				String columna, String columnaPadre,
				List<String> tabla, List<String> aliasTabla, StringBuilder joins
	){
		return getQueryChildren(tableRequestDto, mapaWhere, columna, columnaPadre, tabla, aliasTabla, joins, null, null, null);
	}
	public static <T> StringBuilder getQueryChildren(
			TableRequestDto tableRequestDto,
			Map<String, ?> mapaWhere, 
			String columna, String columnaPadre,
			List<String> tabla, List<String> aliasTabla, StringBuilder joins,
			StringBuilder businessFilters, List<?> businessParams,
			String[] orderByWhiteList
	){

		//Jerarquia (filtro -> hijos del padre)
			//Quitar buscar hijos/descendientes
			Map<String,Object> mapaWhereTMP = new HashMap<String, Object>();
			mapaWhereTMP.put("params", new ArrayList<Object>());
		StringBuilder sqlJerarquiaHijos = getJerarquiaQuery(tableRequestDto, new StringBuilder(), mapaWhereTMP, new ArrayList<Object>(), columna, columnaPadre, tabla, aliasTabla, joins, businessFilters, businessParams);
		//No requiere ordenación ya que es para obetner las PKs de los descendientes

		//Quitar filtrado del padre (obtener todos los elementos para saber su página, línea en página y línea en tabla)
		tableRequestDto.getJerarquia().setParentId(null);
		
		//Jerarquia (all -> metadatos de todos)
		StringBuilder sqlJerarquia = getJerarquiaQuery(tableRequestDto, new StringBuilder(), mapaWhere, new ArrayList<Object>(), columna, columnaPadre, tabla, aliasTabla, joins, businessFilters, businessParams);
		//Ordenar según tabla
		sqlJerarquia.append(TableManager.getOrderBy(tableRequestDto, true, orderByWhiteList));
		
		//Query propia
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("\n").append("select ").append(columna).append(TableManager.getMultiselectionSelectOutter(tableRequestDto));
		sbSQL.append("\n").append("from ( ");
		sbSQL.append("\n\t").append("-- Query Jerarquia");
		sbSQL.append("\n\t").append("select ").append(columna).append(TableManager.getMultiselectionSelectInner(tableRequestDto));
		sbSQL.append("\n\t").append(sqlJerarquia.substring(sqlJerarquia.indexOf("from")));
		sbSQL.append(") ");
		sbSQL.append("\n").append("where ").append(columna).append(" in ( ");
		sbSQL.append("\n\t").append("-- Query JerarquiaFiltro");
		sbSQL.append("\n\t").append("select ").append(columna).append(" ");
		if (tableRequestDto.getJerarquia().isChild()){
			//Solo hijos directos
			sbSQL.append("\n\t").append(sqlJerarquiaHijos.substring(sqlJerarquiaHijos.indexOf("from")).replaceAll("1=1", "LEVEL=1"));
		} else {
			//Todos los descendientes
			sbSQL.append("\n\t").append(sqlJerarquiaHijos.substring(sqlJerarquiaHijos.indexOf("from")));
		}
		sbSQL.append("\n").append(") ");
		return sbSQL;
	}

}