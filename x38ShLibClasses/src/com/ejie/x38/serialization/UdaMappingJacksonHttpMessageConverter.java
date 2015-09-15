package com.ejie.x38.serialization;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import com.ejie.x38.util.StackTraceManager;

/**
 * 
 * @author UDA
 *
 * Decide si la serializacion sucede por el cauce normal o si sucede a traves del serializador de UDA.
 * Todo depende de si el Filtro de UDA ha insertado cierta informacion en el ThreadLocal o no.
 */
public class UdaMappingJacksonHttpMessageConverter extends
		MappingJacksonHttpMessageConverter {

	protected final Logger logger = Logger
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
				logger.log(Level.INFO,
						"UDA's Serialization Mechanism is being triggered.");
				jacksonJsonObjectMapper.writeValue(jsonGenerator, o);
			} else {
				logger.log(Level.INFO,
						"Spring's Default Object Mapper is being triggered.");
				super.writeInternal(o, outputMessage);
			}
		} catch (Exception ex) {
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(ex));
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