/*
* Copyright 2012 E.J.I.E., S.A.
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
package com.ejie.x38.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Path.Node;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.ejie.w43ta.clients.W43taMomoCustomMap;
import com.ejie.w43ta.clients.W43taMomoTraceClient;
import com.ejie.x38.json.JSONObject;
import com.ejie.x38.log.LogbackConfigurer;
import com.ejie.x38.util.Constants;
import com.ejie.x38.util.DateTimeManager;
import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.StaticsContainer;
import com.ejie.x38.util.WebContextParameterManager;

/**
 * Proporciona el API de validación de UDA. 
 * 
 * @author UDA
 *
 */
public class ValidationManager {

	private static final long serialVersionUID = 1L;
	
	private final static Logger logger =  LoggerFactory.getLogger(ValidationManager.class);
	
	@Autowired
	WebContextParameterManager webContextParameterManager;
	
	@Resource
	private ReloadableResourceBundleMessageSource messageSource;

	@Autowired(required=false)
	private Validator validator;
	
	@PostConstruct
	public void init() {
		if (this.validator==null){
			ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory();
			this.validator = validatorFactory.getValidator();
		}
		//Sends traces to w43a
				Properties props = new Properties();
				InputStream in = null;
				try {
					logger.debug("Loading properties from: "+StaticsContainer.webAppName+"/"+StaticsContainer.webAppName+".properties");
					in = LogbackConfigurer.class.getClassLoader().getResourceAsStream(StaticsContainer.webAppName+"/"+StaticsContainer.webAppName+".properties");
					props.load(in);
					
					//Creamos el traceClient
					W43taMomoTraceClient mtc = W43taMomoTraceClient.getInstance(
							props.getProperty(Constants.PROPS_MOMO_SERVICIO),
							Constants.MOMO_APP,
							Constants.MOMO_SEC_TOKEN,
							props.getProperty(Constants.PROPS_MOMO_URI_ENDPOINT),
							Integer.parseInt(props.getProperty(Constants.PROPS_MOMO_PUERTO_ENDPOINT)),
							false);
					
					//Obtenemos la versión desde el manifest
					String implementationVersion = Constants.X38_VERSION;
					//Añaidmos las trazas
					W43taMomoCustomMap customData = mtc.getNewCustomDataMap();
					
					customData.add(Constants.MOMO_LABEL_SERVICIO, Constants.PROPS_MOMO_SERVICIO);
					customData.add(Constants.MOMO_LABEL_COD_APP, StaticsContainer.webAppName);
					
					String now = new java.util.Date().toString();
					String msgTraza = "##|AUDIT ~~ "+Constants.MOMO_APP+" ~~ "+now+" ~~ x38-INIT"+implementationVersion+"|##";
					//Escribimos los datos en PIB
					mtc.info(msgTraza, customData);
				} catch(Exception e){
					logger.error(StackTraceManager.getStackTrace(e));
				}
				finally {
					try {
						if(in != null) {
							in.close();
						}
					} catch (IOException e) {
						logger.error("ERROR al cerrar el inputStream en AuditController:",e);
					}
				}
	}

	/**
	 * Realiza la validación de la instancia indicada como parámetro. Las
	 * validaciones se realizan a partir de las anotaciones realizadas sobre las
	 * propiedades del bean. Los errores se añaden al objeto errors indicado
	 * como parámetro.
	 * 
	 * @param errors
	 *            Parámetro sobre el que se van a añadir los errores de
	 *            validación que se produzcan.
	 * @param obj
	 *            Intancia del objeto que se desea validar.
	 * @param groups
	 *            Grupos de validacion que se van a utilizar para realizar la
	 *            validación. En caso de no especificarse ninguno se tomará del
	 *            grupo Default.class como grupo por defecto.
	 */
	public void validate(Errors errors, Object obj, Class<?>... groups) {
		// En caso de no especificarse grupos de validaciones se toma el grupo Default por defecto.
		if (groups == null || groups.length == 0 || groups[0] == null) {
			groups = new Class<?>[] { Default.class };
		}
		
		// Se realiza la validacion de la instancia
		Set<ConstraintViolation<Object>> violations = validator.validate(obj, groups);
	    
		// A partir de las violaciones obtenidas a partir de la validacion
		// realizada, se añaden en la propiedad Errors
		for (ConstraintViolation<Object> v : violations) {
			Path path = v.getPropertyPath();
			String propertyName = "";
			if (path != null) {
				for (Node n : path) {
					propertyName += n.getName() + ".";
				}
				propertyName = propertyName.substring(0,
						propertyName.length() - 1);
			}
			String constraintName = v.getConstraintDescriptor().getAnnotation()
					.annotationType().getSimpleName();
			if (propertyName == null || "".equals(propertyName) || "null".equals(propertyName)) {
				errors.reject(constraintName, v.getMessage());
			} else {
				errors.rejectValue(propertyName, constraintName, v.getMessage());
			}
		}
	}


