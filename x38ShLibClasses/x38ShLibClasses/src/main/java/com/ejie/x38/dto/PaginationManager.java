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

/**
 * 
 * @author UDA
 *
 */
public class PaginationManager implements java.io.Serializable{

	private static final long serialVersionUID = 2127819481595995328L;
	
	/**
	 * PAGINACIÓN
	 */
	public static StringBuilder getPaginationQuery(Pagination pagination, StringBuilder query){
		return getQueryForPagination(pagination, query, false);
    }
	protected static StringBuilder getQueryForPagination(Pagination pagination, StringBuilder query, boolean isJerarquia){
		//Order
		query.append(getOrderBy(pagination, isJerarquia));
		
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
	
	/**
	 * ORDER BY (interno)
	 */
	protected static StringBuilder getOrderBy (Pagination pagination, boolean isJerarquia){
		//Order
		StringBuilder orderBy = new StringBuilder();
		if (pagination.getSidx() != null) {
			if (!isJerarquia){
				orderBy.append(" ORDER BY ");
			} else {
				orderBy.append("\n\t").append("order siblings by ");
			}
			orderBy.append(pagination.getSidx());
			orderBy.append(" ");
			orderBy.append(pagination.getSord());
			if (isJerarquia){
				orderBy.append("\n");
			}
		}
		return orderBy;
	}
	
	
	
	/**
	 * MULTISELECCION (utilidades internas)
	 */
	protected static StringBuilder getMultiselectionSelectOutter(Pagination pagination){
		return new StringBuilder().append(" , page, pageLine, tableLine "); 
	}
	protected static StringBuilder getMultiselectionSelectInner(Pagination pagination){
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
	
	public static <T extends Object> StringBuilder getSearchQuery(StringBuilder query, Pagination<T> pagination, Class<T> clazz, List<Object> paramList, String searchSQL, List<Object> searchParamList, String... pkList){	
		
		String pkStr = PaginationManager.strArrayToCommaSeparatedStr(pkList);
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("\n").append("select ").append(pkStr).append(PaginationManager.getMultiselectionSelectOutter(pagination)).append("from ( ");      
		sbSQL.append("\n\t").append("select ").append(pkStr).append(PaginationManager.getMultiselectionSelectInner(pagination)); 
		sbSQL.append("\n\t").append("from (").append(query);
			sbSQL.append("\n\t").append(PaginationManager.getOrderBy(pagination, false)).append(") ");
		sbSQL.append("\n").append(") ");
		sbSQL.append("\n").append("where 1=1 ");
			sbSQL.append("\n\t").append(searchSQL);
			
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
	
	public static <T extends Object> StringBuilder getReorderQuery(StringBuilder query, Pagination<T> pagination, Class<T> clazz, List<Object> paramList, String... pkList){	
		
		String pkStr = PaginationManager.strArrayToCommaSeparatedStr(pkList);
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("\n").append("select ").append(pkStr).append(PaginationManager.getMultiselectionSelectOutter(pagination)).append("from ( ");      
		sbSQL.append("\n\t").append("select ").append(pkStr).append(PaginationManager.getMultiselectionSelectInner(pagination)); 
		sbSQL.append("\n\t").append("from (").append(query);
			sbSQL.append("\n\t").append(PaginationManager.getOrderBy(pagination, false)).append(") ");
		sbSQL.append("\n").append(") ");
		sbSQL.append("\n").append("where ");
		
		sbSQL.append("(").append(pkStr).append(") ");
		sbSQL.append(pagination.getMultiselection().getSelectedAll()?" NOT IN ":" IN (");
		
		for (T selectedBean : pagination.getMultiselection().getSelected(clazz)) {
			sbSQL.append("(");
			for (int i = 0; i < pkList.length; i++) {
				String prop = pagination.getMultiselection().getPkNames().get(i);
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
	
	public static List<?> getPaginationList(Pagination pagination, List<?> list){
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
	
	public static StringBuilder getReorderQuery(Pagination pagination, StringBuilder query, String... pkCols){
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
		reorderQuery.append(" SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) ");
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
	
}