package com.ejie.x38.hdiv.config;

import java.io.IOException;
import java.util.List;

import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.SecureIdentifiable;
import org.hdiv.services.TrustAssertion;
import org.springframework.hateoas.Resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

public class SecureModule extends SimpleModule {

	private static final long serialVersionUID = -496403297394833735L;

	public SecureModule() {
		super("secure-module", new Version(1, 0, 0, null, "com.ejie.x38.hdiv.config", "x38"));
	}

	@Override
	public void setupModule(final SetupContext context) {
		super.setupModule(context);
		context.addBeanSerializerModifier(new CustomSecureFieldSerializerModifier());

	}

	private static class CustomSecureFieldSerializerModifier extends BeanSerializerModifier {

		@Override
		 public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
			if (Resource.class.isAssignableFrom(beanDesc.getBeanClass())) {
				return new ResourceSerializer<Object>();
			}else if (SecureIdentifiable.class.isAssignableFrom(beanDesc.getBeanClass())) {
				return new SecureIdentifiableSerializerWrapper(serializer);
			}
			return serializer;
		 }
		
		@Override
		public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
				List<BeanPropertyWriter> beanProperties) {
			checkSecureIdentifiable(beanDesc, beanProperties);
			checkSecureIdContainer(beanDesc, beanProperties);
			return beanProperties;
		}
		
		private void checkSecureIdentifiable(BeanDescription beanDesc,
				List<BeanPropertyWriter> beanProperties) {
			if (SecureIdentifiable.class.isAssignableFrom(beanDesc.getBeanClass())) {
				for (int i = 0; i < beanProperties.size(); i++) {
					BeanPropertyWriter writer = beanProperties.get(i);
					beanProperties.set(i, new NidPropertyWriter(writer, true));
				}
			}
		}
		
		private void checkSecureIdContainer(BeanDescription beanDesc,
				List<BeanPropertyWriter> beanProperties) {
			if (SecureIdContainer.class.isAssignableFrom(beanDesc.getBeanClass())) {
				CustomBeanPropertyWriter newProperty = null; 
				for (int i = 0; i < beanProperties.size(); i++) {
					BeanPropertyWriter writer = beanProperties.get(i);

					TrustAssertion annotation = writer.getAnnotation(TrustAssertion.class);
					if (annotation != null && annotation.idFor() != null) {
						newProperty = new CustomBeanPropertyWriter(writer, SecureIdentifiable.NID_PROPERTY);
					}
				}
				if(newProperty != null) {
					beanProperties.add(newProperty);
				}
			}
		}

		private static class NidPropertyWriter extends BeanPropertyWriter {

			private static final long serialVersionUID = -8681589772780757288L;
			
			boolean isSecureIdentifiable;

			private NidPropertyWriter(BeanPropertyWriter writer, boolean isSecureIdentifiable) {
				super(writer);
				this.isSecureIdentifiable = isSecureIdentifiable;
			}

			@Override
			public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider provider) throws Exception {
				
				if(isSecureIdentifiable) {
					String currentNid = (String) provider.getAttribute("NID_OBJECT");
					if(currentNid != null) {
						jgen.writeStringField(SecureIdentifiable.NID_PROPERTY, currentNid);
						provider.setAttribute("NID_OBJECT", null);
					}

				}
				
				super.serializeAsField(bean, jgen, provider);
			}
		}
	}

	public static class ResourceSerializer<T> extends JsonSerializer<Resource<T>>  {

		@Override
		public void serialize(final Resource<T> value, final JsonGenerator gen, final SerializerProvider provider)
				throws IOException, JsonProcessingException {

			if (value.getContent() != null) {
	
				JavaType type = provider.constructType(value.getContent().getClass());
				if (type.isMapLikeType()) {
					// need contextualization
					provider.findValueSerializer(value.getContent().getClass(), null).serialize(value.getContent(), gen, provider);
				}
				else {
					provider.findValueSerializer(value.getContent().getClass()).serialize(value.getContent(), gen, provider);
				}
			}
		}
	}
	
	public static class SecureIdentifiableSerializerWrapper extends JsonSerializer<SecureIdentifiable<?>>  {

		@SuppressWarnings("rawtypes")
		private JsonSerializer originalSerializer;
		
		private SecureIdentifiableSerializerWrapper(JsonSerializer<?> serializer) {
			originalSerializer = serializer;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void serialize(final SecureIdentifiable<?> value, final JsonGenerator gen, final SerializerProvider provider)
				throws IOException, JsonProcessingException {
			String currentNid = (String) provider.getAttribute("NID_OBJECT");
			if(currentNid == null) {
				provider.setAttribute("NID_OBJECT", value.getId().toString());
			}
			
			originalSerializer.serialize(value, gen, provider);
		}
	}
	
	public static class CustomBeanPropertyWriter extends BeanPropertyWriter {
        
		private static final long serialVersionUID = 7183776743358501641L;

		protected CustomBeanPropertyWriter(BeanPropertyWriter base, String newFieldName) {
            super(base, new SerializedString(newFieldName));
        }
    }
}