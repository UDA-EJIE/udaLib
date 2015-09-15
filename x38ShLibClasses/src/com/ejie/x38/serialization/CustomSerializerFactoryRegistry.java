package com.ejie.x38.serialization;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;

import org.springframework.beans.factory.InitializingBean;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CustomSerializerFactoryRegistry extends CustomSerializerFactory implements InitializingBean {

	protected final Logger logger = Logger.getLogger(CustomSerializerFactoryRegistry.class);

	private Map<Class, JsonSerializer> serializers = new HashMap<Class, JsonSerializer>();
		
	@Override
	public void afterPropertiesSet() throws Exception {
		for (Map.Entry<Class, JsonSerializer> e : serializers.entrySet()) {
			addGenericMapping(e.getKey(), e.getValue());
		}
		logger.log(Level.INFO, "Registered all serializers: " + serializers);
	}

	public void setSerializers(Map<Class, JsonSerializer> serializers) {
		this.serializers = serializers;
	}
}