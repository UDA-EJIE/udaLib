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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejie.x38.dao.sql.OracleEncoder;
import com.ejie.x38.dao.sql.error.SqlInjectionException;

/**
 *
 * @author UDA
 *
 */
public class TableManager implements java.io.Serializable{

	private static final long serialVersionUID = 2127819481595995328L;

	private static final Logger logger = LoggerFactory.getLogger(TableManager.class);

	// Caches para optimización de rendimiento en introspección.
	private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();
	private static final Map<String, PropertyDescriptor> PROPERTY_DESCRIPTOR_CACHE = new ConcurrentHashMap<>();

	/**
	 * PAGINACIÃ“N
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

		// Comprobamos si la cadena de ordenaciÃ³n contiene varios campos


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
			boolean isColumnIndex = false;
			
			try {
		        Integer.parseInt(pagination.getSidx());
		        isColumnIndex = true;
		    } catch (NumberFormatException nfe) {}
			
			if(pagination.getSidx().indexOf(',') >= 0) {
				for(String sidx : pagination.getSidx().split(",")) {
					if (!isColumnIndex && orderByWhiteList != null && !TableManager.validateOrderByFields(orderByWhiteList, sidx)){
						throw new SqlInjectionException("Campo no permitido");
					}
				}
			} else {
				if (!isColumnIndex && orderByWhiteList != null && !TableManager.validateOrderByFields(orderByWhiteList, pagination.getSidx())){
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
		return new StringBuilder().append(" , ceil(rownum/").append(pagination.getRows()).append(") as page, rownum - ((ceil(rownum/").append(pagination.getRows()).append(") - 1) * ").append(pagination.getRows()).append(") as pageLine, rownum as tableLine ");
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

		String pkStr = (TableManager.strArrayToCommaSeparatedStr(pkList)).toUpperCase();

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("\n").append("select ").append(pkStr).append(TableManager.getMultiselectionSelectOutter(pagination)).append("from ( ");
		sbSQL.append("\n\t").append("select SEARCH_QUERY.*").append(TableManager.getMultiselectionSelectInner(pagination));
		sbSQL.append("\n\t").append("from (").append(query);
			sbSQL.append("\n\t").append(TableManager.getOrderBy(pagination, false)).append(") SEARCH_QUERY ");
		sbSQL.append("\n").append(") ");
		sbSQL.append("\n").append("where 1=1 ");

		for (String tableAlias : tableAliases) {
			searchSQL = searchSQL.replaceAll("(?i)"+tableAlias.trim()+"\\.", "").trim();
		}
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

	public static <T extends Object> StringBuilder getReorderQuery(StringBuilder query, TableRequestDto tableRequestDto, Class<T> clazz, List<Object> paramList, String... pkList){

		String pkStr = (TableManager.strArrayToCommaSeparatedStr(pkList)).toUpperCase();

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("\n").append("select ").append(pkStr).append(TableManager.getMultiselectionSelectOutter(tableRequestDto)).append("from ( ");
		sbSQL.append("\n\t").append("select ").append(pkStr).append(TableManager.getMultiselectionSelectInner(tableRequestDto));
		sbSQL.append("\n\t").append("from (").append(query);
		sbSQL.append("\n\t").append(TableManager.getOrderBy(tableRequestDto, false)).append(") ");
		sbSQL.append("\n").append(") ");
		if(tableRequestDto.getMultiselection().getSelectedAll() && tableRequestDto.getMultiselection().getSelectedIds().size() == 0){
			return sbSQL;
		}
		sbSQL.append("\n").append("where ");

		sbSQL.append("(").append(pkStr).append(") IN (");
//		sbSQL.append(tableRequestDto.getMultiselection().getSelectedAll()?" NOT IN (":" IN (");
		
		for (T selectedBean : tableRequestDto.getMultiselection().getSelected(clazz)) {
			sbSQL.append("(");
			for (String pk : pkList) {
				sbSQL.append("?").append(",");
				try {
					
					// Se obtiene el valor de la pk declarada.
					paramList.add(getCampoByIntrospection(clazz, selectedBean, pk));
					
				} catch (IllegalAccessException e) {
					TableManager.logger.error(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					TableManager.logger.error(e.getMessage(), e);
				} catch (IntrospectionException e) {
					TableManager.logger.error(e.getMessage(), e);
				}
			}

			sbSQL.deleteCharAt(sbSQL.length()-1);
			sbSQL.append("),");
		}

		sbSQL.deleteCharAt(sbSQL.length()-1);
		sbSQL.append(")");

		return sbSQL;
	}

	/**
	 * Str array to comma separated str.
	 *
	 * @param strArray the str array
	 * @return the string
	 */
	private static String strArrayToCommaSeparatedStr(String[] strArray){
		StringBuilder retStr = new StringBuilder();
		for (String str : strArray) {
			retStr.append(str).append(",");
		}
		retStr.deleteCharAt(retStr.length()-1);

		return retStr.toString();
	}

