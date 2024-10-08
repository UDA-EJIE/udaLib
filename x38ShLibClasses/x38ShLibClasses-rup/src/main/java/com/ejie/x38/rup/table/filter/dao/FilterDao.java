package com.ejie.x38.rup.table.filter.dao;


import java.util.List;

import com.ejie.x38.rup.table.filter.model.Filter;


public interface FilterDao {

	
	Filter insert(Filter filtro) ;	
	Filter update(Filter filtro);
	
	Filter delete(Filter filtro) ;
	

	//boolean isDefaultAsigned(String selector)  ;
	
	//public boolean isNameRepeated(String selector, String name);

	Filter getById(String filterId) ;
	
	
	
	
	
	Filter getBySelectorAndName(String selector, String name, String user);
	
	void setDefaultAsigned(String selector, String name, boolean pDefault,
				String user);
	Filter getDefaultAsigned(String selector, String user);
	
	List<Filter> getAll(String selector, String user);
}
