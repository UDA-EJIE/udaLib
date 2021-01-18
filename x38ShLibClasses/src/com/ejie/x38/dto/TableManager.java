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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejie.x38.dao.sql.OracleEncoder;
import com.ejie.x38.dao.sql.error.SqlInjectionException;
import com.ejie.x38.util.Constants;

/**
 *
 * @author UDA
 *
 */
public class TableManager implements java.io.Serializable{

	private static final long serialVersionUID = 2127819481595995328L;

	private static final Logger logger = LoggerFactory.getLogger(TableManager.class);

	/**
	 * PAGINACIÓN
	 */
	public static <T> StringBuilder getPaginationQuery(TableRequestDto pagination, StringBuilder query){
		return getQueryForPagination(pagination, query, false, null);
    }

	public static <T> StringBuilder getPaginationQuery(TableRequestDto pagination, StringBuilder query,  boolean isJerarquia){
		return getQueryForPagination(pagination, query, isJerarquia, null);
    }

	public static <T> StringBuilder getPaginationQuery(TableRequestDto pagination, StringBuilder query,  String[] orderByWhiteList){
		return getQueryForPagination(pagination, query, false, orderByWhiteList);
    }

	public static <T> StringBuilder getPaginationQuery(TableRequestDto pagination, StringBuilder query,  boolean isJerarquia, String[] orderByWhiteList){
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

			result = result && TableManager.isInWhiteList(orderByWhiteList, field);

		}

