package com.ejie.x38.rup.table.filter.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ejie.x38.rup.table.filter.model.Filter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
@Transactional
public class FilterDaoImpl implements FilterDao {

	private static final Logger logger = LoggerFactory.getLogger(FilterDaoImpl.class);

	private NamedParameterJdbcTemplate jdbcTemplate;

	private String db_filterTableName;
	private String col_filterId;
	private String col_filterSelector;
	private String col_filterValue;
	private String col_filterName;
	private String col_filterUser;
	private String col_filterDefault;
	private String filterSeq;

	private DefaultLobHandler defaultLobHandler;

	private RowMapper<Filter> filterRowMapper = new RowMapper<Filter>() {
		public Filter mapRow(ResultSet resultSet, int rowNum) throws SQLException {

			Filter filter = new Filter();

			filter.setId(Integer.toString(resultSet.getInt(col_filterId)));
			filter.setSelector(resultSet.getString(col_filterSelector));
			filter.setText(defaultLobHandler.getClobAsString(resultSet, col_filterName));
			filter.setUser(resultSet.getString(col_filterUser));
			filter.setActive(resultSet.getBoolean(col_filterDefault));

			String data = defaultLobHandler.getClobAsString(resultSet, col_filterValue);

			try {
				// Verificar si el String es JSON antes de realizar la conversión
				if (!isJson(data)) {
					String jsonString = convertToValidJson(data);

					filter.setData(jsonString);
				} else {
					filter.setData(data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return filter;
		}
	};

	@Override
	public Filter insert(Filter filter) {

		StringBuilder query = new StringBuilder();
		query.append("SELECT ").append(filterSeq).append(".NEXTVAL FROM DUAL");
		SqlParameterSource beanParameterSource = new BeanPropertySqlParameterSource(filter);

		filter.setId(jdbcTemplate.queryForObject(query.toString(), beanParameterSource, String.class));

		beanParameterSource = new BeanPropertySqlParameterSource(filter);

		query = new StringBuilder();
		query.append("INSERT INTO ").append(db_filterTableName).append(" ");
		query.append("(").append(col_filterId).append(",").append(col_filterSelector).append(",").append(col_filterName)
				.append(",").append(col_filterDefault).append(",").append(col_filterValue).append(',')
				.append(col_filterUser).append(")");
		query.append(" VALUES (:id, :selector, :text, :active, :data, :user)");

		logger.debug("FilterDaoImpl.insert()");
		logger.debug("\tSQL: " + query.toString());
		logger.debug("\tparams: " + filter.toString());

		jdbcTemplate.update(query.toString(), beanParameterSource);

		return filter;

	}

	@Override
	public Filter update(Filter filter) {

		StringBuilder query = new StringBuilder();

		query.append("UPDATE ").append(db_filterTableName).append(" ");
		query.append(" SET ").append(col_filterDefault).append("=:active,").append(col_filterValue).append("=:data");
		query.append(" WHERE ").append(getWhereFieldsNameAndSelector());

		SqlParameterSource beanParameterSource = new BeanPropertySqlParameterSource(filter);

		logger.debug("FilterDaoImpl.update()");
		logger.debug("\tSQL: " + query.toString());
		logger.debug("\tparams: " + filter.toString());

		jdbcTemplate.update(query.toString(), beanParameterSource);
		return filter;
	}

	@Override
	public Filter delete(Filter filter) {

		StringBuilder query = new StringBuilder();

		query.append("DELETE FROM ").append(db_filterTableName).append(" ");
		query.append(" WHERE ").append(getWhereFieldsNameAndSelector());

		SqlParameterSource beanParameterSource = new BeanPropertySqlParameterSource(filter);

		logger.debug("FilterDaoImpl.delete()");
		logger.debug("\tSQL: " + query.toString());
		logger.debug("\tparams: " + filter.toString());

		jdbcTemplate.update(query.toString(), beanParameterSource);
		return filter;

	}

	@Override
	public Filter getBySelectorAndName(String selector, String text, String user) {
		StringBuilder query = new StringBuilder();

		query.append("SELECT ").append(getSelectFildsName());
		query.append(" FROM ").append(db_filterTableName);
		query.append(" WHERE ").append(getWhereFieldsNameAndSelector());

		MapSqlParameterSource mapParameterSource = new MapSqlParameterSource();
		mapParameterSource.addValue("text", text);
		mapParameterSource.addValue("selector", selector);
		mapParameterSource.addValue("user", user);

		logger.debug("FilterDaoImpl.getBySelectorAndName()");
		logger.debug("\tSQL: " + query.toString());
		logger.debug("\tparams: {text: " + text + ", selector: " + selector + "}");

		// return jdbcTemplate.queryForObject(query.toString(), mapParameterSource,
		// filterRowMapper);
		List<Filter> respuesta = jdbcTemplate.query(query.toString(), mapParameterSource, filterRowMapper);

		return (Filter) DataAccessUtils.uniqueResult(respuesta);
	}

	@Override
	public Filter getById(String id) {
		StringBuilder query = new StringBuilder();

		query.append("SELECT ").append(getSelectFildsName());
		query.append(" FROM ").append(db_filterTableName);
		query.append(" WHERE ").append(col_filterId).append(":=id");

		MapSqlParameterSource mapParameterSource = new MapSqlParameterSource();
		mapParameterSource.addValue("id", id);

		logger.debug("FilterDaoImpl.getById()");
		logger.debug("\tSQL: " + query.toString());
		logger.debug("\tparams: {id: " + id + "}");

		// return jdbcTemplate.queryForObject(query.toString(), mapParameterSource,
		// filterRowMapper);
		List<Filter> respuesta = jdbcTemplate.query(query.toString(), mapParameterSource, filterRowMapper);
		return (Filter) DataAccessUtils.uniqueResult(respuesta);
	}

//	@Override
//	public boolean isDefaultAsigned(String selector)
//			 {
//		
//		StringBuilder query = new StringBuilder();
//		
//		query.append("SELECT count(1) ");
//		query.append(" FROM ").append(db_filterTableName);
//		query.append(" WHERE ").append(col_filterSelector).append("=:filterSelector AND ").append(col_filterDefault).append("=1");
//		
//		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
//		mapParameterSource.addValue("filterSelector", selector);
//		
//		logger.debug("FilterDaoImpl.isDefaultAsigned()");
//		logger.debug("\tSQL: " +query.toString());
//		logger.debug("\tparams: { filterSelector: "+selector+"}");
//		
//		return jdbcTemplate.queryForObject(query.toString(), mapParameterSource,Integer.class)>0;
//	}

	/*
	 * public boolean isNameRepeated(String selector, String name) {
	 * 
	 * StringBuilder query = new StringBuilder();
	 * 
	 * query.append("SELECT count(1) ");
	 * query.append(" FROM ").append(db_filterTableName);
	 * query.append(" WHERE ").append(getWhereFieldsNameAndSelector());
	 * 
	 * MapSqlParameterSource mapParameterSource = new MapSqlParameterSource();
	 * mapParameterSource.addValue("filterSelector", selector);
	 * mapParameterSource.addValue("filterName", name);
	 * 
	 * 
	 * logger.debug("FilterDaoImpl.isNameRepeated()"); logger.debug("\tSQL: "
	 * +query.toString());
	 * logger.debug("\tparams: { filterSelector: "+selector+"}");
	 * 
	 * return jdbcTemplate.queryForObject(query.toString(),
	 * mapParameterSource,Integer.class)>0; }
	 */

	@Override
	public void setDefaultAsigned(String selector, String text, boolean active, String user) {

		StringBuilder query = new StringBuilder();
		int pred = 0;
		query.append("UPDATE ").append(db_filterTableName).append(" ");
		query.append("SET ").append(col_filterDefault).append("=:active");
		query.append(" WHERE ").append(getWhereFieldsNameAndSelector());

		if (active) {
			pred = 1;
		}

		MapSqlParameterSource mapParameterSource = new MapSqlParameterSource();
		mapParameterSource.addValue("active", pred);
		mapParameterSource.addValue("selector", selector);
		mapParameterSource.addValue("text", text);
		mapParameterSource.addValue("user", user);

		logger.debug("FilterDaoImpl.setDefaultAsigned()");
		logger.debug("\tSQL: " + query.toString());
		logger.debug("\tparams: {active: " + pred + " text: " + text + ", selector: " + selector + "}");

		jdbcTemplate.update(query.toString(), mapParameterSource);
	}

	@Override
	public Filter getDefaultAsigned(String selector, String user) {

		StringBuilder query = new StringBuilder();

		query.append("SELECT ").append(getSelectFildsName());
		query.append(" FROM ").append(db_filterTableName);
		query.append(" WHERE ").append(col_filterSelector).append("= :selector AND ").append(col_filterUser)
				.append("=:user AND ").append(col_filterDefault).append("=1");

		MapSqlParameterSource mapParameterSource = new MapSqlParameterSource();
		mapParameterSource.addValue("selector", selector);
		mapParameterSource.addValue("user", user);

		logger.debug("FilterDaoImpl.getDefaultAsigned()");
		logger.debug("\tSQL: " + query.toString());
		logger.debug("\tparams: { selector: " + selector + "}");
		logger.debug("\tparams: { user: " + user + "}");

		// return jdbcTemplate.queryForObject(query.toString(), mapParameterSource,
		// filterRowMapper);
		List<Filter> respuesta = jdbcTemplate.query(query.toString(), mapParameterSource, filterRowMapper);
		return (Filter) DataAccessUtils.uniqueResult(respuesta);

	}

	@Override
	public List<Filter> getAll(String selector, String user) {

		StringBuilder query = new StringBuilder();

		query.append("SELECT ").append(getSelectFildsName());
		query.append(" FROM ").append(db_filterTableName);
		query.append(" WHERE ").append(col_filterSelector).append("= :selector");
		query.append(" AND ").append(col_filterUser).append("=:user");
		query.append(" ORDER BY ").append(col_filterName);

		MapSqlParameterSource mapParameterSource = new MapSqlParameterSource();
		mapParameterSource.addValue("selector", selector);
		mapParameterSource.addValue("user", user);

		logger.debug("FilterDaoImpl.getAll()");
		logger.debug("\tSQL: " + query.toString());
		logger.debug("\tparams: { selector: " + selector + "}");
		logger.debug("\tparams: { user: " + user + "}");

		return jdbcTemplate.query(query.toString(), mapParameterSource, filterRowMapper);
	}

	public void setFilterDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.defaultLobHandler = new DefaultLobHandler();
	}

	private String getSelectFildsName() {
		StringBuilder select = new StringBuilder();
		select.append(col_filterName).append(",").append(col_filterId).append(",").append(col_filterSelector)
				.append(",").append(col_filterUser).append(",").append(col_filterValue).append(",")
				.append(col_filterDefault);
		return select.toString();
	}

	private String getWhereFieldsNameAndSelector() {
		StringBuilder text = new StringBuilder();
		text.append(col_filterSelector).append("=:selector AND ").append(col_filterName).append("=:text AND ")
				.append(col_filterUser).append("=:user");
		return text.toString();
	}

	public String getDb_filterTableName() {
		return db_filterTableName;
	}

	public void setDb_filterTableName(String db_filterTableName) {
		this.db_filterTableName = db_filterTableName;
	}

	public String getCol_filterId() {
		return col_filterId;
	}

	public void setCol_filterId(String col_filterId) {
		this.col_filterId = col_filterId;
	}

	public String getCol_filterSelector() {
		return col_filterSelector;
	}

	public void setCol_filterSelector(String col_filterSelector) {
		this.col_filterSelector = col_filterSelector;
	}

	public String getCol_filterValue() {
		return col_filterValue;
	}

	public void setCol_filterValue(String col_filterValue) {
		this.col_filterValue = col_filterValue;
	}

	public String getCol_filterName() {
		return col_filterName;
	}

	public void setCol_filterName(String col_filterName) {
		this.col_filterName = col_filterName;
	}

	public String getCol_filterDefault() {
		return col_filterDefault;
	}

	public void setCol_filterDefault(String col_filterDefault) {
		this.col_filterDefault = col_filterDefault;
	}

	public String getFilterSeq() {
		return filterSeq;
	}

	public void setFilterSeq(String filterSeq) {
		this.filterSeq = filterSeq;
	}

	public String getCol_filterUser() {
		return col_filterUser;
	}

	public void setCol_filterUser(String col_filterUser) {
		this.col_filterUser = col_filterUser;
	}

	private static boolean isJson(String str) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.readTree(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Método para convertir un String a formato JSON válido
	private static String convertToValidJson(String inputString) {
		// Dividir el string en pares key:value
		String[] pairs = inputString.split(", ");

		// Crear un mapa para almacenar las claves y valores
		Map<String, String> keyValueMap = new HashMap<String, String>();

		// Expresión regular para identificar pares key:value
		Pattern pattern = Pattern.compile("(\\w+):(\\S+)");
		for (String pair : pairs) {
			Matcher matcher = pattern.matcher(pair);
			if (matcher.matches()) {
				String key = matcher.group(1);
				String value = matcher.group(2);

				// Si la clave contiene la palabra "fecha", intentar identificar el formato y
				// convertir
				if (key.toLowerCase().contains("fecha")) {
					value = convertFecha(value);
				} else {
					// Agregar comillas dobles solo si el valor no está entre comillas
					if (!value.matches("\"[^\"]*\"")) {
						value = "\"" + value + "\"";
					}
				}

				// Almacenar en el mapa
				keyValueMap.put(key, value);
			}
		}

		// Construir el JSON
		StringBuilder jsonStringBuilder = new StringBuilder("{");
		for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
			jsonStringBuilder.append("\"").append(entry.getKey()).append("\":").append(entry.getValue()).append(",");
		}
		// Eliminar la coma final si hay al menos una entrada
		if (!keyValueMap.isEmpty()) {
			jsonStringBuilder.deleteCharAt(jsonStringBuilder.length() - 1);
		}
		jsonStringBuilder.append("}");

		return jsonStringBuilder.toString();
	}

	// Método para convertir fechas al formato deseado
	private static String convertFecha(String value) {
		try {
			// Convertir el valor directamente a fecha
			long milliseconds = Long.parseLong(value);
			Date date = new Date(milliseconds);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			return "\"" + sdf.format(date) + "\"";
		} catch (NumberFormatException e) {
			// Si no se puede analizar la fecha, mantener el valor original entre comillas
			return "\"" + value + "\"";
		}
	}

}
