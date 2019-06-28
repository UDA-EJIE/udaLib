package com.ejie.x38.webdav.locking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



/**
 * @author UDA
 */
@Repository
@Transactional
public class DataBaseLockDaoImpl implements DataBaseLockDao {

	private Logger logger =  LoggerFactory.getLogger(DataBaseLockDaoImpl.class);
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private String lockingTableName;
	
	private RowMapper<DataBaseModel> dataBaseModelRowMapper = new RowMapper<DataBaseModel>() {
		public DataBaseModel mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {

			DataBaseModel dataBaseModel = new DataBaseModel();
			
			dataBaseModel.setId(resultSet.getString("ID"));
			dataBaseModel.setOwner(resultSet.getString("OWNER"));
			dataBaseModel.setPath(resultSet.getString("PATH"));
			dataBaseModel.setLockDepth(Integer.valueOf(resultSet.getString("LOCK_DEPTH")));
			dataBaseModel.setExpiresAt(resultSet.getLong("EXPIRES_AT"));
			dataBaseModel.setExclusiveLock(resultSet.getString("EXCLUSIVE_LOCK").equals("0")?Boolean.FALSE:Boolean.TRUE);
			dataBaseModel.setLockType(resultSet.getString("LOCK_TYPE"));
			dataBaseModel.setChildrenId(resultSet.getString("CHILDREN_ID"));
			dataBaseModel.setParentId(resultSet.getString("PARENT_ID"));
			dataBaseModel.setTempLock(resultSet.getString("TEMP_LOCK").equals("0")?Boolean.FALSE:Boolean.TRUE);
			
			return dataBaseModel;
		}
	};
	
	
	public void insert(DataBaseModel webdavLockObj){
		
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO ").append(lockingTableName).append(" ");
		query.append("(ID, OWNER, PATH, LOCK_DEPTH, EXPIRES_AT, EXCLUSIVE_LOCK, LOCK_TYPE, CHILDREN_ID, PARENT_ID, TEMP_LOCK) ");
		query.append("VALUES (:id, :owner, :path, :lockDepth, :expiresAt, :exclusiveLock, :lockType, :childrenId, :parentId, :tempLock)");
		
		SqlParameterSource beanParameterSource =  new BeanPropertySqlParameterSource(webdavLockObj);
		
		logger.debug("DataBaseLockDaoImpl.insert()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: " +webdavLockObj.toString());
		
		jdbcTemplate.update(query.toString(), beanParameterSource);
		
	}
	
	public void delete(DataBaseModel webdavLockObj){
		
		StringBuilder query = new StringBuilder();
		
		query.append("DELETE FROM ").append(lockingTableName).append(" ");
		query.append("WHERE ID=:id AND OWNER=:owner");
		
		SqlParameterSource beanParameterSource =  new BeanPropertySqlParameterSource(webdavLockObj);
		
		logger.debug("DataBaseLockDaoImpl.delete()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: " +webdavLockObj.toString());
		
		jdbcTemplate.update(query.toString(), beanParameterSource);
		
	}

	@Override
	public List<DataBaseModel> getByPath(String path, Boolean tempLock) {
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT ID, OWNER, PATH, LOCK_DEPTH, EXPIRES_AT, EXCLUSIVE_LOCK, LOCK_TYPE, CHILDREN_ID, PARENT_ID, TEMP_LOCK ");
		query.append(" FROM ").append(lockingTableName);
		query.append(" WHERE PATH = :path AND TEMP_LOCK = :tempLock");
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("path", path);
		mapParameterSource.addValue("tempLock", tempLock);
		
		logger.debug("DataBaseLockDaoImpl.getByPath()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: {path: " +path + ", tempLock: "+tempLock+"}");
		
		return jdbcTemplate.query(query.toString(), mapParameterSource, dataBaseModelRowMapper);
	}
	
	
	
