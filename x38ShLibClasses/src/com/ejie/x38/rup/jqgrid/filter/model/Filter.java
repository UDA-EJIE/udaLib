package com.ejie.x38.rup.jqgrid.filter.model;


public class Filter implements java.io.Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int filterId;
	private String filterSelector;
	private String filterUser;
	private String filterName;
	private boolean filterDefault;
	private String filterValue;
	private String filterFeedback;
	
	
	
	
	
	
	public int getFilterId() {
		return filterId;
	}
	public void setFilterId(int pId) {
		this.filterId = pId;
	}
	public String getFilterSelector() {
		return filterSelector;
	}
	public void setFilterSelector(String pSelector) {
		this.filterSelector = pSelector;
	}
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String pName) {
		this.filterName = pName;
	}
	public boolean isFilterDefault() {
		return filterDefault;
	}
	public void setFilterDefault(boolean pDefault) {
		this.filterDefault = pDefault;
	}
	public String getFilterValue() {
		return filterValue;
	}
	public void setFilterValue(String pValue) {
		this.filterValue = pValue;
	}
	
	
	
	public String getFilterUser() {
		return filterUser;
	}
	public void setFilterUser(String filterUser) {
		this.filterUser = filterUser;
	}
	public String getFilterFeedback() {
		return filterFeedback;
	}
	public void setFilterFeedback(String filterFeedback) {
		this.filterFeedback = filterFeedback;
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(this.getClass().getName()).append(" Object {");
		result.append(" [ filterId: ").append(this.filterId).append(" ]");
		result.append(" [ filterSelector: ").append(this.filterSelector).append(" ]");
		result.append(" [ filterName: ").append(this.filterName).append(" ]");
		result.append(" [ filterUser: ").append(this.filterUser).append(" ]");
		result.append(" [ filterDefault: ").append(this.filterDefault).append(" ]");
		result.append(" [ filterValue: ").append(this.filterValue).append(" ]");
		result.append("}");
		return result.toString();
	}
	
	
	
	

}