/*
* Copyright 2023 E.J.I.E., S.A.
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
import java.util.Map;
import java.util.Map.Entry;

import com.ejie.hdiv.services.CustomSecureSerializer;
import com.ejie.hdiv.services.SecureIdentifiable;
import com.fasterxml.jackson.databind.JsonSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Serializador que permite serializar unicamente determinadas propiedades del
 * objeto a procesar. Las propiedades a serializar se especifican enviandose en
 * un mapa de propiedades del thread. Siempre incluye el campo NID para mantener
 * la retrocompatibilidad con Hdiv.
 * 
 * @author UDA
 * 
 */
public class CustomNidSerializer extends CustomSecureSerializer {

	protected final Logger logger = LoggerFactory.getLogger(CustomNidSerializer.class);

	/**
	 * Realiza la serializacion del objeto pasado por parametro. Se escriben
	 * unicamente en el JSON resultante las propiedades del bean indicadas en el
	 * mapa de parametros almacenado en el thread.
	 */
	@Override
	protected void writeBody(final Object obj, Map<String, JsonSerializer<Object>> secureIdSerializer) {

		BeanWrapper beanWrapper = new BeanWrapperImpl(obj);
		for (Entry<?, ?> entry : ThreadSafeCache.getMap().entrySet()) {
			String propertyName = (String) entry.getValue();
			if (beanWrapper.isReadableProperty(propertyName)) {
				try {
					writeField(beanWrapper, (String) entry.getKey(), propertyName, true, secureIdSerializer);
					if (((String) entry.getKey()).matches("value|id")) {
						writeField(beanWrapper, SecureIdentifiable.NID_PROPERTY, propertyName, true,
								secureIdSerializer);
					}
				} catch (IOException e) {
					logger.error("Error serializing object", e);
				}
			}
		}
	}

}