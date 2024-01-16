package com.ejie.x38.dto;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;


/**
 * DTO encargado de facilitar la recogida de los parámetros de filtrado y búsqueda en la acción search del componente tabla.
 * 
 * @author UDA
 *
 * @param <T> Tipo de bean utlizado para recoger los parámetros de filtado.
 * @param <U> Tipo de bean utilizado para recoger los parámetros de búsqueda.
 */
public class SearchModelMapDto<T, U> {
	
	private Map<String, Object> filterParams;
	private Map<String, Object> searchParams;
	
	/**
	 * Constructor.
	 */
	public SearchModelMapDto(){
	}
	
	/**
	 * Constructor.
	 * 
	 * @param filterParams
	 *            Parámetros de filtrado.
	 * @param searchParams
	 *            Parámetros de búsqueda.
	 */
	public SearchModelMapDto(Map<String, Object> filterParams, Map<String, Object> searchParams){
		this.filterParams = filterParams;
		this.searchParams = searchParams;
	}	
	
	/**
	 * Getter de filterParams;
	 * 
	 * @return filterParams.
	 */
	public Map<String, Object> getFilterParams() {
		return filterParams;
	}

	/**
	 * Setter de filterParams.
	 * 
	 * @param filterParams
	 *            Parámetros de filtrado.
	 */
	public void setFilterParams(Map<String, Object> filterParams) {
		this.filterParams = filterParams;
	}

	/**
	 * Getter de searchParams;
	 * 
	 * @return searchParams.
	 */
	public Map<String, Object> getSearchParams() {
		return this.searchParams;
	}

	/**
	 * Setter de searchParams.
	 * 
	 * @param searchParams
	 *            Parámetros de búsqueda.
	 */
	public void setSearchParams(Map<String, Object> searchParams) {
		this.searchParams = searchParams;
	}
	
	/**
	 * Devuelve un bean del tipo indicado como parámetro, con los criterios de
	 * filtrado.
	 * 
	 * @param clazz
	 *            Tipo de bean.
	 * @return Parámetros de filtrado.
	 */
	public T getFilterParams(Class<T> clazz) {
		return this.fromMapToBean(clazz, this.filterParams);
	}
	
	/**
	 * Devuelve un bean del tipo indicado como parámetro, con los criterios de
	 * búsqueda.
	 * 
	 * @param clazz
	 *            Tipo de bean.
	 * @return Parámetros de búsqueda.
	 */
	public U getSearchParams(Class<U> clazz) {
		return this.fromMapToBean(clazz, this.searchParams);
	}
	
	/**
	 * Devuelve un bean del tipo Pagination, con los parámetros de paginación-
	 * 
	 * @return Parámetros de paginación.
	 */
	public Pagination<T> getPagination() {
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