	/**
	 * Gets the pagination list.
	 *
	 * @param <T> the generic type
	 * @param pagination the pagination
	 * @param list the list
	 * @return the pagination list
	 */
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

	public static <T> StringBuilder getReorderQuery(TableRequestDto pagination, StringBuilder query, String... pkList){
		//Order
		StringBuilder reorderQuery = new StringBuilder();
		if (pagination.getSidx() != null) {
			reorderQuery.append(" ORDER BY ");
			if(pagination.getSidx().contains(",")) {
				String[] arrSidx = pagination.getSidx().split(",");
				String[] arrSord = pagination.getSord().split(",");
				
				for (int i = 0; i < arrSidx.length ; i++) {
					reorderQuery.append(arrSidx[i]);
					reorderQuery.append(" ");
					reorderQuery.append(arrSord[i]);
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
//		for (String pkCol : pkList) {mu
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
	
	/**
	 * Crea una consulta de eliminaciÃ³n mÃºltiple teniendo en cuenta el filtro (en caso de haberlo).
	 *
	 * @param Map<?, ?> Mapa que contiene la query where like
	 * @param TableRequestDto Dto que contiene los parÃ¡metros de configuraciÃ³n propios del RUP_TABLE
	 * @param Class<T> Tipo de clase
	 * @param String Nombre de la tabla a tratar
	 * @param String Alias usado en la query
	 * @param String... Strings que forman la clave primaria
	 * 
	 * @return StringBuilder Query que permite eliminar mÃºltiples registros de la tabla
	 */
	public static <T> StringBuilder getRemoveMultipleQuery(Map<?, ?> mapaWhereLike, TableRequestDto tableRequestDto, Class<T> clazz, String table, String alias, String... pkList) {
		String pkStr = (TableManager.strArrayToCommaSeparatedStr(pkList)).toUpperCase();
		List<Object> paramList = new ArrayList<Object>();
		StringBuilder removeQuery = new StringBuilder();
		
		removeQuery.append("DELETE FROM ").append(table).append(" ").append(alias).append(" WHERE 1=1 ");
		
		// Comprobar si el mapa no es nulo y tiene contenido
		if (mapaWhereLike != null && !mapaWhereLike.isEmpty()) {
			removeQuery.append(mapaWhereLike.get("query"));
		}
		
		if (!tableRequestDto.getMultiselection().getSelectedIds().isEmpty()) {
			removeQuery.append(" AND (").append(alias).append(".").append(pkStr).append(") ").append(tableRequestDto.getMultiselection().getSelectedAll() ? "NOT" : "").append(" IN (");
		
			for (T selectedBean : tableRequestDto.getMultiselection().getSelected(clazz)) {
				removeQuery.append("(");
				for (String pk : pkList) {
					removeQuery.append("?").append(",");
					
					try {
						// Se obtiene el valor de la pk declarada.
						paramList.add(getCampoByIntrospection(clazz, selectedBean, pk));
					} catch (IllegalAccessException e) {
						TableManager.logger.error(e.getMessage(), e);
					} catch (InvocationTargetException e) {
						TableManager.logger.error(e.getMessage(), e);
					} catch (IntrospectionException e) {
						TableManager.logger.error(e.getMessage(), e);
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
	
	public static <T> StringBuilder getSelectMultipleQuery(TableRequestDto tableRequestDto, Class<T> clazz, List<Object> paramList, String[] orderByWhiteList, String... pkList){

		String pkStr = (TableManager.strArrayToCommaSeparatedStr(pkList)).toUpperCase();

		StringBuilder selectQuery = new StringBuilder();
		
		if(!tableRequestDto.getMultiselection().getSelectedIds().isEmpty()) {
			selectQuery.append(" AND (").append(pkStr).append(") ")
				.append(tableRequestDto.getMultiselection().getSelectedAll()? "NOT":"").append(" IN (");
			
			for (T selectedBean : tableRequestDto.getMultiselection().getSelected(clazz)) {
				selectQuery.append("(");
				for (String pk : pkList) {
					selectQuery.append("?").append(",");
					
					try {
						// Se obtiene el valor de la pk declarada.
						paramList.add(getCampoByIntrospection(clazz, selectedBean, pk.replace("_", "")));
					} catch (IllegalAccessException e) {
						TableManager.logger.error(e.getMessage(), e);
					} catch (InvocationTargetException e) {
						TableManager.logger.error(e.getMessage(), e);
					} catch (IntrospectionException e) {
						TableManager.logger.error(e.getMessage(), e);
					}
				}

				selectQuery.deleteCharAt(selectQuery.length()-1);
				selectQuery.append("),");
			}
			
			selectQuery.deleteCharAt(selectQuery.length()-1);
			selectQuery.append(")");
			
			selectQuery.append(getOrderBy(tableRequestDto, false, orderByWhiteList));
		}

		return selectQuery;

	}
	
	/**
	 * Obtiene el valor de un campo específico de un objeto mediante introspección,
	 * navegando por propiedades anidadas si es necesario.
	 *
	 * <p>Este método utiliza introspección para acceder a campos de un objeto, soportando
	 * navegación por propiedades anidadas mediante notación de puntos (ej: "usuario.direccion.calle").
	 * Incluye lógica de matching flexible que intenta coincidir nombres de campos tanto
	 * con guiones bajos como sin ellos para mayor compatibilidad.</p>
	 *
	 * <p><strong>Características:</strong></p>
	 * <ul>
	 *   <li>Soporte para propiedades anidadas usando notación de puntos</li>
	 *   <li>Matching flexible: intenta nombres con y sin guiones bajos</li>
	 *   <li>Cache interno de PropertyDescriptors para mejor rendimiento</li>
	 *   <li>Comparación case-insensitive de nombres de campos</li>
	 * </ul>
	 *
	 * <p><strong>Ejemplos de uso:</strong></p>
	 * <pre>
	 * // Campo simple
	 * Object nombre = getCampoByIntrospection(Usuario.class, usuario, "nombre");
	 *
	 * // Campo anidado
	 * Object calle = getCampoByIntrospection(Usuario.class, usuario, "direccion.calle");
	 *
	 * // Campo con guiones bajos (busca "user_name" y "username")
	 * Object userName = getCampoByIntrospection(Usuario.class, usuario, "user_name");
	 * </pre>
	 *
	 * @param <T> el tipo de la clase del objeto
	 * @param clazz la clase del objeto sobre el que realizar la introspección
	 * @param selectedBean el objeto del cual extraer el valor del campo
	 * @param pk la clave del campo a obtener, soporta notación de puntos para campos anidados
	 *           (ej: "campo", "objeto.campo", "objeto.subObjeto.campo")
	 *
	 * @return el valor del campo especificado, puede ser de cualquier tipo según el campo accedido
	 *
	 * @throws IllegalAccessException si no se puede acceder al método getter del campo
	 * @throws IllegalArgumentException si los argumentos proporcionados no son válidos
	 * @throws InvocationTargetException si ocurre una excepción al invocar el método getter
	 * @throws IntrospectionException si la clave especificada no corresponde con ningún campo
	 *                                en la clase o si hay problemas creando el PropertyDescriptor
	 * @throws IllegalStateException si hay problemas internos creando PropertyDescriptors para el cache
	 *
	 * @see PropertyDescriptor
	 * @see java.beans.Introspector
	 */
	private static <T> Object getCampoByIntrospection(Class<T> clazz, T selectedBean, String pk)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		
		logger.debug("Starting introspection for class {} with pk {}", clazz.getName(), pk);
		
		// Validaciones previas.
		if (selectedBean == null) throw new IllegalArgumentException("selectedBean cannot be null");
		if (pk == null) throw new IllegalArgumentException("pk cannot be null");
		
		var trimmedPk = pk.trim();
		if (trimmedPk.isEmpty()) throw new IllegalArgumentException("pk cannot be empty");
		if (trimmedPk.startsWith(".") || trimmedPk.endsWith(".") || trimmedPk.contains("..")) {
			throw new IllegalArgumentException(
				String.format("pk has invalid format: '%s'. Cannot start/end with dot or contain consecutive dots.", pk));
		}
		
		var pkFieldNames = trimmedPk.split("\\.", 0);
		Object currentObject = selectedBean;
		Class<?> currentClass = clazz;
		
		// Navegación con caché.
		for (var pkFieldName : pkFieldNames) {
			logger.trace("Searching field {} in class {}", pkFieldName, currentClass.getName());
			var classFields = FIELD_CACHE.computeIfAbsent(currentClass, TableManager::buildFieldMap);
			var field = findFieldInMap(classFields, pkFieldName);
			
			if (field == null) {
				logger.warn("Field {} not found in class {}", pkFieldName, currentClass.getName());
				throw new IntrospectionException(
					String.format("The specified key \"%s\" has no correspondence in class \"%s\".", 
								trimmedPk, currentClass.getName()));
			}
			logger.trace("Field found: {} -> {}", pkFieldName, field.getName());
			
			// Capturar variables antes de la lambda.
			var finalCurrentClass = currentClass;
			var finalFieldName = field.getName();
			var cacheKey = finalCurrentClass.getName() + "." + finalFieldName;
			
			var descriptor = PROPERTY_DESCRIPTOR_CACHE.computeIfAbsent(cacheKey, descriptorKey -> {
				try {
					return new PropertyDescriptor(finalFieldName, finalCurrentClass);
				} catch (IntrospectionException e) {
					throw new IllegalStateException(
						String.format("Error creating PropertyDescriptor for %s", descriptorKey), e);
				}
			});
			
			currentObject = descriptor.getReadMethod().invoke(currentObject);
			if (currentObject == null) {
				logger.trace("Null value found in field {}, ending navigation", pkFieldName);
				return null;
			}
			
			currentClass = currentObject.getClass();
		}
		
		logger.debug("Introspection completed successfully for class {} with pk {}", clazz.getName(), pk);
		return currentObject;
	}

	/**
	 * Construye un mapa de campos para una clase, incluyendo herencia.
	 * 
	 * <p>Este método recorre la jerarquía de herencia de la clase proporcionada,
	 * recolectando todos los campos declarados y creando un mapa indexado por
	 * el nombre del campo en minúsculas para facilitar búsquedas case-insensitive.</p>
	 * 
	 * <p>Los campos de las subclases tienen prioridad sobre los de las superclases
	 * en caso de nombres duplicados (usando putIfAbsent).</p>
	 * 
	 * @param clazz la clase de la cual construir el mapa de campos
	 * @return mapa con los campos indexados por nombre en minúsculas
	 */
	private static Map<String, Field> buildFieldMap(Class<?> clazz) {
		logger.debug("Building field map for class {}", clazz.getName());
		
		var fieldMap = new HashMap<String, Field>();
		var fieldCount = 0;
		
		var currentClass = clazz;
		while (currentClass != null && currentClass != Object.class) {
			var classFields = currentClass.getDeclaredFields();
			logger.trace("Processing {} fields from class {}", classFields.length, currentClass.getName());
			
			for (Field field : classFields) {
				var fieldKey = field.getName().toLowerCase();
				if (fieldMap.putIfAbsent(fieldKey, field) == null) {
					fieldCount++;
				}
			}
			currentClass = currentClass.getSuperclass();
		}
		
		logger.debug("Field map built for {}. {} total fields", clazz.getName(), fieldCount);
		return fieldMap;
	}

	/**
	 * Busca un campo en el mapa probando diferentes variantes del nombre.
	 * 
	 * <p>Implementa la lógica de matching flexible que intenta encontrar campos
	 * tanto con el nombre original como con una versión "limpia" sin guiones bajos.
	 * Esto proporciona compatibilidad con diferentes convenciones de nomenclatura.</p>
	 * 
	 * <p>Orden de búsqueda:</p>
	 * <ol>
	 *   <li>Nombre sin guiones bajos (ej: "username" para "user_name")</li>
	 *   <li>Nombre original (ej: "user_name")</li>
	 * </ol>
	 * 
	 * @param classFields mapa de campos de la clase indexado por nombre en minúsculas
	 * @param fieldName nombre del campo a buscar (puede contener guiones bajos)
	 * @return el campo encontrado o null si no existe ninguna variante
	 */
	private static Field findFieldInMap(Map<String, Field> classFields, String fieldName) {
		var originalName = fieldName.toLowerCase();
		var cleanName = fieldName.replace("_", "").toLowerCase();
		
		// Intentar primero sin guiones bajos (caso más común en Java).
		var field = classFields.get(cleanName);
		
		// Si no se encuentra, intentar con el nombre original.
		return field != null ? field : classFields.get(originalName);
	}

	/**
	 * Limpia los cachés de introspección para liberar memoria.
	 * 
	 * <p>Los cachés se reconstruirán automáticamente en las siguientes llamadas.
	 * Útil en aplicaciones de larga duración, después de hot-reload de clases,
	 * entre tests unitarios, o cuando se necesita liberar memoria.</p>
	 * 
	 * <p><strong>Ejemplos de uso:</strong></p>
	 * <pre>
	 * // Limpieza básica
	 * String resultado = TableManager.clearIntrospectionCaches();
	 * 
	 * // En endpoint de administración
	 * {@code @PostMapping("/admin/clear-cache")}
	 * public String clearCache() {
	 *     return TableManager.clearIntrospectionCaches();
	 * }
	 * 
	 * // Limpieza automática cada hora
	 * {@code @Scheduled(fixedRate = 3600000)}
	 * public void limpiezaPeriodicaCaches() {
	 *     TableManager.clearIntrospectionCaches();
	 * }
	 * 
	 * // En tests unitarios
	 * {@code @AfterEach}
	 * void limpiarCaches() {
	 *     TableManager.clearIntrospectionCaches();
	 * }
	 * </pre>
	 * 
	 * @return estadísticas de limpieza para logging/monitoreo
	 * @see #getCacheStats()
	 */
	public static String clearIntrospectionCaches() {
		var fieldCacheSize = FIELD_CACHE.size();
		var descriptorCacheSize = PROPERTY_DESCRIPTOR_CACHE.size();
		
		FIELD_CACHE.clear();
		PROPERTY_DESCRIPTOR_CACHE.clear();
		
		var stats = String.format("Caches cleared - Fields: %d classes, PropertyDescriptors: %d entries", 
								fieldCacheSize, descriptorCacheSize);
		
		logger.info("{}", stats);
		return stats;
	}

	/**
	 * Obtiene estadísticas actuales de los cachés sin modificarlos.
	 * 
	 * <p>Proporciona información sobre el estado actual de los cachés para
	 * monitoreo, debugging y decisiones sobre limpieza de memoria.</p>
	 * 
	 * <p><strong>Ejemplos de uso:</strong></p>
	 * <pre>
	 * // Consulta básica
	 * String stats = TableManager.getCacheStats();
	 * 
	 * // En endpoint de administración
	 * {@code @GetMapping("/admin/cache-stats")}
	 * public String getCacheStatus() {
	 *     return TableManager.getCacheStats();
	 * }
	 * 
	 * // Logging periódico
	 * {@code @Scheduled(fixedRate = 300000)}
	 * public void logCacheStats() {
	 *     logger.info("Estado cachés: {}", TableManager.getCacheStats());
	 * }
	 * </pre>
	 * 
	 * @return información sobre el tamaño actual de los cachés
	 * @see #clearIntrospectionCaches()
	 */
	public static String getCacheStats() {
		return String.format("Current caches - Fields: %d classes, PropertyDescriptors: %d entries", 
				FIELD_CACHE.size(), PROPERTY_DESCRIPTOR_CACHE.size());
	}
}
