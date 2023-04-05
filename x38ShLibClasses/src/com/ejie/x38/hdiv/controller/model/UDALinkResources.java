package com.ejie.x38.hdiv.controller.model;

import java.util.ArrayList;
import java.util.List;

public class UDALinkResources {

	List<Object> entities = new ArrayList<Object>();
	List<Object> subEntities = new ArrayList<Object>();
	
	public List<Object> getEntities() {
		return entities;
	}
	public List<Object> getSubEntities() {
		return subEntities;
	}
	
	public void addAllEntities(List<Object> entities) {
		this.entities.addAll(entities);;
	}
	public void addAllSubEntities(List<Object> subEntities) {
		this.subEntities.addAll(subEntities);
	}
	
}
