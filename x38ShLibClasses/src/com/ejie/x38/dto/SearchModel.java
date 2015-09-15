package com.ejie.x38.dto;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;



public class SearchModel<T, U> {
	
	private Map<String, Object> filterParams;
	private Map<String, Object> searchParams;
//	private Pagination pagination;
	
	public SearchModel(){
		
	}
	
	public SearchModel(Map<String, Object> filterParams, Map<String, Object> searchParams){
		this.filterParams = filterParams;
		this.searchParams = searchParams;
	}	
	
	public Map<String, Object> getFilterParams() {
		return filterParams;
	}
	public void setFilterParams(Map<String, Object> filterParams) {
		this.filterParams = filterParams;
	}
	public Map<String, Object> getSearchParams() {
		return this.searchParams;
	}
	public void setSearchParams(Map<String, Object> searchParams) {
		this.searchParams = searchParams;
	}
	
//	public void setPagination(Pagination pagination) {
//		this.pagination = pagination;
//	}
	
	public T getFilterParams(Class<T> clazz) {
		return this.fromMapToBean(clazz, this.filterParams);
	}
	public U getSearchParams(Class<U> clazz) {
		return this.fromMapToBean(clazz, this.searchParams);
	}
	public Pagination getPagination() {
		return this.fromMapToBean(Pagination.class, this.filterParams);
	}
	
	@SuppressWarnings("unchecked")
	private <V> V fromMapToBean(Class<V> clazz, Map<String, Object> propertyMap){
		
		BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);
		Set<String> keySet = propertyMap.keySet();
		
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (beanWrapper.isWritableProperty(key)){
				beanWrapper.setPropertyValue(key, propertyMap.get(key));
			}
			
		}
		
		return (V) beanWrapper.getWrappedInstance();
	}
}
