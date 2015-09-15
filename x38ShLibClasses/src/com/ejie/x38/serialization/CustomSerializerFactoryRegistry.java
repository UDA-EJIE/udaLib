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
package com.ejie.x38.serialization;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author UDA
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CustomSerializerFactoryRegistry extends CustomSerializerFactory implements InitializingBean {

	protected final Logger logger =  LoggerFactory.getLogger(CustomSerializerFactoryRegistry.class);

	private Map<Class, JsonSerializer> serializers = new HashMap<Class, JsonSerializer>();
		
	@Override
	public void afterPropertiesSet() throws Exception {
		for (Map.Entry<Class, JsonSerializer> e : serializers.entrySet()) {
			addGenericMapping(e.getKey(), e.getValue());
		}
		logger.info( "Registered all serializers: " + serializers);
	}

	public void setSerializers(Map<Class, JsonSerializer> serializers) {
		this.serializers = serializers;
	}
}