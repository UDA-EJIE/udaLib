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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ejie.x38.dao.sql.OracleEncoder;
import com.ejie.x38.dao.sql.error.SqlInjectionException;

/**
 * 
 * @author UDA
 *
 */
@Deprecated
public class JQGridManager implements java.io.Serializable{

	private static final long serialVersionUID = 2127819481595995328L;
	
	
	
	/**
	 * PAGINACIÓN
	 */
	public static <T> StringBuilder getPaginationQuery(JQGridRequestDto pagination, StringBuilder query){
		return getQueryForPagination(pagination, query, false, null);
    }
	
	public static <T> StringBuilder getPaginationQuery(JQGridRequestDto pagination, StringBuilder query,  boolean isJerarquia){
		return getQueryForPagination(pagination, query, isJerarquia, null);
    }
	
	public static <T> StringBuilder getPaginationQuery(JQGridRequestDto pagination, StringBuilder query,  String[] orderByWhiteList){
		return getQueryForPagination(pagination, query, false, orderByWhiteList);
    }
	
	public static <T> StringBuilder getPaginationQuery(JQGridRequestDto pagination, StringBuilder query,  boolean isJerarquia, String[] orderByWhiteList){
		return getQueryForPagination(pagination, query, isJerarquia, orderByWhiteList);
    }
	
	private static boolean isInWhiteList(String[] whiteList, String text){
		
		// Comprobamos si la cadena de ordenación contiene varios campos
					
		
		if (StringUtils.isBlank(text)){
			return false;
		}
		
		for (String string : whiteList) {
			if (text.trim().toUpperCase().equals(string.trim().toUpperCase())){
				return true;
			}
		}
		
		return false;
		
	}
	
	private static boolean validateOrderByFields (String[] orderByWhiteList, String text){
		
		boolean result = true;
		
		String[] fields = text.indexOf(",")!=-1?text.split(","):new String[]{text};
		
		for (String field : fields) {
			
			result = result && JQGridManager.isInWhiteList(orderByWhiteList, field);
			
		}
		
		return result;
		
		
	}
	
	protected static <T> StringBuilder getQueryForPagination(JQGridRequestDto pagination, StringBuilder query, boolean isJerarquia, String[] orderByWhiteList){
		//Order
		query.append(getOrderBy(pagination, isJerarquia, orderByWhiteList));
			
		
		//Limits
		StringBuilder paginationQuery = new StringBuilder();
		Long rows = pagination.getRows();	
		Long page = pagination.getPage();
		if (page!=null && rows!=null){
			paginationQuery.append("SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > " + (rows*(page-1)) +" and rnum < " + ((rows*page)+1));
		}else if (rows!=null) {
			paginationQuery.append("SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > 0 and rnum < " + (rows+1));
		}else{
			return query;
		}
		return paginationQuery;
    }
	
	protected static <T> StringBuilder getOrderBy (JQGridRequestDto pagination, boolean isJerarquia){
		return JQGridManager.getOrderBy(pagination, isJerarquia, null);
	
	}
	
	/**
	 * ORDER BY (interno)
	 */
	protected static <T> StringBuilder getOrderBy (JQGridRequestDto pagination, boolean isJerarquia, String[] orderByWhiteList){
		//Order
		StringBuilder orderBy = new StringBuilder();
		if (pagination.getSidx() != null) {
						
			if (orderByWhiteList != null && !JQGridManager.validateOrderByFields(orderByWhiteList, pagination.getSidx())){
				throw new SqlInjectionException("Campo no permitido");
			}
			
			if (!isJerarquia){
				orderBy.append(" ORDER BY ");
			} else {
				orderBy.append("\n\t").append("order siblings by ");
			}
			orderBy.append(OracleEncoder.getInstance().encode(pagination.getSidx()));
			orderBy.append(" ");
			orderBy.append(OracleEncoder.getInstance().encode(pagination.getSord()));
			if (isJerarquia){
				orderBy.append("\n");
			}
		}
		return orderBy;
	}
	
	
	
