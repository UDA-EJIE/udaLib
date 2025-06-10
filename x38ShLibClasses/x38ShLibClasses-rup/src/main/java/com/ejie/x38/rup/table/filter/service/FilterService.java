package com.ejie.x38.rup.table.filter.service;

import java.util.List;

import com.ejie.x38.rup.table.filter.model.Filter;

public interface FilterService {

	Filter insert(Filter filter);

	Filter update(Filter filter);

	Filter delete(Filter filter);

	Filter getBySelectorAndName(String selector, String name, String user);

	Filter getById(String id);

	Filter getDefault(String selector, String user);

	List<Filter> getAllFilters(String selector, String user);
}
