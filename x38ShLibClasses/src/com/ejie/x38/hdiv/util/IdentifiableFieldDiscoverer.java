package com.ejie.x38.hdiv.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.TrustAssertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejie.x38.hdiv.controller.model.LimitedCache;

public class IdentifiableFieldDiscoverer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IdentifiableFieldDiscoverer.class);

	private static final LimitedCache<Class<?>, Map<Class<?>, Field>> IDENTIFIABLE_FIELDS = new LimitedCache<Class<?>, Map<Class<?>, Field>>();

	public static Map<Class<?>, Field> getClassIdentifiableField(Class<SecureIdContainer> clazz) {
		
		Map<Class<?>, Field> identifiableData = null;
				
		if(SecureIdContainer.class.isAssignableFrom(clazz)) {
			//Need to discover the secure annotated fields to get it value
			//Store the data to minimize the use of reflection
			identifiableData = IDENTIFIABLE_FIELDS.get(clazz);
			
			if(identifiableData == null) {
				identifiableData = new ConcurrentHashMap<Class<?>, Field>();
				for (Field field : clazz.getDeclaredFields()) {
					TrustAssertion trustAssertion = field.getAnnotation(TrustAssertion.class);
					if(trustAssertion != null && trustAssertion.idFor() != null) {
						try {
							field.setAccessible(true);
							identifiableData.put(trustAssertion.idFor(), field);
						}
						catch (Exception e) {
							LOGGER.error("Cannot get the secure annotated values from " + clazz, e);
						}
					}
				}
				IDENTIFIABLE_FIELDS.put(clazz, identifiableData);
			}
		}
		return identifiableData;
		
	}
}
