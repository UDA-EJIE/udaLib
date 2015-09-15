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
import java.nio.charset.Charset;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import com.ejie.x38.util.StackTraceManager;

/**
 * 
 * Decide si la serializacion sucede por el cauce normal o si sucede a traves del serializador de UDA.
 * Todo depende de si el Filtro de UDA ha insertado cierta informacion en el ThreadLocal o no.
 * 
 * @author UDA
 * 
 */
public class UdaMappingJacksonHttpMessageConverter extends
		MappingJacksonHttpMessageConverter {

	protected final Logger logger = LoggerFactory
			.getLogger(UdaMappingJacksonHttpMessageConverter.class);

	private ObjectMapper jacksonJsonObjectMapper;

	@Override
	protected void writeInternal(Object o, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		JsonEncoding encoding = getEncoding(outputMessage.getHeaders()
				.getContentType());
		JsonGenerator jsonGenerator = jacksonJsonObjectMapper.getJsonFactory()
				.createJsonGenerator(outputMessage.getBody(), encoding);
		try {
			if (ThreadSafeCache.getMap().keySet().size() > 0) {
				logger.info("UDA's Serialization Mechanism is being triggered.");
				jacksonJsonObjectMapper.writeValue(jsonGenerator, o);
			} else {
				logger.info("Spring's Default Object Mapper is being triggered.");
				super.writeInternal(o, outputMessage);
			}
		} catch (Exception ex) {
			logger.error(StackTraceManager.getStackTrace(ex));
			throw new HttpMessageNotWritableException("Could not write JSON: "
					+ ex.getMessage(), ex);
		}
	}

	private JsonEncoding getEncoding(MediaType contentType) {
		if (contentType != null && contentType.getCharSet() != null) {
			Charset charset = contentType.getCharSet();
			for (JsonEncoding encoding : JsonEncoding.values()) {
				if (charset.name().equals(encoding.getJavaName())) {
					return encoding;
				}
			}
		}
		return JsonEncoding.UTF8;
	}

	public void setJacksonJsonObjectMapper(ObjectMapper jacksonJsonObjectMapper) {
		this.jacksonJsonObjectMapper = jacksonJsonObjectMapper;
	}
}