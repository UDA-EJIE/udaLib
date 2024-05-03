package com.ejie.x38.rup.table.filter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ejie.x38.rup.table.filter.dao.FilterDao;
import com.ejie.x38.rup.table.filter.model.Filter;

@Service(value = "filterService")
public class FilterServiceImpl implements FilterService {

	private FilterDao filterDao;

	public FilterDao getFilterDao() {
		return filterDao;
	}

	public void setFilterDao(FilterDao filterDao) {
		this.filterDao = filterDao;
	}

	@Override
	public Filter insert(Filter filter) {
		// comprobar nombreRepetido
		if (!repeatedName(filter.getSelector(), filter.getText(), filter.getUser())) {
			// comprobar predefinido
			if (filter.isActive()) {
				// quitar el filtro predefinido anterior
				removeDeafultPreviousFilter(filter.getSelector(), filter.getUser());
			}

			filterDao.insert(filter);
			filter.setFeedback("ok");
		} else {
			// nombre repetido

			// comprobar predefinido
			if (filter.isActive()) {
				// quitar el filtro predefinido anterior
				removeDeafultPreviousFilter(filter.getSelector(), filter.getUser());
			}
			// Updateo los valores
			// filter.setFeedback("Error, nombre repetido");
			filterDao.update(filter);

		}
		return filter;

	}

	@Override
	public Filter update(Filter filter) {
		// comprobar predefinido
		if (filter.isActive()) {
			// quitar el filtro predefinido anterior
			removeDeafultPreviousFilter(filter.getSelector(), filter.getUser());
		}
		filterDao.update(filter);
		return filter;
	}

	@Override
	public Filter delete(Filter filter) {
		// comprobar existencia
		if (checkFilter(filter)) {

			filterDao.delete(filter);
		} else {
			// no existe el elemento a borrar
			filter.setFeedback("no_records");
		}

		return filter;
	}

	@Override
	public Filter getBySelectorAndName(String selector, String text, String user) {
		return filterDao.getBySelectorAndName(selector, text, user);
	}

	@Override
	public Filter getById(String id) {
		return filterDao.getById(id);
	}

	@Override
	public List<Filter> getAllFilters(String selector, String user) {
		return filterDao.getAll(selector, user);
	}

	private void removeDeafultPreviousFilter(String selector, String user) {
		Filter filter = new Filter();

		filter = filterDao.getDefaultAsigned(selector, user);

		// borro el boolean de predefinido si existe
		// if(filter.size()>0)
		if (filter != null) {
			filterDao.setDefaultAsigned(filter.getSelector(), filter.getText(), false, user);
		}
	}

	private boolean repeatedName(String selector, String name, String user) {
		boolean respuesta = false;

		Filter filter = filterDao.getBySelectorAndName(selector, name, user);

		// if(filter.size()>0)
		if (filter != null) {
			respuesta = true;
		}

		return respuesta;
	}

	@Override
	public Filter getDefault(String selector, String user) {
		return filterDao.getDefaultAsigned(selector, user);
	}

	private boolean checkFilter(Filter filter) {
		boolean existe = false;

		Filter newFilter = filterDao.getBySelectorAndName(filter.getSelector(), filter.getText(), filter.getUser());
		if (newFilter != null) {
			existe = true;
		}
		return existe;
	}

}