	/**
	 * Método que permite realizar la validación de
	 * una propiedad del bean.
	 * 
	 * @param bean
	 *            Nombre del tipo del bean sobre el que se deben de validar los
	 *            datos.
	 * @param property
	 *            Nombre de la propiedad del bean que se debe de validar.
	 * @param value
	 *            Valor de la propiedad que se debe de validar.
	 * @param locale
	 *            Locale actual que define el idioma a utilizar en la
	 *            internacionalización de los mensajes.
	 * @return Texto de error que contiene los mensajes de error resultantes de la validación.
	 */
	public String validateProperty(String bean, String property, String value, Locale locale){
		try{
			
			String capitalicedBean = StringUtils.capitalize(bean);
	
			BeanWrapper beanWrapper = new BeanWrapperImpl(Class.forName(StaticsContainer.modelPackageName+capitalicedBean));
			beanWrapper.setAutoGrowNestedPaths(true);
			beanWrapper.setPropertyValue(property, value);
			
			Set<ConstraintViolation<Object>> constraintViolations = validator.validateProperty(beanWrapper.getWrappedInstance(), property, Default.class);			
			return summary(constraintViolations, bean, locale);
		}catch (Exception e) {
			logger.error(StackTraceManager.getStackTrace(e));
			return "error!";
		}
	}
	
	
	/**
	 * Genera, a partir de unos errores indicados como parámetros, un mapa en el
	 * cual cada elemento está formado del siguiente modo:<br>
	 * <br>
	 * - El key del elemento del mapa está formado por el nombre de la propiedad
	 * sobre la que se ha producido el error de la validación o por el
	 * identificador que se ha asignado a la validación.<br>
	 * <br>
	 * - El objeto asociado al key está formado por una lista de mensajes de
	 * error internacionalizados a partir del key de error.
	 * 
	 * @param errors
	 *            Conjunto de errores que se desean procesar.
	 * @return Mapa resultante que contiene los errores procesados.
	 */
	public Map<String,List<String>> getErrorsAsMap(Errors errors){
		// Se obtiene la locale actual que va a ser utilizada para realizar la
		// internacionalización de los mensajes de error.
		Locale locale = LocaleContextHolder.getLocale();
		// Mapa en el que se van a almacenar los errores 
		Map<String,List<String>> errorsMap = new HashMap<String, List<String>>();
		// Lista de errores que se han producido al realizarse el databinding
		List<? extends ObjectError> fieldErrors = errors.getAllErrors();
		// Se recorre cada error
		for (Iterator<? extends ObjectError> iterator = fieldErrors.iterator(); iterator.hasNext();) {
		
			ObjectError objectError = (ObjectError) iterator.next();
			String errorMessage;
			String key;
			
			// Se comprueba si el error está asociado a la validación de una
			// propiedad de un bean
			if (objectError instanceof FieldError){
				FieldError fieldError = (FieldError)objectError;
				// En caso de que se trate de un FieldError se toma el nombre
				// del campo como el key que se utilizará en el elemento del
				// mapa.
				key = fieldError.getField();
				// Se trata de obtener el mensaje internacionalizado a partir del key del error.
				try{
					// En caso de existir se toma el mensaje internacionalizado como el texto de error a mostrar.
					errorMessage = messageSource.getMessage(fieldError.getDefaultMessage(), null, locale);
				}catch (NoSuchMessageException e) {
					// En caso de producirse un error en la obtención del texto se toma el key como texto a mostrar.
					errorMessage = fieldError.getCode();
				}
			}else{
				// En caso de que se trate de un FieldError se toma el code
				// del error como el key que se utilizará en el elemento del
				// mapa.
				key = objectError.getCode();
				// Se trata de obtener el mensaje internacionalizado a partir del key del error.
				try{
					// En caso de existir se toma el mensaje internacionalizado como el texto de error a mostrar.
					errorMessage = messageSource.getMessage(objectError.getDefaultMessage(), null, locale);
				}catch (NoSuchMessageException e) {
					// En caso de producirse un error en la obtención del texto se toma el key como texto a mostrar.
					errorMessage = objectError.getDefaultMessage();
				}
			}
			
			// Se comprrueba si error se ha insertado y a en el mapa
			if(errorsMap.containsKey(key)){
				// En caso de existir se añade el mensaje de error en la lista de errores.
				errorsMap.get(key).add(errorMessage);
			}else{
				// En caso de no existir se genera un nuevo elemento en el mapa
				// con una lista de errores en la cual se añade el mensaje de
				// error.
				List<String> listaErrores = new ArrayList<String>();
				listaErrores.add(errorMessage);
				errorsMap.put(key,listaErrores);
			}
		}
		// Se devuelve el mapa de errores
		return errorsMap;
	}
	
