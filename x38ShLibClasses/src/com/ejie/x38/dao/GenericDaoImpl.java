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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.persistence.EntityManager;

import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author UDA
 *
 * @param <T> Objeto Generico.
 * @param <PK> Objeto Generico.
 * 
 */
@Transactional
public class GenericDaoImpl<T, PK extends Serializable> implements GenericDao<T, PK> {

	private EntityManager em;
	
	public void setEntityManager(EntityManager entityManager){
		this.em = entityManager;
	}
	
	public EntityManager getEntityManager(){
		return em;
	}
	
	private Class<T> classT; // cumple las veces de T.class que no es valido demomento en java.

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GenericDaoImpl() {

		// Bueno pues como no se puede hacer T.class hay que hacer esta
		// movida mediante introspección.
		// this.classT = (Class<T>) ((ParameterizedType) getClass()
		// .getGenericSuperclass()).getActualTypeArguments()[0];
		// para obtener la clase T. es decir el primer parámetro generico
		// mediante
		// introspección.
		// evitar un posible ClassCastException Por si acaso a su vez el tipo se
		// trata de un generico
		Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if (type instanceof ParameterizedType)
			this.classT = (Class) ((ParameterizedType) type).getRawType();
		else
			this.classT = (Class) type;
	}

	@Override
	public T add(T entity) {

		this.em.persist(entity);
		return entity;
	}

	@Transactional(readOnly = true)
	@Override
	public T find(PK id) {

		return (T) em.find(classT, id);
	}

	@Override
	public void remove(PK id) {

		T entity = (T) em.find(classT, id);
		em.remove(entity);

	}

	@Override
	public T update(T entity) {
		return em.merge(entity);

	}

//	@Transactional(readOnly = true)
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<T> findAll() {
//		return em.createQuery("Select t from " + this.classT.getSimpleName() + " t").getResultList();
//	}
}