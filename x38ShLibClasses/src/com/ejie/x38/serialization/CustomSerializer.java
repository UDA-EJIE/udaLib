/*
* Copyright 2012 E.J.I.E., S.A.
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
package com.ejie.x38.serialization;

import java.io.IOException;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Serializador que permite serializar unicamente determinadas propiedades del
 * objeto a procesar. Las propiedades a serializar se especifican enviandose en
 * un mapa de propiedades del thread.
 * 
 * @author UDA
 * 
 */
public class CustomSerializer extends JsonSerializer<Object> {

	protected final Logger logger =  LoggerFactory.getLogger(CustomSerializer.class);

	/**
	 * Realiza la serializacion del objeto pasado por parametro. Se escriben
	 * unicamente en el JSON resultante las propiedades del bean indicadas en el
	 * mapa de parametros almacenado en el thread.
	 * 
	 * @param obj
	 *            Objeto a serializar.
	 * @param jgen
	 *            Clase que define la API publica para escribir contenido JSON.
	 * @param provider
	 *            Proporciona la API para obtener serializadores para serializar
	 *            instancias de tipos especificos.
	 * @throws IOException
	 *             Al producirse un error al escribir contenido JSON.
	 * @throws JsonProcessingException
	 *             Al producirse un error al escribir contenido JSON.
	 */
	@Override
	public void serialize(Object obj, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		logger.debug("CustomSerializer.serialize()");

		// Se crea un BeanWrapper a partir del objeto a serializar
		BeanWrapper beanWrapper = new BeanWrapperImpl(obj);

		// Inicio del objeto JSON
		jgen.writeStartObject();
		
		// Se recorren las propiedades almacenadas en el mapa del thread
		for (Entry<?, ?> entry : ThreadSafeCache.getMap().entrySet()) {
            
			// Obtenemos el nombre de la propiedad
            String propertyName = (String) entry.getValue();
            
            // Comprobamos si la propiedad existe en el bean y es accesible para lectura
            if(beanWrapper.isReadableProperty(propertyName)){
            	
            	// Se obtiene el valor de la propiedad del bean
            	Object propertyValue = beanWrapper.getPropertyValue(propertyName);
            	
            	// Se escribe en el JSON el key 
            	jgen.writeFieldName((String) entry.getKey());
            
            	// Se escribe en el JSON el value 
				if (propertyValue==null){
					jgen.writeString("");
				}else{
					jgen.writeObject(propertyValue);
				}
            }
		}
		
		// Fin del objeto JSON
		jgen.writeEndObject();
	}
}