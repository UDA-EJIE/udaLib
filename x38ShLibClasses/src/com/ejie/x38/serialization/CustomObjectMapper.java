package com.ejie.x38.serialization;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerFactory;

public class CustomObjectMapper extends ObjectMapper{

	protected final Logger logger = Logger.getLogger(CustomObjectMapper.class);

	public void setCustomSerializerFactory(SerializerFactory factory) {
		setSerializerFactory(factory);
		logger.log(Level.DEBUG, "Using [" + factory + "] as the custom Jackson JSON serializer factory.");
	}
}