	/**
	 * MULTISELECCION (utilidades internas)
	 */
	protected static <T> StringBuilder getMultiselectionSelectOutter(JQGridRequestDto pagination){
		return new StringBuilder().append(" , page, pageLine, tableLine "); 
	}
	protected static <T> StringBuilder getMultiselectionSelectInner(JQGridRequestDto pagination){
		return new StringBuilder().append(" , ceil(rownum/").append(pagination.getRows()).append(") page, case when (mod(rownum,").append(pagination.getRows()).append(")=0) then '").append(pagination.getRows()).append("' else TO_CHAR(mod(rownum,").append(pagination.getRows()).append(")) end as pageLine, rownum as tableLine "); 
	}
	
//	public static StringBuilder getMultiselectionQuery(Pagination pagination, List<String> pkList, String tabla){	
//		StringBuilder sbSQL = new StringBuilder();
//		sbSQL.append("\n").append("select ID, page, pageLine, tableLine from ( ");      
//		sbSQL.append("\n\t").append("select ID, ceil(rownum/").append(pagination.getRowNum()).append(") page, case when (mod(rownum,").append(pagination.getRowNum()).append(")=0) then '").append(pagination.getRowNum()).append("' else TO_CHAR(mod(rownum,").append(pagination.getRowNum()).append(")) end as pageLine, rownum as tableLine "); 
//		sbSQL.append("\n\t").append("from ").append(tabla).append(" ");
//			sbSQL.append("\n\t").append("order by ").append(pagination.getSidx()).append(" ").append(pagination.getSord()).append(" ");
//		sbSQL.append("\n").append(") ");
//		sbSQL.append("\n").append("where ID in ( ");
//			sbSQL.append("\n\t").append("select ID ");
//			sbSQL.append("\n\t").append("from ").append(tabla).append(" t1 ");
//		sbSQL.append("\n").append(") ");
//		return sbSQL;
//	}
	
//	public static StringBuilder getSearchQuery(Pagination pagination, List<String> pkList, String tabla){	
//		StringBuilder sbSQL = new StringBuilder();
//		sbSQL.append("\n").append("select ID, page, pageLine, tableLine from ( ");      
//		sbSQL.append("\n\t").append("select ID, ceil(rownum/").append(pagination.getRowNum()).append(") page, case when (mod(rownum,").append(pagination.getRowNum()).append(")=0) then '").append(pagination.getRowNum()).append("' else TO_CHAR(mod(rownum,").append(pagination.getRowNum()).append(")) end as pageLine, rownum as tableLine "); 
//		sbSQL.append("\n\t").append("from ").append(tabla).append(" ");
//			sbSQL.append("\n\t").append("order by ").append(pagination.getSidx()).append(" ").append(pagination.getSord()).append(" ");
//		sbSQL.append("\n").append(") ");
//		sbSQL.append("\n").append("where ID in ( ");
//			sbSQL.append("\n\t").append("select ID ");
//			sbSQL.append("\n\t").append("from ").append(tabla).append(" t1 ");
//		sbSQL.append("\n").append(") ");
//		return sbSQL;
//	}
	
	public static <T> StringBuilder getSearchQuery(StringBuilder query, JQGridRequestDto pagination, Class<T> clazz, List<Object> paramList, String searchSQL, List<Object> searchParamList, String... pkList){
		return JQGridManager.getSearchQuery(query, pagination, clazz, paramList, searchSQL, searchParamList, null, pkList);
	}
	
