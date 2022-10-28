package com.ejie.x38.hdiv.filter;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.hdiv.context.RequestContextHolder;
import org.hdiv.filter.ValidationContext;
import org.hdiv.filter.ValidatorError;
import org.hdiv.filter.ValidatorHelperRequest;
import org.hdiv.filter.ValidatorHelperResult;
import org.hdiv.state.IParameter;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.validator.EditableDataValidationProvider;
import org.hdiv.validator.EditableDataValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ejie.x38.hdiv.protection.IdProtectionDataManager;
import com.ejie.x38.hdiv.util.Constants;

public class EjieValidatorHelperRequest extends ValidatorHelperRequest {

	private static Logger LOGGER = LoggerFactory.getLogger(ValidatorHelperRequest.class);
	
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
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//If the request does not have HDIV STATE, validate url with UDALink annotation
		if(requestContext.getHdivState() == null) {
			if(checkServerSideLink()) {
				//If it is a serverside Link, do not allow parameters apart from MODIFIED_HDIV_STATE
				if(isSimpleRequest(context)) {
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
			if(!paramName.equals(requestContext.getHdivModifyParameterName()) && !isExcludedParam(context.getTarget(), paramName)) {
				return false;
			}
		}
		return true;
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
	
}
