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

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Factoría de jackson utilizada para configurar las clases que van a utilizar
 * el CustomSerializer.
 * 
 * @author UDA
 * 
 */
@Deprecated
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CustomSerializerFactoryRegistry extends CustomSerializerFactory implements InitializingBean {

	/**
	 * Logger utilizado para traceo.
	 */
	protected final Logger logger =  LoggerFactory.getLogger(CustomSerializerFactoryRegistry.class);

	/**
	 * Serializadores que van a utiilizar el CustomSerializer
	 */
	private Map<Class, JsonSerializer> serializers = new HashMap<Class, JsonSerializer>();
		
	/**
	 * Realiza el regitro de las clases que van a utilizar el CustomSerializer.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		for (Map.Entry<Class, JsonSerializer> e : serializers.entrySet()) {
			addGenericMapping(e.getKey(), e.getValue());
		}
		logger.info( "Registered all serializers: " + serializers);
	}

	/**
	 * Setter de la propiedad serializers.
	 * 
	 * @param serializers
	 *            Serializadores que van a utiilizar el CustomSerializer.
	 */
	public void setSerializers(Map<Class, JsonSerializer> serializers) {
		this.serializers = serializers;
	}
}