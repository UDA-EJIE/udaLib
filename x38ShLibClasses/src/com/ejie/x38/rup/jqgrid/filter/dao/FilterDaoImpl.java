package com.ejie.x38.rup.jqgrid.filter.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

import com.ejie.x38.rup.jqgrid.filter.model.Filter;


@Repository
@Transactional
public class FilterDaoImpl implements FilterDao{
	
	

	private Logger logger =  LoggerFactory.getLogger(FilterDaoImpl.class);
	
	
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
		public Filter mapRow(ResultSet resultSet, int rowNum) throws SQLException
				 {
			
			

			Filter filtro = new Filter();
			
			filtro.setFilterId(resultSet.getInt(col_filterId));
			filtro.setFilterSelector(resultSet.getString(col_filterSelector));
			filtro.setFilterName(defaultLobHandler.getClobAsString(resultSet,col_filterName));
			filtro.setFilterUser(resultSet.getString(col_filterUser));
			filtro.setFilterDefault(resultSet.getBoolean(col_filterDefault));
			filtro.setFilterValue(defaultLobHandler.getClobAsString(resultSet,
					col_filterValue));
		
			return filtro;
		}
	};
	
	
	
	

	@Override
	public Filter insert(Filter filtro)  {

		StringBuilder query = new StringBuilder();
		query.append("SELECT ").append(filterSeq).append(".NEXTVAL FROM DUAL");
		SqlParameterSource beanParameterSource =  new BeanPropertySqlParameterSource(filtro);

		filtro.setFilterId(jdbcTemplate.queryForInt(query.toString(), beanParameterSource));
		
		beanParameterSource=  new BeanPropertySqlParameterSource(filtro);

		
		
		query = new StringBuilder();
		query.append("INSERT INTO ").append(db_filterTableName).append(" ");
		query.append("(").append(col_filterId).append(",").append(col_filterSelector).append(",").append(col_filterName).append(",")
		.append(col_filterDefault).append(",").append(col_filterValue).append(',').append(col_filterUser).append(")");
		query.append(" VALUES (:filterId, :filterSelector, :filterName, :filterDefault, :filterValue, :filterUser)");
		
		
		logger.debug("FilterDaoImpl.insert()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: " +filtro.toString());
		
		jdbcTemplate.update(query.toString(), beanParameterSource);
		
		return filtro;
		
	}

	@Override
	public Filter update(Filter filtro) {
		
	StringBuilder query = new StringBuilder();
		
		query.append("UPDATE ").append(db_filterTableName).append(" ");
		query.append(" SET ").append(col_filterDefault).append("=:filterDefault,").append(col_filterValue).append("=:filterValue");
		query.append(" WHERE ").append(getWhereFieldsNameAndSelector());
		
		SqlParameterSource beanParameterSource =  new BeanPropertySqlParameterSource(filtro);
		
		logger.debug("FilterDaoImpl.update()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: " +filtro.toString());
		
		jdbcTemplate.update(query.toString(), beanParameterSource);
		return filtro;
}

	@Override
	public Filter delete(Filter filtro)  {
		
		StringBuilder query = new StringBuilder();
		
		query.append("DELETE FROM ").append(db_filterTableName).append(" ");
		query.append(" WHERE ").append(getWhereFieldsNameAndSelector());
		
		SqlParameterSource beanParameterSource =  new BeanPropertySqlParameterSource(filtro);
		
		logger.debug("FilterDaoImpl.delete()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: " +filtro.toString());
		
		jdbcTemplate.update(query.toString(), beanParameterSource);
		return filtro;
		
	}

	@Override
	public Filter getBySelectorAndName(String selector, String name, String user)  {
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT ").append(getSelectFildsName());
		query.append(" FROM ").append(db_filterTableName);
		query.append(" WHERE ").append(getWhereFieldsNameAndSelector());
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("filterName", name);
		mapParameterSource.addValue("filterSelector", selector);
		mapParameterSource.addValue("filterUser", user);

		
		logger.debug("FilterDaoImpl.getBySelectorAndName()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: {filterName: " +name + ", filterSelector: "+selector+"}");
		
		//return  jdbcTemplate.queryForObject(query.toString(), mapParameterSource, filterRowMapper);
		List<Filter> respuesta= jdbcTemplate.query(query.toString(), mapParameterSource, filterRowMapper);
		
		return (Filter)DataAccessUtils.uniqueResult(respuesta);
	}
	
	@Override
	public Filter getById(String filterId)  {
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT ").append(getSelectFildsName());
		query.append(" FROM ").append(db_filterTableName);
		query.append(" WHERE ").append(col_filterId).append(":=filterId");
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("filterId", filterId);
		
		logger.debug("FilterDaoImpl.getById()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: {filterId: " +filterId +"}");
		
		//return  jdbcTemplate.queryForObject(query.toString(), mapParameterSource, filterRowMapper);
		List<Filter> respuesta= jdbcTemplate.query(query.toString(), mapParameterSource, filterRowMapper);
		return (Filter)DataAccessUtils.uniqueResult(respuesta);
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
	
	
/*	public boolean isNameRepeated(String selector, String name)
	 {

			StringBuilder query = new StringBuilder();
	
			query.append("SELECT count(1) ");
			query.append(" FROM ").append(db_filterTableName);
			query.append(" WHERE ").append(getWhereFieldsNameAndSelector());
	
			MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
			mapParameterSource.addValue("filterSelector", selector);
			mapParameterSource.addValue("filterName", name);

	
			logger.debug("FilterDaoImpl.isNameRepeated()");
			logger.debug("\tSQL: " +query.toString());
			logger.debug("\tparams: { filterSelector: "+selector+"}");
	
			return jdbcTemplate.queryForObject(query.toString(), mapParameterSource,Integer.class)>0;
	 }*/
	
	
	
	@Override
	public void setDefaultAsigned(String selector, String name,
			boolean pDefault, String user)  {
		
		StringBuilder query = new StringBuilder();
		int pred=0;
		query.append("UPDATE ").append(db_filterTableName).append(" ");
		query.append("SET ").append(col_filterDefault).append("=:filterDefault");
		query.append(" WHERE ").append(getWhereFieldsNameAndSelector());
		
		if (pDefault){
			pred=1;
		}
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("filterDefault",pred);
		mapParameterSource.addValue("filterSelector", selector);
		mapParameterSource.addValue("filterName", name);
		mapParameterSource.addValue("filterUser", user);

		
		logger.debug("FilterDaoImpl.setDefaultAsigned()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: {filterDefault: "+pred+" filterName: " +name+ ", filterSelector: "+selector+"}");
		
		jdbcTemplate.update(query.toString(), mapParameterSource);
	}

	@Override
	public Filter getDefaultAsigned(String selector, String user)
			 {
		
		StringBuilder query = new StringBuilder();

		
		query.append("SELECT ").append(getSelectFildsName());
		query.append(" FROM ").append(db_filterTableName);
		query.append(" WHERE ").append(col_filterSelector).append("= :filterSelector AND ").append(col_filterUser).append("=:filterUser AND ").append(col_filterDefault).append("=1");
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("filterSelector", selector);
		mapParameterSource.addValue("filterUser", user);

		
		logger.debug("FilterDaoImpl.getDefaultAsigned()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: { filterSelector: "+selector+"}");
		logger.debug("\tparams: { filterUser: "+user+"}");

		
		//return  jdbcTemplate.queryForObject(query.toString(), mapParameterSource, filterRowMapper);
		List<Filter> respuesta= jdbcTemplate.query(query.toString(), mapParameterSource, filterRowMapper);
		return (Filter)DataAccessUtils.uniqueResult(respuesta);
		
	}
	
	

	@Override
	public List<Filter> getAll(String selector, String user)  {

		StringBuilder query = new StringBuilder();

		query.append("SELECT ").append(getSelectFildsName());
		query.append(" FROM ").append(db_filterTableName);
		query.append(" WHERE ").append(col_filterSelector).append("= :filterSelector");
		query.append(" AND ").append(col_filterUser).append("=:filterUser");
		query.append(" ORDER BY ").append(col_filterName);
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("filterSelector", selector);
		mapParameterSource.addValue("filterUser", user);

		
		logger.debug("FilterDaoImpl.getAll()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: { filterSelector: "+selector+"}");
		logger.debug("\tparams: { filterUser: "+user+"}");

		return  jdbcTemplate.query(query.toString(), mapParameterSource, filterRowMapper);
	}

	
	
	
	
	public void setFilterDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.defaultLobHandler = new DefaultLobHandler();

		
	}

	
	private String getSelectFildsName(){
		StringBuilder select =new StringBuilder();
			select.append(col_filterName).append(",").append(col_filterId).append(",").append(col_filterSelector).append(",").append(col_filterUser).append(",").append(col_filterValue).append(",").append(col_filterDefault);
		return select.toString();
	}
	
	
	private String getWhereFieldsNameAndSelector(){
		StringBuilder text =new StringBuilder();
		text.append(col_filterSelector).append("=:filterSelector AND ").append(col_filterName).append("=:filterName AND  ").append(col_filterUser).append("=:filterUser");
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
	
	
	


}


