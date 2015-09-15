package com.ejie.x38.serialization;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.springframework.util.StringUtils;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.ejie.x38.util.StackTraceManager;

public class CustomSerializer extends JsonSerializer<Object> {

	protected final Logger logger = Logger.getLogger(CustomSerializer.class);

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
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
		}
	}
}