		return result;


	}

	protected static <T> StringBuilder getQueryForPagination(TableRequestDto pagination, StringBuilder query, boolean isJerarquia, String[] orderByWhiteList){
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

	protected static <T> StringBuilder getOrderBy (TableRequestDto pagination, boolean isJerarquia){
		return TableManager.getOrderBy(pagination, isJerarquia, null);

	}

	/**
	 * ORDER BY (interno)
	 */
	protected static <T> StringBuilder getOrderBy (TableRequestDto pagination, boolean isJerarquia, String[] orderByWhiteList){
		//Order
		StringBuilder orderBy = new StringBuilder();
		if (pagination.getSidx() != null) {
			
			if(pagination.getSidx().indexOf(',') >= 0) {
				for(String sidx : pagination.getSidx().split(",")) {
					if (orderByWhiteList != null && !TableManager.validateOrderByFields(orderByWhiteList, sidx)){
						throw new SqlInjectionException("Campo no permitido");
					}
				}
			} else {
				if (orderByWhiteList != null && !TableManager.validateOrderByFields(orderByWhiteList, pagination.getSidx())){
					throw new SqlInjectionException("Campo no permitido");
				}
			}

			if (!isJerarquia){
				orderBy.append(" ORDER BY ");
			} else {
				orderBy.append("\n\t").append("order siblings by ");
			}
			if(pagination.getSidx().indexOf(',') >= 0) {
				String[] arrSidx = pagination.getSidx().split(",");
				String[] arrSord = pagination.getSord().split(",");
				
				for (int i = 0; i < arrSidx.length ; i++) {
					orderBy.append(OracleEncoder.getInstance().encode(arrSidx[i]));
					orderBy.append(" ");
					orderBy.append(OracleEncoder.getInstance().encode(arrSord[i]));
					if(i < arrSidx.length -1) {
						orderBy.append(",");
					}
					if (isJerarquia){
						orderBy.append("\n");
					}
				}
			} else {
				orderBy.append(OracleEncoder.getInstance().encode(pagination.getSidx()));
				orderBy.append(" ");
				orderBy.append(OracleEncoder.getInstance().encode(pagination.getSord()));
				if (isJerarquia){
					orderBy.append("\n");
				}
			}
		}
		return orderBy;
	}



	/**
	 * MULTISELECCION (utilidades internas)
	 */
	protected static <T> StringBuilder getMultiselectionSelectOutter(TableRequestDto pagination){
		return new StringBuilder().append(" , page, pageLine, tableLine ");
	}
	protected static <T> StringBuilder getMultiselectionSelectInner(TableRequestDto pagination){
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

	public static <T> StringBuilder getSearchQuery(StringBuilder query, TableRequestDto pagination, Class<T> clazz, List<Object> paramList, String searchSQL, List<Object> searchParamList, String... pkList){
		return TableManager.getSearchQuery(query, pagination, clazz, paramList, searchSQL, searchParamList, null, pkList);
	}

	public static <T> StringBuilder getSearchQuery(StringBuilder query, TableRequestDto pagination, Class<T> clazz, List<Object> paramList, String searchSQL, List<Object> searchParamList, List<String> tableAliases, String... pkList){

		String pkStr = TableManager.strArrayToCommaSeparatedStr(pkList);

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("\n").append("select ").append(pkStr.replaceAll("_","")).append(TableManager.getMultiselectionSelectOutter(pagination)).append("from ( ");
		sbSQL.append("\n\t").append("select SEARCH_QUERY.*").append(TableManager.getMultiselectionSelectInner(pagination));
		sbSQL.append("\n\t").append("from (").append(query);
			sbSQL.append("\n\t").append(TableManager.getOrderBy(pagination, false)).append(") SEARCH_QUERY ");
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

	public static <T extends Object> StringBuilder getReorderQuery(StringBuilder query, TableRequestDto tableRequestDto, Class<T> clazz, List<Object> paramList, String... pkList){

		String pkStr = TableManager.strArrayToCommaSeparatedStr(pkList);

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("\n").append("select ").append(pkStr).append(TableManager.getMultiselectionSelectOutter(tableRequestDto)).append("from ( ");
		sbSQL.append("\n\t").append("select ").append(pkStr).append(TableManager.getMultiselectionSelectInner(tableRequestDto));
		sbSQL.append("\n\t").append("from (").append(query);
		sbSQL.append("\n\t").append(TableManager.getOrderBy(tableRequestDto, false)).append(") ");
		sbSQL.append("\n").append(") ");
		sbSQL.append("\n").append("where ");

		sbSQL.append("(").append(pkStr).append(") IN (");
//		sbSQL.append(tableRequestDto.getMultiselection().getSelectedAll()?" NOT IN (":" IN (");
		
		// Comprobar si la lista de parámetros recibida es la misma que la aportada en pkList. 
		// Cabe decir que en los casos en los que las claves primarias sean compuestas esta condición nunca será afirmativa ya que siempre diferirán los valores recibidos y aportados.
		if (tableRequestDto.getCore().getPkNames().size() != pkList.length && !tableRequestDto.getMultiselection().getSelectedIds().get(0).contains(Constants.PK_TOKEN)) {
			TableManager.logger.info("[getReorderQuery] : La lista de parámetros recibida no es la misma que la aportada");
		}
		
		for (T selectedBean : tableRequestDto.getMultiselection().getSelected(clazz)) {
			sbSQL.append("(");
			for (String prop: pkList) {
				sbSQL.append("?").append(",");
				try {
//					paramList.add(BeanUtils.getProperty(selectedBean, prop));
                    paramList.add(new PropertyDescriptor(prop, selectedBean.getClass()).getReadMethod().invoke(selectedBean));
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IntrospectionException e) {
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

	public static <T> List<?> getPaginationList(TableRequestDto pagination, List<?> list){
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

	public static <T> StringBuilder getReorderQuery(TableRequestDto pagination, StringBuilder query, String... pkCols){
		//Order
		StringBuilder reorderQuery = new StringBuilder();
		if (pagination.getSidx() != null) {
			reorderQuery.append(" ORDER BY ");
			if(pagination.getSidx().contains(",")) {
				String[] arrSidx = pagination.getSidx().split(",");
				String[] arrSord = pagination.getSord().split(",");
				
				for (int i = 0; i < arrSidx.length ; i++) {
					reorderQuery.append(arrSidx);
					reorderQuery.append(" ");
					reorderQuery.append(arrSord);
					if(i < arrSidx.length -1) {
						reorderQuery.append(",");
					}
				}
			} else {
				reorderQuery.append(pagination.getSidx());
				reorderQuery.append(" ");
				reorderQuery.append(pagination.getSord());
				query.append(reorderQuery);
			}
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
	
	/*
	 * BORRADO MULTIPLE
	 */
	public static <T> StringBuilder getRemoveMultipleQuery(TableRequestDto tableRequestDto, Class<T> clazz, String table, String... pkCols){
		
		String pkStr = TableManager.strArrayToCommaSeparatedStr(pkCols);
		List<Object> paramList = new ArrayList<Object>();
		StringBuilder removeQuery = new StringBuilder();
		
		removeQuery.append("DELETE FROM ").append(table);
		if(!tableRequestDto.getMultiselection().getSelectedIds().isEmpty()) {
			removeQuery.append(" WHERE (").append(pkStr).append(") ")
				.append(tableRequestDto.getMultiselection().getSelectedAll()? "NOT":"").append(" IN (");
			
			// Comprobar si la lista de parámetros recibida es la misma que la aportada en pkCols.
			// Cabe decir que en los casos en los que las claves primarias sean compuestas esta condición nunca será afirmativa ya que siempre diferirán los valores recibidos y aportados.
			if (tableRequestDto.getCore().getPkNames().size() != pkCols.length && !tableRequestDto.getMultiselection().getSelectedIds().get(0).contains(Constants.PK_TOKEN)) {
				TableManager.logger.info("[getRemoveMultipleQuery] : La lista de parámetros recibida no es la misma que la aportada");
			}
			
			for (T selectedBean : tableRequestDto.getMultiselection().getSelected(clazz)) {
				removeQuery.append("(");
				for (String prop: pkCols) {
					removeQuery.append("?").append(",");
					try {
						paramList.add(BeanUtils.getProperty(selectedBean, prop));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}

				removeQuery.deleteCharAt(removeQuery.length()-1);
				removeQuery.append("),");
			}
			
			removeQuery.deleteCharAt(removeQuery.length()-1);
			removeQuery.append(")");
		}

		return removeQuery;
	}

	/*
	 * BORRADO MULTIPLE
	 */
	@Deprecated
	public static <T> StringBuilder getRemoveMultipleQuery(TableRequestDto tableRequestDto, Class<T> clazz, StringBuilder query, List<Object> paramList, String... pkCols){

		String pkStr = TableManager.strArrayToCommaSeparatedStr(pkCols);

		StringBuilder removeQuery = new StringBuilder();

		removeQuery.append("DELETE FROM (").append(query).append(") ");
		removeQuery.append(" WHERE (").append(pkStr).append(") ").append(tableRequestDto.getMultiselection().getSelectedAll()?" NOT ":"").append(" IN (");
//		sbSQL.append(tableRequestDto.getMultiselection().getSelectedAll()?" NOT IN (":" IN (");
		for (T selectedBean : tableRequestDto.getMultiselection().getSelected(clazz)) {
			removeQuery.append("(");
			for (int i = 0; i < pkCols.length; i++) {
				String prop = tableRequestDto.getCore().getPkNames().get(i);
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
	public static <T> StringBuilder getSelectMultipleQuery(TableRequestDto tableRequestDto, Class<T> clazz, List<Object> paramList, String... pkCols){

		String pkStr = TableManager.strArrayToCommaSeparatedStr(pkCols);

		StringBuilder selectQuery = new StringBuilder();
		
		if(!tableRequestDto.getMultiselection().getSelectedIds().isEmpty()) {
			selectQuery.append(" AND (").append(pkStr).append(") ")
				.append(tableRequestDto.getMultiselection().getSelectedAll()? "NOT":"").append(" IN (");
			
			// Comprobar si la lista de parámetros recibida es la misma que la aportada en pkCols.
			// Cabe decir que en los casos en los que las claves primarias sean compuestas esta condición nunca será afirmativa ya que siempre diferirán los valores recibidos y aportados.
			if (tableRequestDto.getCore().getPkNames().size() != pkCols.length && !tableRequestDto.getMultiselection().getSelectedIds().get(0).contains(Constants.PK_TOKEN)) {
				TableManager.logger.info("[getSelectMultipleQuery] : La lista de parámetros recibida no es la misma que la aportada");
			}
			
			for (T selectedBean : tableRequestDto.getMultiselection().getSelected(clazz)) {
				selectQuery.append("(");
				for (String prop: pkCols) {
					selectQuery.append("?").append(",");
					try {
	                    paramList.add(new PropertyDescriptor(prop, selectedBean.getClass()).getReadMethod().invoke(selectedBean));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (IntrospectionException e) {
	                    e.printStackTrace();
	                }
	            }

				selectQuery.deleteCharAt(selectQuery.length()-1);
				selectQuery.append("),");
			}
			
			selectQuery.deleteCharAt(selectQuery.length()-1);
			selectQuery.append(")");
		}

		return selectQuery;

	}

}
