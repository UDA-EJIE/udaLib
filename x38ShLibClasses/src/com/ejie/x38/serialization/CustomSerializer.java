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

import java.io.IOException;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.ejie.x38.util.StackTraceManager;

/**
 * 
 * @author UDA
 *
 */
public class CustomSerializer extends JsonSerializer<Object> {

	protected final Logger logger =  LoggerFactory.getLogger(CustomSerializer.class);

	@Override
	public void serialize(Object value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		try {
			Class<?> clazz = value.getClass();
			jgen.writeStartObject();
			for (Entry<?, ?> entry : ThreadSafeCache.getMap().entrySet()) {
				try{
					jgen.writeFieldName((String) entry.getKey());
					jgen.writeString(clazz
							.getDeclaredMethod(
									"get"
											+ StringUtils.capitalize((String) entry
													.getValue())).invoke(value)
							.toString());
				}catch(Exception e){
					jgen.writeString("");
				}
			}
			jgen.writeEndObject();
		} catch (Exception e) {
			logger.error(StackTraceManager.getStackTrace(e));
		}
	}
}