	@Override
	public List<DataBaseModel> getById(String id, Boolean tempLock) {
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT ID, OWNER, PATH, LOCK_DEPTH, EXPIRES_AT, EXCLUSIVE_LOCK, LOCK_TYPE, CHILDREN_ID, PARENT_ID, TEMP_LOCK ");
		query.append(" FROM ").append(lockingTableName);
		query.append(" WHERE ID = :id AND TEMP_LOCK = :tempLock");
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("id", id);
		mapParameterSource.addValue("tempLock", tempLock);
		
		logger.debug("DataBaseLockDaoImpl.getById()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: {id: " +id + ", tempLock: "+tempLock+"}");
		
		return jdbcTemplate.query(query.toString(), mapParameterSource, dataBaseModelRowMapper);
	}

	public Boolean isLockedByPath(String path, Boolean tempLock){
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT count(1)");
		query.append(" FROM ").append(lockingTableName);
		query.append(" WHERE PATH = :path AND TEMP_LOCK=:tempLock");
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("path", path);
		mapParameterSource.addValue("tempLock", tempLock);
		
		logger.debug("DataBaseLockDaoImpl.isLockedByPath()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: {path: " +path + ", tempLock: "+tempLock+"}");
		
		return jdbcTemplate.queryForObject(query.toString(), mapParameterSource, Integer.class)>0;
	}

	@Override
	public Boolean isLockedById(String id, Boolean tempLock) {
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT count(1) ");
		query.append(" FROM ").append(lockingTableName);
		query.append(" WHERE id = :id AND TEMP_LOCK=:tempLock");
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("id", id);
		mapParameterSource.addValue("tempLock", tempLock);
		
		logger.debug("DataBaseLockDaoImpl.isLockedById()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: {id: " +id+ ", tempLock: "+tempLock+"}");
		
		return jdbcTemplate.queryForObject(query.toString(), mapParameterSource, Integer.class)>0;
	}

	@Override
	public void deleteById(String id, Boolean tempLock) {
		StringBuilder query = new StringBuilder();
		
		query.append("DELETE FROM ").append(lockingTableName); 
		query.append(" WHERE ID=:id AND TEMP_LOCK=:tempLock");
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("id", id);
		mapParameterSource.addValue("tempLock", tempLock);
		
		logger.debug("DataBaseLockDaoImpl.deleteById()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: {id: " +id+ ", tempLock: "+tempLock+"}");
		
		jdbcTemplate.update(query.toString(), mapParameterSource);
	}
	
	@Override
	public void deleteByPath(String path, Boolean tempLock) {
		StringBuilder query = new StringBuilder();
		
		query.append("DELETE FROM ").append(lockingTableName); 
		query.append(" WHERE PATH=:path AND TEMP_LOCK=:tempLock");
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("path", path);
		mapParameterSource.addValue("tempLock", tempLock);
		
		logger.debug("DataBaseLockDaoImpl.deleteByPath()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: {path: " +path+ ", tempLock: "+tempLock+"}");
		
		jdbcTemplate.update(query.toString(), mapParameterSource);
	}


	@Override
	public void removeTimeoutLocks(Boolean tempLock) {
		StringBuilder query = new StringBuilder();
		
		query.append("DELETE FROM ").append(lockingTableName); 
		query.append(" WHERE TEMP_LOCK=:tempLock AND EXPIRES_AT < :sysdate");
		
		MapSqlParameterSource mapParameterSource =  new MapSqlParameterSource();
		mapParameterSource.addValue("sysdate", Calendar.getInstance().getTime().getTime());
		mapParameterSource.addValue("tempLock", tempLock);
		
		logger.debug("DataBaseLockDaoImpl.deleteByPath()");
		logger.debug("\tSQL: " +query.toString());
		logger.debug("\tparams: {tempLock: " +tempLock+"}");
		
		jdbcTemplate.update(query.toString(), mapParameterSource);
		
	}

	public void setLockingTableName(String lockingTableName) {
		this.lockingTableName = lockingTableName;
	}
	
	public void setLockingDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}


