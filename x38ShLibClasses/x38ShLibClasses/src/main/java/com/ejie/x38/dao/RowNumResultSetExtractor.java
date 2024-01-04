package com.ejie.x38.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.ejie.x38.dto.Pagination;
import com.ejie.x38.dto.TableRequestDto;
import com.ejie.x38.dto.TableRowDto;

public class RowNumResultSetExtractor<T> implements ResultSetExtractor<List<TableRowDto<T>>> {

	private RowMapper<T> rowMapper;
	private List<String> pkColums;
	
	@Override
	public List<TableRowDto<T>> extractData(ResultSet resultSet) throws SQLException,
			DataAccessException {
		List<TableRowDto<T>> listTableRow = new ArrayList<TableRowDto<T>>();
		
		while (resultSet.next()) {
			TableRowDto<T> tableRow = new TableRowDto<T>();
			tableRow.setPage(resultSet.getInt("PAGE"));
			tableRow.setPageLine(resultSet.getInt("PAGELINE"));
			tableRow.setTableLine(resultSet.getInt("TABLELINE"));
			
			T model = rowMapper.mapRow(resultSet, resultSet.getRow());
			tableRow.setModel(model);
			try {
				for (String pk : this.pkColums) {
					tableRow.addPrimaryKey(pk, BeanUtils.getProperty(model, pk));
				}
				listTableRow.add(tableRow);
			} catch (IllegalAccessException e) {
				throw new SQLException(e);
			} catch (InvocationTargetException e) {
				throw new SQLException(e);
			} catch (NoSuchMethodException e) {
				throw new SQLException(e);
			}
		}
		return listTableRow;
	}
	
	public RowNumResultSetExtractor(RowMapper<T> rowMapper, String...pk){
		this.rowMapper = rowMapper;
		this.pkColums = Arrays.asList(pk);
	}
	
	public RowNumResultSetExtractor(RowMapper<T> rowMapper, Pagination<T> pagination){
		this.rowMapper = rowMapper;
		this.pkColums = pagination.getMultiselection().getPkNames();
	}
	
	public RowNumResultSetExtractor(RowMapper<T> rowMapper, TableRequestDto tableRequestDto){
		this.rowMapper = rowMapper;
		this.pkColums = tableRequestDto.getCore().getPkNames();
	}
	
}
