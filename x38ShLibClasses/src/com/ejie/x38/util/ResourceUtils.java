package com.ejie.x38.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Resource;

public class ResourceUtils {

	public static <T> List<Resource<T>> fromListToResource(List<T> list){
		List<Resource<T>> resources = new ArrayList<Resource<T>>();
		for(T object : list) {
			resources.add(new Resource<T>(object));
		}
		return resources;
	}
	
	public static <T> Resource<T> toResource(T entity){
		if(entity != null) {
			return new Resource<T>(entity);
		}
		return null;
	}
}
