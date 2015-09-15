package com.ejie.x38.dto;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class TableRowDto<T> {

	private Map<String, String> pkMap = new HashMap<String, String>();
	
	private Integer page;
	private Integer pageLine;
	private Integer tableLine;
	
	@JsonIgnore
	private T model;
	
	
	public TableRowDto() {
		super();
	}
	
	/**
	 * @param model
	 */
	public TableRowDto(T model) {
		super();
		this.model = model;
	}

	/**
	 * @param pkMap
	 * @param page
	 * @param pageLine
	 * @param tableLine
	 */
	public TableRowDto(Map<String, String> pkMap, Integer page,
			Integer pageLine, Integer tableLine) {
		super();
		this.pkMap = pkMap;
		this.page = page;
		this.pageLine = pageLine;
		this.tableLine = tableLine;
	}

	/**
	 * @param pkMap
	 * @param page
	 * @param pageLine
	 * @param tableLine
	 * @param model
	 */
	public TableRowDto(Map<String, String> pkMap, Integer page,
			Integer pageLine, Integer tableLine, T model) {
		super();
		this.pkMap = pkMap;
		this.page = page;
		this.pageLine = pageLine;
		this.tableLine = tableLine;
		this.model = model;
	}

	@JsonProperty("pk")
	public Map<String, String> getPkMap() {
		return pkMap;
	}
	public void setPkMap(Map<String, String> pkMap) {
		this.pkMap = pkMap;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getPageLine() {
		return pageLine;
	}
	public void setPageLine(Integer pageLine) {
		this.pageLine = pageLine;
	}
	public Integer getTableLine() {
		return tableLine;
	}
	public void setTableLine(Integer tableLine) {
		this.tableLine = tableLine;
	}
	public T getModel() {
		return model;
	}
	public void setModel(T model) {
		this.model = model;
	}
	
	
	public void addPrimaryKey(String pkName, String pkValue){
		this.pkMap.put(pkName, pkValue);
	}
}
