package com.ejie.x38.rup.table.filter.dao;

import java.util.List;

import com.ejie.x38.rup.table.filter.model.Filter;

public interface FilterDao {

	Filter insert(Filter filter);

	Filter update(Filter filter);

	Filter delete(Filter filter);

	// boolean isDefaultAsigned(String selector) ;

	// public boolean isNameRepeated(String selector, String text);

	Filter getById(String id);

	Filter getBySelectorAndName(String selector, String text, String user);

	void setDefaultAsigned(String selector, String text, boolean active, String user);

	Filter getDefaultAsigned(String selector, String user);

	List<Filter> getAll(String selector, String user);
}
