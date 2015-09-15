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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author UDA
 *
 */
public class CustomObjectMapper extends ObjectMapper{

	protected final Logger logger =  LoggerFactory.getLogger(CustomObjectMapper.class);

	public void setCustomSerializerFactory(SerializerFactory factory) {
		setSerializerFactory(factory);
		logger.info("Using [" + factory + "] as the custom Jackson JSON serializer factory.");
	}
}