	/**
	 * Genera, a partir de unos errores indicados como parámetros, una lista
	 * formada por los mensajes de error internacionalizados.
	 * 
	 * @param errors
	 *            Conjunto de errores que se desean procesar.
	 * @return Lista resultanete que contiene los mensajes de error procesados.
	 */
	public List<String> getErrorsAsList(Errors errors){
		// Se obtiene la locale actual que va a ser utilizada para realizar la
		// internacionalización de los mensajes de error.
		Locale locale = LocaleContextHolder.getLocale();
		// Mapa en el que se van a almacenar los errores 
		List<String> list = new ArrayList<String>();
		// Lista de errores que se han producido al realizarse el databinding
		List<FieldError> fieldErrors = errors.getFieldErrors();
		
		// Se recorre cada error
		for (Iterator<FieldError> iterator = fieldErrors.iterator(); iterator.hasNext();) {
		
			FieldError fieldError = (FieldError) iterator.next();
			String errorMessage;
			
			try{
				errorMessage = messageSource.getMessage(fieldError.getCode(), null, locale);
			}catch (NoSuchMessageException e) {
				errorMessage = fieldError.getCode();
			}

			list.add(errorMessage);
			
		}
		
		return list;
	}

	/**
	 * Genera a partir de un mensaje y un estilo, una estructura para devolver
	 * una estructura que pueda ser convertida a formato JSON e interpretada por
	 * el feedback.
	 * 
	 * @param msg
	 *            Mensaje que debe ser visualizado por el feedback.
	 * @param style
	 *            Estilo que se debe mostrar con el feedback.
	 * @return Estrutura que contiene el mensaje y el estilo que se debe de
	 *         visualizar en el feedback.
	 */
	public Map<String,Object> getRupFeedbackMsg(String msg, String style){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("label", msg);
		if (style!=null && !"".equals(style)){
			map.put("style", style);
		}
		return map;
	}
	
	
	/*
	 * Metodos de validaciones
	 */
	public JSONObject getMessageJSON(Object fieldErrors){
		return this.getMessageJSON(fieldErrors, null, null);
	}
	public JSONObject getMessageJSON(Object fieldErrors, Object feedbackMessage){
		return this.getMessageJSON(fieldErrors, feedbackMessage, null);
	}
	public JSONObject getMessageJSON(Object fieldErrors, Object feedbackMessage, String style){
		
		JSONObject message = new JSONObject();
		
		if (feedbackMessage!=null ){
			
			JSONObject feedbackObj = new JSONObject();
			feedbackObj.put("message", feedbackMessage);
			
			if (style!=null && !"".equals(style)){
				feedbackObj.put("style", style);
			}
			message.put("rupFeedback", feedbackObj);
		}
		
		if (fieldErrors!=null){
			message.put("rupErrorFields", fieldErrors);
		}
		
		return message;
	}
	
	/**
	 * Método utilizado por el ValidationFilter para realizar la validación de
	 * la información enviada en la request.
	 * 
	 * @param bean
	 *            Nombre del tipo del bean sobre el que se deben de validar los
	 *            datos.
	 * @param data
	 *            Datos enviados en la petición.
	 * @param locale
	 *            Locale actual que define el idioma a utilizar en la
	 *            internacionalización de los mensajes.
	 * @return Texto de error que contiene los mensajes de error y que van a ser
	 *         enviados por el ValidationFilter.
	 */
	@Deprecated
	public String validateObject(String bean, String data, Locale locale){
		try{
			Class<?> clazz = Class.forName(StaticsContainer.modelPackageName+bean);
			ObjectMapper mapper = new ObjectMapper();
			Object instance = mapper.readValue(data, clazz);					
	
			Set<ConstraintViolation<Object>> constraintViolations = validator.validate(instance);
			return summary(constraintViolations, bean, locale);
		}catch (Exception e) {
			logger.error(StackTraceManager.getStackTrace(e));
			return "error!";
		}
	}
	
