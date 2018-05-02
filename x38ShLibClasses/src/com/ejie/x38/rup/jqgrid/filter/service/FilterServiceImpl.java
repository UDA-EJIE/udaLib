package com.ejie.x38.rup.jqgrid.filter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ejie.x38.rup.table.filter.dao.FilterDao;
import com.ejie.x38.rup.table.filter.model.Filter;


@Service(value = "filterService")
public class FilterServiceImpl implements FilterService{

	
	

	private FilterDao filterDao;

	public FilterDao getFilterDao() {
		return filterDao;
	}

	public void setFilterDao(FilterDao filterDao) {
		this.filterDao = filterDao;
	}

	@Override
	public Filter insert(Filter filtro) {
		
		//comprobar nombreRepetido
		if (!repeatedName(filtro.getFilterSelector(), filtro.getFilterName(), filtro.getFilterUser())){
			//comprobar predefinido
			if (filtro.isFilterDefault()){
				//quitar el filtro predefinido anterior
				removeDeafultPreviousFilter(filtro.getFilterSelector(), filtro.getFilterUser());
			}
			
			filterDao.insert(filtro);
			filtro.setFilterFeedback("ok");
		}else{
			//nombre repetido
			
			//comprobar predefinido
			if (filtro.isFilterDefault()){
				//quitar el filtro predefinido anterior
				removeDeafultPreviousFilter(filtro.getFilterSelector(), filtro.getFilterUser());
			}
			//Updateo los valores
			//filtro.setFilterFeedback("Error, nombre repetido");
			filterDao.update(filtro);
			
		}
		return filtro;
			
	}

	@Override
	public Filter update(Filter filtro) {
		//comprobar predefinido
		if (filtro.isFilterDefault()){
			//quitar el filtro predefinido anterior
			removeDeafultPreviousFilter(filtro.getFilterSelector(), filtro.getFilterUser());
		}
		filterDao.update(filtro);
		return filtro;
	}

	@Override
	public Filter delete(Filter filtro) {
		//comprobar existencia
		if( checkFilter(filtro)){
		
			filterDao.delete(filtro);
		}else{
			//no existe el elemento a borrar
			filtro.setFilterFeedback("no_records");
		}
		
		return filtro;
	}

	@Override
	public Filter getBySelectorAndName(String selector, String name, String user) {
		return filterDao.getBySelectorAndName(selector, name,user);
	}
	@Override
	public Filter getById(String filterId) {
		return filterDao.getById(filterId);
	}

	@Override
	public List <Filter> getAllFilters(String selector,String user) {
		
		return filterDao.getAll(selector,user);
	}
	
	
	private void removeDeafultPreviousFilter(String selector, String user){
		Filter filtro= new Filter();
		
		filtro=filterDao.getDefaultAsigned(selector, user);
	
		//borro el boolean de predefinido si existe
		//if(filtro.size()>0)
		if(filtro!=null)

			filterDao.setDefaultAsigned(filtro.getFilterSelector(),filtro.getFilterName(), false, user);
		
	}
	
	private boolean repeatedName(String selector,String name, String user){
		boolean respuesta=false;
		
		Filter filtro= filterDao.getBySelectorAndName(selector, name,user);
		
		//if(filtro.size()>0)
		if(filtro!=null)
			respuesta=true;
				
			return respuesta;
	}

	@Override
	public Filter getDefault(String selector, String user) {
		
		Filter filtro= filterDao.getDefaultAsigned(selector, user);
		return filtro;
	}
	
	private boolean checkFilter(Filter filtro){
		boolean existe=false;
		
		Filter filter= filterDao.getBySelectorAndName(filtro.getFilterSelector(), filtro.getFilterName(), filtro.getFilterUser());
		if (filter!=null){
			existe=true;
		}
		return existe;
	}
	
}
