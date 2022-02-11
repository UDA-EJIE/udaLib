package com.ejie.x38.hdiv.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hdiv.services.LinkProvider;
import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.SecureIdentifiable;
import org.hdiv.services.TrustAssertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;

import com.ejie.x38.hdiv.controller.model.ReferencedObject;
import com.ejie.x38.hdiv.controller.model.SecureClassInfo;
import com.ejie.x38.hdiv.controller.model.UDALinkResources;
import com.ejie.x38.hdiv.controller.utils.DinamicLinkProvider;

public abstract class ResponseLinkProcesor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseLinkProcesor.class);

	private static final int MAX_DEEP = 8;
	
	private static final String[] JRE_PACKAGES = new String[] { "java.", "com.sun.", "sun.", "oracle.", "org.xml.", "com.oracle." };
	
	private static final String ID = "id";
	
	private static final String GET = "get";
	
	private static final String GET_ID = "getId";

	public Object checkResponseToLinks(final Object object, Class<?> controller, LinkProvider<?> linkProvider) throws Throwable {

			UDALinkResources udaLinkResources = new UDALinkResources();
			Object processed = fillResources(object, 0, udaLinkResources, false, null);
			UDASecureResourceProcesor.processLinks(udaLinkResources, controller, (DinamicLinkProvider) linkProvider);
			return processed;
	}

	@SuppressWarnings("unchecked")
	protected Object fillResources( Object result, final int deep, UDALinkResources udaLinkResources, boolean isSubEntity, Integer parentIndex) {

		if (result == null || deep > MAX_DEEP) {
			return result;
		}
		
		if (result instanceof Resource) {
			
			Object content = ((Resource<?>)result).getContent();
			if(content instanceof Resource) {
				content = ((Resource<?>)result).getContent();
				fillResources( ((Resource<?>)result).getContent(), deep, udaLinkResources, false, parentIndex);
			}else {
				int currentIndex = addResource(udaLinkResources, result, false, null, parentIndex);
				if(content != null) {
					checkFields(content, deep + 1, udaLinkResources, currentIndex);	
				}
			}
		}else if (result instanceof SecureIdentifiable<?> ) {
			SecureClassInfo identifiableInfo = new SecureClassInfo(ID, GET_ID, result.getClass());
			
			int currentIndex = addResource(udaLinkResources, result, isSubEntity, Arrays.asList(identifiableInfo), parentIndex);
			checkFields(result, deep + 1, udaLinkResources, currentIndex);
			
		}else if (result instanceof SecureIdContainer) {
			
			List<SecureClassInfo> identificatorInfo = getIdentificatorInfo(result);
			int currentIndex = addResource(udaLinkResources, result, isSubEntity, identificatorInfo, parentIndex);
			checkFields(result, deep + 1, udaLinkResources, currentIndex);
			
		}else if (result instanceof Iterable) {
			List<Object> objects = new ArrayList<Object>();
			for (Object o : (Iterable<?>) result) {
				objects.add(fillResources(o, deep + 1, udaLinkResources, isSubEntity, parentIndex));
			}
		}else if (result instanceof Map) {
			for (Entry<Object, Object> entry : ((Map<Object, Object>) result).entrySet()) {
				((Map<Object, Object>) result).put(entry.getKey(), fillResources(entry.getValue(), deep + 1, udaLinkResources, isSubEntity, parentIndex));
			}
		}
		else if (!isJRECLass(result.getClass().getName())) {
			
			//TODO: This case should be deleted due to an unneeded action
			//Test it just in case
			onOtherType(result, deep, udaLinkResources, isSubEntity, parentIndex);
		}
		
		return result;
	}
	
	private List<SecureClassInfo> getIdentificatorInfo(Object object) {
		
		List<SecureClassInfo> secureInfoList = new ArrayList<SecureClassInfo>();
		
		for(Field field  : object.getClass().getDeclaredFields()) {
			try {
				TrustAssertion trustAssertion = field.getAnnotation(TrustAssertion.class);
			    if (trustAssertion != null && trustAssertion.idFor() != null) {
			    	String propertyName = field.getName();
			    	String methodName = GET+ propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			    	Object value = object.getClass().getDeclaredMethod(GET+ propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
			    	//Proxy only valued objects
			    	if(value != null) {
			    		secureInfoList.add(new SecureClassInfo(field.getName(), methodName,  trustAssertion.idFor()));
			    	}
			    }
			}catch(Exception e) {
		    	LOGGER.debug("Field {} of {} not processed", field.getName(), object );
		    }
		}
		
		return secureInfoList;
		
	}
	
	protected void onOtherType(Object result, final int deep, UDALinkResources udaLinkResources, boolean isSubEntity, Integer parentIndex) {

		try {
			Method[] methods = result.getClass().getDeclaredMethods();
			for (Method method : methods) {
				try {
					if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0
							&& method.getReturnType() != null) {
						fillResources(method.invoke(result), deep + 1, udaLinkResources, isSubEntity, parentIndex);
					}
				}
				catch (Exception e) {
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Error getting methods of class:" + result.getClass().getName(), e);
		}
	
	}
	
	protected int addResource(UDALinkResources udaLinkResources, Object resource, boolean isSubEntity, List<SecureClassInfo> secureClassInfo, Integer parentIndex) {
		if(isSubEntity) {
			udaLinkResources.getSubEntities().add(new ReferencedObject(String.valueOf(parentIndex), resource, secureClassInfo));
			return parentIndex;
		}else {
			udaLinkResources.getEntities().add(resource);
			return udaLinkResources.getEntities().size() - 1;
		}
		
	}
	
	private void checkFields(Object object, final int deep, UDALinkResources udaLinkResources, int parentIndex) {
		
		for (Field field : getObjectFields(object)) {
			try {
				if( !Modifier.isStatic(field.getModifiers()) && (Resource.class.isAssignableFrom(field.getDeclaringClass()) || SecureIdentifiable.class.isAssignableFrom(field.getDeclaringClass()) || SecureIdContainer.class.isAssignableFrom(field.getDeclaringClass()))) {
					field.setAccessible(true);
					fillResources(field.get(object), deep, udaLinkResources, true, parentIndex);
				}
			}
			catch (Exception e) {
				LOGGER.error("Error getting field " + field.getName() + " of class:" + object.getClass().getName(), e);
			}
		}	
	}
	
	private Field[] getObjectFields(Object object) {
		Field[] fields;
		Class<?> parentClass = object.getClass().getSuperclass();
		if(parentClass != null) {
			Map<String, Field> fieldsMap = new HashMap<String, Field>();
			for(Field field : parentClass.getDeclaredFields()) {
				fieldsMap.put(field.getName(), field);
			}
			for(Field field : object.getClass().getDeclaredFields()) {
				fieldsMap.put(field.getName(), field);
			}
			fields = new Field[fieldsMap.size()];
			fieldsMap.values().toArray(fields);
			
		}else {
			fields = object.getClass().getDeclaredFields();
		}
		
		return fields;
	}
	
	public boolean isJRECLass(final String className) {
		for (String jrePackage : JRE_PACKAGES) {
			if (className.startsWith(jrePackage)) {
				return true;
			}
		}
		return false;
	}
	
//	protected abstract Object updateOnSecureIdentifiableFound(Object object, SecureClassInfo identifiableInfo);
//	
//	protected abstract Object updateOnSecureIdContainerFound(Object object, List<SecureClassInfo> identifiableInfo);

}
