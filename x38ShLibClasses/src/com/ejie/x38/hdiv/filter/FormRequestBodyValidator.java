package com.ejie.x38.hdiv.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.filter.ValidationContext;
import org.hdiv.filter.ValidatorError;
import org.hdiv.filter.ValidatorHelperResult;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.StateUtil;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.validator.EditableDataValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ejie.x38.hdiv.util.Constants;
import com.ejie.x38.hdiv.util.ObfuscatorUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

public class FormRequestBodyValidator {

	private static final String HDIV_FORM_BASEPATH = "hdiv.form.basepath";

	private static final Logger LOGGER = LoggerFactory.getLogger(FormRequestBodyValidator.class);

	private static ObjectMapper objectMapper;
	
	private HDIVConfig config;
	
	private StateUtil stateUtil;
	
	private List<String> validatedParams = new ArrayList<String>();
	
	public FormRequestBodyValidator(HDIVConfig hdivConfig, StateUtil stateUtil) {
		config = hdivConfig; 
		this.stateUtil = stateUtil;
	}

	public ValidatorHelperResult validateBody(final ValidationContext context) throws JsonProcessingException, IOException {

		List<ValidatorError> errors = new ArrayList<ValidatorError>();

		Map<String, Map<String, List<String>>> paramData = new HashMap<String, Map<String, List<String>>>();
		paramData.put("form", new HashMap<String, List<String>>());
		paramData.put("extra", new HashMap<String, List<String>>());
		paramData.put("entity", new HashMap<String, List<String>>());
		
		JsonNode json = getRequestBody(context);

		if (json != null) {
			paramData = getParametersFromJson(paramData, json, "", false);
			
			final Map<String, List<String>> formParameters = paramData.get("form");
			final Map<String, List<String>> extraParameters = paramData.get("extra");
		
			if (!formParameters.isEmpty() || !extraParameters.isEmpty()) {
				checkFormParameters(formParameters, context, errors);
				checkExtraParameters(extraParameters, context, errors);
			}
			else {
				// check for an entity body
				ValidatorError error = new ValidatorError(HDIVErrorCodes.HDIV_PARAMETER_DOES_NOT_EXIST, context.getTarget());
				return new ValidatorHelperResult(error);
			}
		}
		
		return errors.isEmpty() ? EjieValidatorHelperResult.VALID.markAsValidated(validatedParams) : new ValidatorHelperResult(errors);
	}
	
	public JsonNode getRequestBody(final ValidationContext context) throws JsonProcessingException, IOException {
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		if ( request.getContentLength() > 0) {
			InputStream is = context.getRequestContext().getInputStream();
			return getObjectMapper().readTree(is);
		}
		return null;
	}
	
