package com.ejie.x38.hdiv.serialization;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;

import com.ejie.hdiv.config.HDIVConfig;
import com.ejie.hdiv.services.AnyEntity;
import com.ejie.hdiv.services.SecureIdentifiable;
import com.ejie.hdiv.services.TrustAssertion;
import com.ejie.x38.hdiv.protection.IdProtectionDataManager;
import com.ejie.x38.hdiv.util.ObfuscatorUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public class EjieNidModule extends SimpleModule {

	protected final Logger logger = LoggerFactory.getLogger(EjieNidModule.class);

	private static final long serialVersionUID = -496403297394833735L;

	private static IdProtectionDataManager idProtectionDataManager;

	private TrustAssertionAnnotationIntrospector trustAssertionAnnotationIntrospector;

	private static HDIVConfig hdivConfig;

	public EjieNidModule(IdProtectionDataManager idProtectionManager, HDIVConfig hdivConfig) {
		super("ejie-nid-module", new Version(1, 0, 0, null, "com.ejie.x38.serialization", "x38"));
		trustAssertionAnnotationIntrospector = new TrustAssertionAnnotationIntrospector();

		if (idProtectionManager != null) {
			if (idProtectionDataManager == null) {
				idProtectionDataManager = idProtectionManager;
			} else {
				logger.debug("Id protection data manager is already defined, using the existing one.");
			}
		}

		if (hdivConfig != null) {
			if (EjieNidModule.hdivConfig == null) {
				EjieNidModule.hdivConfig = hdivConfig;
			} else {
				logger.debug("Hdiv config is already defined, using the existing one.");
			}
		}
	}

	public EjieNidModule() {
		this(null, null);
	}

	public static void setIdProtectionDataManager(IdProtectionDataManager idProtectionManager) {
		idProtectionDataManager = idProtectionManager;
	}

	public static void setHdivConfig(HDIVConfig hdivConfig) {
		EjieNidModule.hdivConfig = hdivConfig;
	}

	@Override
	public void setupModule(final SetupContext context) {
		super.setupModule(context);

		context.insertAnnotationIntrospector(trustAssertionAnnotationIntrospector);
		context.setMixInAnnotations(SecureIdentifiable.class, SecureIdentifiableMixin.class);
		context.addBeanSerializerModifier(new CustomSecureFieldSerializerModifier());
	}

	private static class CustomSecureFieldSerializerModifier extends BeanSerializerModifier {

		@Override
		public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc,
				JsonSerializer<?> serializer) {
			if (Resource.class.isAssignableFrom(beanDesc.getBeanClass())) {
				return new ResourceSerializer<Object>();
			}
			return super.modifySerializer(config, beanDesc, serializer);
		}
	}

	public static class ResourceSerializer<T> extends JsonSerializer<Resource<T>> {

		@Override
		public void serialize(final Resource<T> value, final JsonGenerator gen, final SerializerProvider provider)
				throws IOException, JsonProcessingException {

			if (value.getContent() != null) {

				JavaType type = provider.constructType(value.getContent().getClass());
				if (type.isMapLikeType()) {
					// need contextualization
					provider.findValueSerializer(value.getContent().getClass(), null).serialize(value.getContent(), gen,
							provider);
				} else {
					provider.findValueSerializer(value.getContent().getClass()).serialize(value.getContent(), gen,
							provider);
				}
			}
		}
	}

	public static class CustomBeanPropertyWriter extends BeanPropertyWriter {

		private static final long serialVersionUID = 7183776743358501641L;

		protected CustomBeanPropertyWriter(BeanPropertyWriter base, String newFieldName) {
			super(base, new SerializedString(newFieldName));
		}
	}

	public static class SecureIdSerializer extends JsonSerializer<Object> implements ContextualSerializer {

		private BeanProperty beanProperty;

		public SecureIdSerializer() {
		}

		private SecureIdSerializer(BeanProperty beanProperty) {
			this.beanProperty = beanProperty;
		}

		@Override
		public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider)
				throws IOException, JsonProcessingException {
			if (provider != null) {
				if (value != null) {
					String sValue = value.toString();

					provider.findValueSerializer(String.class).serialize(sValue, gen, provider);
					gen.writeStringField(SecureIdentifiable.NID_PROPERTY, sValue);
				} else {
					provider.findValueSerializer(beanProperty.getType(), beanProperty).serialize(value, gen, provider);
				}
			}
		}

		@Override
		public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty beanProperty)
				throws JsonMappingException {
			return new SecureIdSerializer(beanProperty);
		}
	}

	public static class SecureIdDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

		private BeanProperty beanProperty;

		public SecureIdDeserializer() {
		}

		private SecureIdDeserializer(BeanProperty beanProperty) {
			this.beanProperty = beanProperty;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object deserialize(JsonParser parser, DeserializationContext context)
				throws IOException, JsonProcessingException {

			Class<?> clazz;
			TrustAssertion trustAssertion = beanProperty.getAnnotation(TrustAssertion.class);
			if (trustAssertion != null && trustAssertion.idFor() != null) {
				clazz = trustAssertion.idFor();
			} else {
				clazz = ((AnnotatedClass) beanProperty.getMember().getTypeContext()).getRawType();
			}

			Class<?> propertyClass = beanProperty.getType().getRawClass();

			if (JsonToken.START_ARRAY == parser.getCurrentToken()) {

				List<Object> baseArray;
				boolean isArray = false;
				if (List.class.isAssignableFrom(propertyClass)) {
					// It is a list
					if (propertyClass.isInterface()) {
						baseArray = new ArrayList<Object>();
					} else {
						try {
							baseArray = (List<Object>) propertyClass.newInstance();
						} catch (Exception e) {
							baseArray = new ArrayList<Object>();
						}
					}
				} else {
					// Asume array. Fill as List and convert to array
					baseArray = new ArrayList<Object>();
					isArray = true;
				}

				while (parser.nextToken() != JsonToken.END_ARRAY) {
					baseArray.add(getDeobfuscatedValue(parser, clazz));
				}
				return isArray ? baseArray.toArray() : baseArray;
			} else {
				return parseFromString(getDeobfuscatedValue(parser, clazz), propertyClass);
			}
		}

		private String getDeobfuscatedValue(JsonParser parser, Class<?> expectedClass) throws IOException {

			String value = parser.getText();
			String nid;
			try {
				Class<?> parseClass = ObfuscatorUtils.getClass(value);

				if (expectedClass != AnyEntity.class && expectedClass != parseClass) {
					throw new RuntimeException("Incorrect identifier");
				}

				nid = ObfuscatorUtils.deobfuscate(value);
				if (!idProtectionDataManager.isAllowedSecureId(parseClass, nid)) {
					throw new RuntimeException("Not allowed identifier");
				}
			} catch (RuntimeException e) {
				nid = value;
			}
			return nid;
		}

		private Object parseFromString(String value, Class<?> clazz) {
			if (clazz.equals(Integer.class)) {
				return Integer.valueOf(value);
			} else if (clazz.equals(BigDecimal.class)) {
				return new BigDecimal(value);
			} else if (clazz.equals(Long.class)) {
				return Long.valueOf(value);
			} else if (clazz.equals(Double.class)) {
				return Double.valueOf(value);
			}
			return value;
		}

		@Override
		public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty beanProperty)
				throws JsonMappingException {
			return new SecureIdDeserializer(beanProperty);
		}
	}

	public class TrustAssertionAnnotationIntrospector extends JacksonAnnotationIntrospector {

		private static final long serialVersionUID = -129712771550801L;

		@Override
		public Object findSerializer(final Annotated am) {
			return isIdentifiable(am) ? SecureIdSerializer.class : super.findSerializer(am);
		}

		@Override
		public Object findDeserializer(final Annotated am) {
			return isIdentifiable(am) ? SecureIdDeserializer.class : super.findDeserializer(am);
		}

		private boolean isIdentifiable(final Annotated am) {
			TrustAssertion trustAssertion = am.getAnnotation(TrustAssertion.class);
			return (trustAssertion != null && trustAssertion.idFor() != null);
		}
	}

	public interface SecureIdentifiableMixin<ID extends Serializable> extends SecureIdentifiable<ID> {

		@JsonSerialize(using = EjieNidModule.SecureIdSerializer.class)
		@JsonDeserialize(using = EjieNidModule.SecureIdDeserializer.class)
		@Override
		ID getId();
	}
}