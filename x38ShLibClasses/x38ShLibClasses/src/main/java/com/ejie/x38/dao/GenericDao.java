/*
* Copyright 2011 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * 
 * @author UDA
 *
 * @param <T> Objeto Generico.
 * @param <PK> Objeto Generico.
 * 
 */
public interface GenericDao<T , PK extends Serializable> {
	
	void setEntityManager(EntityManager entityManager);
	
	EntityManager getEntityManager();
	
	/**
	 * Añadir un nuevo registro 
	 * @param entity instancia de clase entidad.
	 * @return el entity
	 */
	T add (T entity);
	
	/**
	 * Encontrar una entidad por clave primaria.
	 * @param id clave primaria 
	 * @return la  entidad o null en caso de no existir
	 */
	T find(PK id);
	
	/**
	 * Borrar un registro.
	 * @param id clave primaria  
	 */
	void remove(PK id);
	
	/**
	 * modificar un registro
	 * @param entity
	 * @return la entidad modificada
	 */
	T update(T entity);
	
//	/**
//	 * devolver el la lista completa de entidades
//	 * @return lista de entidades
//	 */
//	List<T> findAll();
}