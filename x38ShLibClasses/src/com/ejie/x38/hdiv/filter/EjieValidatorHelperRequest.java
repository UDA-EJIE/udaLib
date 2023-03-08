package com.ejie.x38.hdiv.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hdiv.context.RequestContextHolder;
import org.hdiv.filter.ValidationContext;
import org.hdiv.filter.ValidatorError;
import org.hdiv.filter.ValidatorHelperRequest;
import org.hdiv.filter.ValidatorHelperResult;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.validator.DefaultEditableDataValidationProvider;
import org.hdiv.validator.EditableDataValidationProvider;
import org.hdiv.validator.EditableDataValidationResult;
import org.hdiv.validator.IValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ejie.x38.hdiv.protection.IdProtectionDataManager;
import com.ejie.x38.hdiv.util.Constants;

public class EjieValidatorHelperRequest extends ValidatorHelperRequest {

	private static Logger LOGGER = LoggerFactory.getLogger(ValidatorHelperRequest.class);
	
	private static final String VALIDATED_PARAMS = "VALIDATED_PARAMS";
	
	private FormRequestBodyValidator formRequestBodyValidator;
	
	private IdProtectionDataManager idProtectionDataManager;
	
	public void setIdProtectionDataManager(final IdProtectionDataManager idProtectionDataManager) {
		this.idProtectionDataManager = idProtectionDataManager;
	}
	
