package com.ejie.x38.hdiv.controller.model;

import java.util.ArrayList;
import java.util.List;

public class UDALinkResources {

	List<Object> entities = new ArrayList<Object>();
	List<ReferencedObject> subEntities = new ArrayList<ReferencedObject>();
	
	public List<Object> getEntities() {
		return entities;
	}
	public List<ReferencedObject> getSubEntities() {
		return subEntities;
	}
	
	public void addAllEntities(List<Object> entities) {
		this.entities.addAll(entities);;
	}
	public void addAllSubEntities(List<ReferencedObject> subEntities) {
		this.subEntities.addAll(subEntities);
	}
	
}