	public static <T> StringBuilder getSearchQuery(StringBuilder query, JQGridRequestDto pagination, Class<T> clazz, List<Object> paramList, String searchSQL, List<Object> searchParamList, List<String> tableAliases, String... pkList){	
		
		String pkStr = JQGridManager.strArrayToCommaSeparatedStr(pkList);
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("\n").append("select ").append(pkStr.replaceAll("_","")).append(JQGridManager.getMultiselectionSelectOutter(pagination)).append("from ( ");      
		sbSQL.append("\n\t").append("select SEARCH_QUERY.*").append(JQGridManager.getMultiselectionSelectInner(pagination)); 
		sbSQL.append("\n\t").append("from (").append(query);
			sbSQL.append("\n\t").append(JQGridManager.getOrderBy(pagination, false)).append(") SEARCH_QUERY ");
		sbSQL.append("\n").append(") ");
		sbSQL.append("\n").append("where 1=1 ");
		
		for (String tableAlias : tableAliases) {
			searchSQL = searchSQL.replaceAll("(?i)"+tableAlias.trim()+"\\.", "").trim();
		}
		sbSQL.append("\n\t").append(searchSQL.replaceAll("_",""));
			
		paramList.addAll(searchParamList);
//		sbSQL.append("(").append(pkStr).append(") ");
//		sbSQL.append(pagination.getMultiselection().getSelectedAll()?" NOT IN ":" IN (");
//		
//		for (T selectedBean : pagination.getMultiselection().getSelected(clazz)) {
//			sbSQL.append("(");
//			for (int i = 0; i < pkList.length; i++) {
//				String prop = pagination.getMultiselection().getPkNames().get(i);
//				sbSQL.append("?").append(",");
//				try {
//					paramList.add(BeanUtils.getProperty(selectedBean, prop));
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (NoSuchMethodException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//			sbSQL.deleteCharAt(sbSQL.length()-1);
//			sbSQL.append("),");
//		}
//		
//		sbSQL.deleteCharAt(sbSQL.length()-1);
//		sbSQL.append(")");
		
		return sbSQL;
	}
	
	public static <T extends Object> StringBuilder getReorderQuery(StringBuilder query, JQGridRequestDto jqGridRequestDto, Class<T> clazz, List<Object> paramList, String... pkList){	
		
		String pkStr = JQGridManager.strArrayToCommaSeparatedStr(pkList);
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("\n").append("select ").append(pkStr).append(JQGridManager.getMultiselectionSelectOutter(jqGridRequestDto)).append("from ( ");      
		sbSQL.append("\n\t").append("select ").append(pkStr).append(JQGridManager.getMultiselectionSelectInner(jqGridRequestDto)); 
		sbSQL.append("\n\t").append("from (").append(query);
			sbSQL.append("\n\t").append(JQGridManager.getOrderBy(jqGridRequestDto, false)).append(") ");
		sbSQL.append("\n").append(") ");
		sbSQL.append("\n").append("where ");
		
		sbSQL.append("(").append(pkStr).append(") IN (");
//		sbSQL.append(jqGridRequestDto.getMultiselection().getSelectedAll()?" NOT IN (":" IN (");
		for (T selectedBean : jqGridRequestDto.getMultiselection().getSelected(clazz)) {
			sbSQL.append("(");
			for (int i = 0; i < pkList.length; i++) {
				String prop = jqGridRequestDto.getCore().getPkNames().get(i);
				sbSQL.append("?").append(",");
				try {
					paramList.add(BeanUtils.getProperty(selectedBean, prop));
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			sbSQL.deleteCharAt(sbSQL.length()-1);
			sbSQL.append("),");
		}
		
		sbSQL.deleteCharAt(sbSQL.length()-1);
		sbSQL.append(")");
		
		return sbSQL;
	}
	
	private static String strArrayToCommaSeparatedStr(String[] strArray){
		StringBuilder retStr = new StringBuilder();
		for (String str : strArray) {
			retStr.append(str).append(",");
		}
		retStr.deleteCharAt(retStr.length()-1);
		
		return retStr.toString();
	}
	
	public static <T> List<?> getPaginationList(JQGridRequestDto pagination, List<?> list){
		List <Object> returnList = new ArrayList<Object>();
		Long rows = pagination.getRows();	
		Long page = pagination.getPage();
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
	
	public static <T> StringBuilder getReorderQuery(JQGridRequestDto pagination, StringBuilder query, String... pkCols){
		//Order
		StringBuilder reorderQuery = new StringBuilder();
		if (pagination.getSidx() != null) {
			reorderQuery.append(" ORDER BY ");
			reorderQuery.append(pagination.getSidx());
			reorderQuery.append(" ");
			reorderQuery.append(pagination.getSord());
			query.append(reorderQuery);
		}
		
		reorderQuery = new StringBuilder();
		//Limits
//		Long rows = pagination.getRows();	
//		Long page = pagination.getPage();
//		if (page!=null && rows!=null){
//		SELECT rownum rnum, a.*  FROM (
//		reorderQuery.append("SELECT ");
//		for (String pkCol : pkCols) {mu
//			reorderQuery.append(pkCol).append(",");
//		}
//		reorderQuery.deleteCharAt(reorderQuery.length()-1);
//		reorderQuery.append(" FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) ");
		reorderQuery.append(" SELECT * FROM (SELECT rownum rnum, a.*  FROM (").append(query).append(")a) ");
		reorderQuery.append(" WHERE ID IN (");
//		for (Object pkCol : pagination.getMultiselection().getSelectedIds()) {
//			reorderQuery.append("'").append(pkCol).append("',");;
//		}
		reorderQuery.deleteCharAt(reorderQuery.length()-1);
		reorderQuery.append(") ");
//		}else if (rows!=null) {
//			paginationQuery.append("SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > 0 and rnum < " + (rows+1));
//		}else{
//			return query;
//		}
		return reorderQuery;
    }

//	
//	String pkStr = JQGridManager.strArrayToCommaSeparatedStr(pkList);
//	
//	StringBuilder sbSQL = new StringBuilder();
//	
//	sbSQL.append("\n").append("select ").append(pkStr).append(JQGridManager.getMultiselectionSelectOutter(jqGridRequestDto)).append("from ( ");      
//	sbSQL.append("\n\t").append("select ").append(pkStr).append(JQGridManager.getMultiselectionSelectInner(jqGridRequestDto)); 
//	sbSQL.append("\n\t").append("from (").append(query);
//		sbSQL.append("\n\t").append(JQGridManager.getOrderBy(jqGridRequestDto, false)).append(") ");
//	sbSQL.append("\n").append(") ");
//	sbSQL.append("\n").append("where ");
//	
//	sbSQL.append("(").append(pkStr).append(") IN (");
	
	/*
	 * BORRADO MULTIPLE
	 */
	public static <T> StringBuilder getRemoveMultipleQuery(JQGridRequestDto jqGridRequestDto, Class<T> clazz, StringBuilder query, List<Object> paramList, String... pkCols){
		
		String pkStr = JQGridManager.strArrayToCommaSeparatedStr(pkCols);
		
		StringBuilder removeQuery = new StringBuilder();
		
		removeQuery.append("DELETE FROM (").append(query).append(") ");
		removeQuery.append(" WHERE (").append(pkStr).append(") ").append(jqGridRequestDto.getMultiselection().getSelectedAll()?" NOT ":"").append(" IN (");
//		sbSQL.append(jqGridRequestDto.getMultiselection().getSelectedAll()?" NOT IN (":" IN (");
		for (T selectedBean : jqGridRequestDto.getMultiselection().getSelected(clazz)) {
			removeQuery.append("(");
			for (int i = 0; i < pkCols.length; i++) {
				String prop = jqGridRequestDto.getCore().getPkNames().get(i);
				removeQuery.append("?").append(",");
				try {
					paramList.add(BeanUtils.getProperty(selectedBean, prop));
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			removeQuery.deleteCharAt(removeQuery.length()-1);
			removeQuery.append("),");
		}
		
		removeQuery.deleteCharAt(removeQuery.length()-1);
		removeQuery.append(")");
		
		return removeQuery;
		
	}
	
	/*
	 * SELECCION MULTIPLE
	 */
	public static <T> StringBuilder getSelectMultipleQuery(JQGridRequestDto jqGridRequestDto, Class<T> clazz, List<Object> paramList, String... pkCols){
		
		String pkStr = JQGridManager.strArrayToCommaSeparatedStr(pkCols);
		
		StringBuilder selectQuery = new StringBuilder();
		
		selectQuery.append("SELECT * FROM USUARIO");
		selectQuery.append(" WHERE (").append(pkStr).append(") ").append(jqGridRequestDto.getMultiselection().getSelectedAll()?" NOT ":"").append(" IN (");
		for (T selectedBean : jqGridRequestDto.getMultiselection().getSelected(clazz)) {
			selectQuery.append("(");
			for (int i = 0; i < pkCols.length; i++) {
				String prop = jqGridRequestDto.getCore().getPkNames().get(i);
				selectQuery.append("?").append(",");
				try {
					paramList.add(BeanUtils.getProperty(selectedBean, prop));
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			selectQuery.deleteCharAt(selectQuery.length()-1);
			selectQuery.append("),");
		}
		
		selectQuery.deleteCharAt(selectQuery.length()-1);
		selectQuery.append(")");
		
		return selectQuery;
		
	}
	
}