	public synchronized ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return objectMapper;
	}

	private boolean isEntityPart(final IState state, final String parameterName, final ValidationContext context) {
		if (state.getParameter(parameterName) != null) {
			return true;
		}
		else {
			int index = parameterName.lastIndexOf(".");
			if (index > 0) {
				String name = parameterName.substring(0, index);
				return isEntityPart(state, name, context);
			}
			else {
				return false;
			}
		}
	}

	private void checkFormParameters(final Map<String, List<String>> parameters,
			final ValidationContext context, final List<ValidatorError> errors) {
		
		if (!parameters.isEmpty()) {

			if (parameters.get(config.getStateParameterName()) == null) {
				errors.add(new ValidatorError(HDIVErrorCodes.HDIV_PARAMETER_DOES_NOT_EXIST, ""));
			}
			else {

				String formBasePath = parameters.get(HDIV_FORM_BASEPATH).get(0);
				parameters.remove(HDIV_FORM_BASEPATH);

				String strState = parameters.get(config.getStateParameterName()).get(0);
				context.getRequestContext().setHdivState(strState);
				IState state = stateUtil.restoreState(context.getRequestContext(),strState);
				parameters.remove(config.getStateParameterName());
				
				Iterator<Entry<String, List<String>>> parameterIterator = parameters.entrySet().iterator();

				while (parameterIterator.hasNext()) {
					
					Entry<String, List<String>> parameter = parameterIterator.next();
					String parameterName = parameter.getKey();
					String parameterFullName = formBasePath + parameterName;

					LOGGER.debug("Validate {} param ", parameterFullName);
					
					if(!excludedFromValidation(config, context.getTarget(), parameterName)) {					
		
						IParameter param = state.getParameter(parameterName);
						LOGGER.debug("Validate {} param ", parameterFullName);
	
						if (param == null) {
							// Check for a entity type param or check if no validation required
							if (!isEntityPart(state, parameterName, context)) {
								// Add as extra value to be validated as CLIENT PARAMETER
								validateExtraParameter(parameterName, parameterFullName, parameter.getValue(), errors);
								LOGGER.debug("Validate {} param as client parameter ", parameterFullName);
							}
						}
						else if(param.isEditable()) {
							validateEditableParameter(parameterName, parameterFullName, parameter.getValue(), errors);
						}else if (!param.getValues().isEmpty() && parameter.getValue() != null) {
							validateNonEditable(param, parameter, parameterName, parameterFullName, errors);
						}

					}
				}
			}
		}
	}
	
	private void checkExtraParameters(final Map<String, List<String>> parameters,
			final ValidationContext context, final List<ValidatorError> errors) {
		
		if (!parameters.isEmpty()) {
			Iterator<Entry<String, List<String>>> parameterIterator = parameters.entrySet().iterator();

			String target = context.getTarget();
			while (parameterIterator.hasNext()) {
				
				Entry<String, List<String>> parameter = parameterIterator.next();
				String parameterName = parameter.getKey();
				
				if(!excludedFromValidation(config, target, parameterName)) {	
					validateExtraParameter(target, parameterName, parameter.getValue(), errors);
				} else {
					markAsValidated(parameterName);
				}
			}
		}
	}

	private void validateNonEditable(final IParameter stateParam, final Entry<String, List<String>> bodyParameters,
			final String parameterName,
			final String parameterFullName, List<ValidatorError> errors) {

		LOGGER.debug("Validate {} param as non editable ", parameterFullName);
		List<String> posibleValues = stateParam.getValues();
		boolean valid = true;
		for (String value : bodyParameters.getValue()) {
			if (!posibleValues.contains(value)) {
				errors.add(new ValidatorError(HDIVErrorCodes.INVALID_PARAMETER_VALUE, parameterName, value));
				valid = false;
				break;
			}
		}
		if (valid) {
			markAsValidated(parameterName);
		}
	}

	private Map<String, Map<String, List<String>>> getParametersFromJson(final Map<String, Map<String, List<String>>> params,
			final JsonNode json, final String parentPath, final boolean insideHdivForm) {

		boolean isInsideForm = insideHdivForm || json.has(config.getStateParameterName());

		String currentParentPath;
		// If it is the start of the form, restart parentPath
		if (isInsideForm && !insideHdivForm) {
			params.get("form").put(HDIV_FORM_BASEPATH, Arrays.asList(parentPath));
			currentParentPath = "";
		}
		else {
			currentParentPath = parentPath;
		}

		Iterator<Entry<String, JsonNode>> it = json.fields();
		while (it.hasNext()) {

			Entry<String, JsonNode> jsonField = it.next();
			JsonNode innerNode = jsonField.getValue();
			String parentkey = currentParentPath + jsonField.getKey();
			String dataType = isInsideForm ? "form" : "extra";

			if (innerNode.isContainerNode()) {

				if (innerNode.isArray()) {
					Iterator<JsonNode> itt = innerNode.iterator();
					while (itt.hasNext()) {
						JsonNode innerArrayNode = itt.next();
						if (innerArrayNode instanceof TextNode) {
							addValueToDataParams(params, dataType, innerArrayNode.asText(), parentkey);
						}
						else {
							params.putAll(getParametersFromJson(params, innerArrayNode, parentkey + ".", isInsideForm));
						}
					}
				}
				else {
					params.putAll(getParametersFromJson(params, innerNode, parentkey + ".", isInsideForm));
				}
			}
			else if (!"nid".equals(parentkey)) {
				String nodeValue = innerNode.asText();
				if ("".equals(parentPath) && ObfuscatorUtils.isObfuscatedId(nodeValue)) {
					params.get("entity").put(parentkey, Arrays.asList(nodeValue));
				}

				addValueToDataParams(params, dataType, nodeValue, parentkey);
			}
		}
		return params;
	}

	private void addValueToDataParams(final Map<String, Map<String, List<String>>> params, final String dataType, final String nodeValue,
			final String parentkey) {
		List<String> value = params.get(dataType).get(parentkey);
		if (value == null) {
			value = new ArrayList<String>();
		}
		value.add(nodeValue);
		params.get(dataType).put(parentkey, value);
	}

	private boolean excludedFromValidation(final HDIVConfig hdivConfig, final String url, final String paramName) {
		return hdivConfig.isStartParameter(paramName) || hdivConfig.isParameterWithoutValidation(url, paramName);
	}
	
	private void validateExtraParameter(String target, String parameter, List<String> values, List<ValidatorError> errors) {
		
		EditableDataValidationResult result = config.getEditableDataValidationProvider().validate(Constants.CLIENT_PARAMETERS+target, parameter, values.toArray(new String[0]), null);
		if(result != EditableDataValidationResult.VALID) {
			errors.add(new ValidatorError(HDIVErrorCodes.INVALID_PARAMETER_NAME, target, parameter));
		} else {
			markAsValidated(parameter);
		}
	}
	
	private void validateEditableParameter(String target, String parameter, List<String> values, List<ValidatorError> errors) {
		
		EditableDataValidationResult result = config.getEditableDataValidationProvider().validate(target, parameter, values.toArray(new String[0]), null);
		if (!result.isValid()) {
			errors.add(new ValidatorError(HDIVErrorCodes.INVALID_PARAMETER_VALUE, target, parameter));
		} else {
			markAsValidated(parameter);
		}
	}
	
	private void markAsValidated(String paramName) {
		validatedParams.add(paramName);
	}

}