	/**
	 * Validate parameter non present in the state.
	 *
	 * @param request HttpServletRequest to validate
	 * @param stateParams parameter and values from the parameters stored in the state
	 * @param stateParameter IParameter The restored state for this url
	 * @param actionParamValues actio params values
	 * @param unauthorizedEditableParameters Editable parameters with errors
	 * @param hdivParameter Hdiv state parameter name
	 * @param target Part of the url that represents the target action
	 * @param parameter Parameter name to validate
	 * @return Valid if parameter has not errors
	 */
	@Override
	protected ValidatorHelperResult validateExtraParameter(final RequestContextHolder request, final Map<String, String[]> stateParams,
			final IParameter stateParameter, final String[] actionParamValues, final List<ValidatorError> unauthorizedEditableParameters,
			final String hdivParameter, final String target, final String parameter) {
		
		String[] values = request.getParameterValues(parameter);
		
		EditableDataValidationProvider provider =  hdivConfig.getEditableDataValidationProvider();
		EditableDataValidationResult result = provider.validate(Constants.CLIENT_PARAMETERS+target, parameter, values, null);
		
		if (result == EditableDataValidationResult.VALID) {
			return ValidatorHelperResult.VALID;
		}
		else if (!result.isValid()) {
			// Editable validation failed
			StringBuilder unauthorizedValues = new StringBuilder(values[0]);
			for (int i = 1; i < values.length; i++) {
				unauthorizedValues.append("," + values[i]);
			}

			ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_EDITABLE_VALUE.toString(), target, parameter,
					unauthorizedValues.toString(), null, result.getValidationId());
			return new ValidatorHelperResult(error);

		}
		else { // is EditableDataValidationResult.VALIDATION_NOT_REQUIRED

			// If the parameter is not defined in the state, and it is not editable, it is an error.
			// With this verification we guarantee that no extra parameters are added.

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Validation Error Detected: Parameter [" + parameter + "] does not exist in the state for action [" + target + "]");
			}

			ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_PARAMETER_NAME, target, parameter,
					request.getParameter(parameter));
			return new ValidatorHelperResult(error);
		}
	}
	
	@Override
	public void init() {
		super.init();
		formRequestBodyValidator = new FormRequestBodyValidator(hdivConfig, stateUtil);
	}

	@Override
	protected ValidatorHelperResult preValidate(ValidationContext context) {
		
		RequestContextHolder requestContext = context.getRequestContext();
		
		if (isStartPage(requestContext, context.getTarget())) {
			return validateStartPageParameters(requestContext, context.getTarget());
		}
		
		if(isJsonRequest(requestContext.getContentType())) {
			//Check as JSON
			try {
				ValidatorHelperResult formValidationResult = formRequestBodyValidator.validateBody(context);
				if(!formValidationResult.isValid() ) {
					return formValidationResult;
				} else if (formValidationResult instanceof EjieValidatorHelperResult) {
					requestContext.getRequest().setAttribute(VALIDATED_PARAMS, ((EjieValidatorHelperResult) formValidationResult).getValidatedParams());
				}
			}
			catch (Exception e) {
				LOGGER.error("Exception validating body.", e);
				throw new RuntimeException(e);
			}
		}
		
		//If the request does not have HDIV STATE, validate url with UDALink annotation
		if(requestContext.getHdivState() == null) {
			if(checkServerSideLink()) {
				//If it is a serverside Link, do not allow parameters apart from MODIFIED_HDIV_STATE
				if(isSimpleRequest(context) || checkModifyRequest(context)) {
					return  ValidatorHelperResult.VALID;	
				}else {
					return new ValidatorHelperResult(new ValidatorError(HDIVErrorCodes.INVALID_PARAMETER_NAME, context.getTarget()));
				}
			}else {
				return new ValidatorHelperResult(new ValidatorError(HDIVErrorCodes.INVALID_ACTION, context.getTarget()));
			}	
		}
		
		return super.preValidate(context);
	}
	
	private boolean isSimpleRequest(ValidationContext context) {
		RequestContextHolder requestContext = context.getRequestContext();
		Enumeration<String> parameters = requestContext.getParameterNames();
		while (parameters.hasMoreElements()) {
			String paramName = parameters.nextElement();
			if(!isExcludedParam(context.getTarget(), paramName) && ValidatorHelperResult.VALID != validateExtraParameter(requestContext, null, null, null, null, null, context.getTarget(), paramName) ) {
				return false;
			}
		}
		return true;
	}
				
	private boolean checkModifyRequest(ValidationContext context) {
		
		boolean isValidated = false;
		RequestContextHolder requestContext = context.getRequestContext();
		String strState = requestContext.getParameter(requestContext.getHdivModifyParameterName());
		
		if(strState != null) {
			String modifiedFormField = requestContext.getParameter(Constants.MODIFY_HDIV_STATE_FORM_FIELD_NAME);
			IState state = stateUtil.restoreState(requestContext, strState);
			IParameter modifiedStateParentParam = state.getParameter(modifiedFormField);
		
			//The parent value is one of the values given by the server. It is into the state
			//The parameter to be modified have to be part of the state too.
			//Parent and the parameter which wants to be modified must be not editable (select, radio,...)
			if(StringUtils.hasText(modifiedFormField) && modifiedStateParentParam != null && !modifiedStateParentParam.isEditable() ) {
				
				EditableDataValidationProvider provider =  hdivConfig.getEditableDataValidationProvider();
				EditableDataValidationResult result = provider.validate(context.getTarget(), Constants.MODIFY_TARGET_PARAMETER + modifiedFormField, new String[] {"-"}, null);
				
				if (!result.isValid() && Constants.MODIFY_RULE_NAME.equals(result.getValidationId())) {
					
					isValidated = true;
					Enumeration<String> parameters = requestContext.getParameterNames();
					while (parameters.hasMoreElements()) {
						String paramName = parameters.nextElement();
						if(!isExcludedParam(context.getTarget(), paramName) && !requestContext.getHdivModifyParameterName().equals(paramName) && !Constants.MODIFY_HDIV_STATE_FORM_FIELD_NAME.equals(paramName) && ValidatorHelperResult.VALID != validateExtraParameter(requestContext, null, null, null, null, null, context.getTarget(), paramName)) {
							IParameter stateParentParam = state.getParameter(paramName);
							
							if(stateParentParam != null && !stateParentParam.isEditable() 
								&& stateParentParam.existValue(requestContext.getParameter(paramName))
								&& isValidByRule(Constants.MODIFY_RULE_NAME, provider, Constants.MODIFY_TARGET_PARAMETER + paramName, requestContext.getParameterValues(paramName), context)) {
								isValidated = true;
							}else {
								return false;
							}
						}
					}
				}
			}
		}
		return isValidated;
	}
	
	private boolean isValidByRule(String validationId, EditableDataValidationProvider provider, String paramName, String[] paramValues, ValidationContext context) {
		if (provider instanceof DefaultEditableDataValidationProvider) {
			List<IValidation> validations = ((DefaultEditableDataValidationProvider) provider).getValidationRepository().findValidations(context.getTarget(), paramName);
			for (IValidation validation : validations) {
				if (validation.getName().equals(validationId)) {
					return provider.validate(context.getTarget(), paramName, paramValues, null).isValid();
				}
			}
		}
		return false;
	}

	private boolean isExcludedParam(String target, String param) {
		return hdivConfig.isStartParameter( param) || hdivConfig.isParameterWithoutValidation(target, param);
	}
	
	private boolean checkServerSideLink() {
		return idProtectionDataManager.isAllowedAction(((ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()).getRequest());
	}
	
	private boolean isJsonRequest(String contentType) {
		MediaType requestContentType = contentType == null ? null : MediaType.valueOf(contentType);
		return MediaType.APPLICATION_JSON.isCompatibleWith(requestContentType);
	}
	
	/**
	 * Check if all required parameters are received in <code>request</code>.
	 *
	 * @param request HttpServletRequest to validate
	 * @param state IState The restored state for this url
	 * @param target Part of the url that represents the target action
	 * @param stateParams Url params from State
	 * @return valid result if all required parameters are received. False in otherwise.
	 */
	@SuppressWarnings("unchecked")
	protected ValidatorHelperResult allRequiredParametersReceived(final RequestContextHolder request, final IState state,
			final String target, final Map<String, String[]> stateParams) {

		List<String> requiredParameters = state.getRequiredParams(hdivConfig.getEditableFieldsRequiredByDefault());
		Set<String> requiredParams = stateParams.keySet();

		Enumeration<?> requestParameters = request.getParameterNames();

		Set<String> required = new HashSet<String>();
		required.addAll(requiredParameters);
		required.addAll(requiredParams);
		
		List<String> validatedParams = (List<String>) request.getAttribute(VALIDATED_PARAMS);
		if (validatedParams != null) {
			required.removeAll(validatedParams);
		}

		while (requestParameters.hasMoreElements()) {

			String currentParameter = (String) requestParameters.nextElement();

			required.remove(currentParameter);

			// If multiple parameters are received, it is possible to pass this
			// verification without checking all the request parameters.
			if (required.isEmpty()) {
				return ValidatorHelperResult.VALID;
			}
		}

		// Fix for IBM Websphere different behavior with parameters without values.
		// For example, param1=val1&param2
		// This kind of parameters are excluded from request.getParameterNames() API.
		// http://www.ibm.com/support/docview.wss?uid=swg1PM35450
		if (!required.isEmpty()) {
			Iterator<String> it = required.iterator();
			while (it.hasNext()) {
				String req = it.next();
				if (isNoValueParameter(request, req)) {
					it.remove();
				}
			}
		}

		return validateMissingParameters(request, state, target, stateParams, new ArrayList<String>(required));
	}
	
	/**
	 * Check if the given parameter doesn't have values looking in the query string.
	 * 
	 * @param request HttpServletRequest instance
	 * @param parameter Parameter name
	 * @return true if the parameter does't have value
	 */
	private boolean isNoValueParameter(final RequestContextHolder request, final String parameter) {

		if(parameter.startsWith("_")) {
			return true;
		}
		
		String queryString = request.getQueryString();
		if (queryString == null) {
			return false;
		}

		String[] parts = queryString.split("&");
		if (parts.length == 0) {
			return false;
		}

		List<String> partsList = Arrays.asList(parts);
		return partsList.contains(parameter);
	}
	
}