	/**
	 * Realiza la validación del objeto indicado y envía en la response los
	 * errores de validación que se han producido.
	 * 
	 * @param <T>
	 *            Tipo del objeto que se va a validar.
	 * @param object
	 *            Objeto sobre el que se va a realizar la validación.
	 * @param response
	 *            HttpServletResponse sobre la que se van a escribir los errores
	 *            de validación.
	 * @return True/false dependiendo del resultado de la validación.
	 */
	@Deprecated
	public <T extends Object> Boolean writeValidationErrors(T object, HttpServletResponse response){
		
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
		
		if (!constraintViolations.isEmpty()){
			String summary = this.summary(constraintViolations, object.getClass().getSimpleName(), LocaleContextHolder.getLocale());
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			response.setContentType("text/javascript;charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Expires", DateTimeManager.getHttpExpiredDate());
			try {
				response.getWriter().write(summary);
				return true;
			} catch (IOException e) {
				logger.error(StackTraceManager.getStackTrace(e));
				return true;
			}
		}
		
		return false;
	}
	
	
	// MÉTODOS PRIVADOS
	
	/**
	 * Genera una cadena de error en formato JSON a partir de los errores de
	 * validación que se han producido.
	 * 
	 * @param <T>
	 *            Tipo del objeto que se va a validar.
	 * @param constraintViolations
	 *            Errores de validación sobre los que se desea generar el
	 *            mensaje.
	 * @param bean
	 *            Nombre del tipo de bean sobre el que se ha realizado la
	 *            validación.
	 * @param locale
	 *            Locale actual que define el idioma a utilizar en la
	 *            internacionalización de los mensajes.
	 * @return Cadena de error resultante.
	 */
	private <T extends Object> String summary (Set<ConstraintViolation<T>> constraintViolations, String bean, Locale locale){
		Iterator<ConstraintViolation<T>> ite = constraintViolations.iterator();
		Map<String,List<Map<String,String>>> errors = new HashMap<String,List<Map<String,String>>>();
		String propertyKey ="";
		List<Map<String,String>> propertyErrors;
		while (ite.hasNext()) {
			ConstraintViolation<T> constraintViolation = ite.next();
			propertyKey = constraintViolation.getPropertyPath()+"";
			if(errors.containsKey(propertyKey)){
				propertyErrors = errors.get(propertyKey);				
			}else{
				propertyErrors = new ArrayList<Map<String,String>>();
			}
			Map<String,String> node = new HashMap<String,String>();
			String interpolatedMessage;
			//Try to get the interpolated Message in this order: 1- War, 2- EAR, 3- Hibernate's Default 
			try{
				interpolatedMessage = messageSource.getMessage(constraintViolation.getMessage(), null, locale);
			}catch(NoSuchMessageException e){
				interpolatedMessage = constraintViolation.getMessage();
			}			
			if(interpolatedMessage!= null && !interpolatedMessage.equals("")){
				node.put(constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(), interpolatedMessage);
			}else{
				node.put(constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(), "message not found");
				logger.error("Validation message for key "+constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()+" not found");
			}
			
			propertyErrors.add(node);
			errors.put(propertyKey, propertyErrors);
		}
		if (!errors.isEmpty()){				
			Map<String, String> title = new HashMap<String, String>();
			title.put("key", bean);
			String header = serialize(title);
			String body = serialize(errors);
			String[] result = {header, body};
			String summary = serialize(result);
			return summary;
		}else{
    		return null;		
		}
	}
	
	/**
	 * Metodo utilizado para realizar la serialización de un objeto en formato JSON.
	 * 
	 * @param obj Objeto a serializar.
	 * @return Serialización del objeto en formato JSON.
	 */
	private String serialize(Object obj) {
		StringWriter sw = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		MappingJsonFactory jsonFactory = new MappingJsonFactory();
		try {
			JsonGenerator jsonGenerator = jsonFactory.createGenerator(sw);
			mapper.writeValue(jsonGenerator, obj);
			sw.close();

			return sw.getBuffer().toString();
		} catch (IOException e) {
			logger.error(StackTraceManager.getStackTrace(e));
			return "error!";
		}
	}
}