package com.ejie.x38.hdiv.error;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.ejie.hdiv.config.HDIVConfig;
import com.ejie.hdiv.context.RequestContextHolder;
import com.ejie.hdiv.filter.ValidationErrorException;
import com.ejie.hdiv.filter.ValidatorError;
import com.ejie.hdiv.filter.ValidatorErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class EjieValidationErrorHander implements ValidatorErrorHandler {

	private static final Logger LOG = LoggerFactory.getLogger(EjieValidationErrorHander.class);

	private int responseErrorCode = HttpServletResponse.SC_FORBIDDEN;

	private final int responseMethodNotAllowedCode = HttpServletResponse.SC_METHOD_NOT_ALLOWED;

	private String responseErrorMessage = "Unauthorized access.";

	public static final String HTTP_METHOD_NOT_ALLOWED = "Http Method not allowed";

	public static final String NO_VALIDATOR_FOUND = "No suitable validator found";

	private final ObjectMapper objectMapper;
	
	private final HDIVConfig config;
	
	private boolean isJsonRequest(String contentType) {
		MediaType requestContentType = contentType == null ? null : MediaType.valueOf(contentType);
		return MediaType.APPLICATION_JSON.isCompatibleWith(requestContentType);
	}

	@Autowired
	public EjieValidationErrorHander(final HDIVConfig config) {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new ValidatorErrorModule());
		this.config = config;
	}

	public void setResponseErrorCode(final int responseErrorCode) {
		this.responseErrorCode = responseErrorCode;
	}

	public void setResponseErrorMessage(final String responseErrorMessage) {
		this.responseErrorMessage = responseErrorMessage;
	}

	@Override
	public void handleValidatorError(final RequestContextHolder ctx, final List<ValidatorError> errors) {
		try {
			if (LOG.isInfoEnabled()) {
				LOG.info("Sending error response due to {} error, JSESSIONID={}", errors, ctx.getHeader("cookie"));
				if (errors != null) {
					for (ValidatorError validatorError : errors) {
						if (validatorError.getStackTrace() != null) {
							for (int i = 0; i < validatorError.getStackTrace().length; i++) {
								LOG.debug(String.valueOf(validatorError.getStackTrace()[i]));
							}
						}
					}
				}
			}
			
			ErrorResponse errorResponse = new ErrorResponse(responseErrorMessage, errors);
			ctx.getRequest().setAttribute("VALIDATION_ERROR", errorResponse);
			checkCustomErrorPage(ctx);

			ValidatorError methodNotAllowedError = null;
			if (errors != null) {
				for (ValidatorError error : errors) {
					if (error.getType().equals(HTTP_METHOD_NOT_ALLOWED)) {
						methodNotAllowedError = error;
					}
				}
			}

			ctx.getResponse().setStatus(methodNotAllowedError != null ? responseMethodNotAllowedCode : responseErrorCode);
			ctx.getResponse().setContentType(MediaType.APPLICATION_JSON.toString());
			ctx.getResponse().setCharacterEncoding("UTF-8");

			ErrorResponse error = new ErrorResponse(responseErrorMessage, errors);
			ctx.getResponse().getWriter().write(objectMapper.writeValueAsString(error));

		}
		catch (IOException e) {
			throw new IllegalStateException("Fail sending error code", e);
		}
	}

	@SuppressWarnings("deprecation")
	private void checkCustomErrorPage(final RequestContextHolder ctx) {
		if (config.getErrorPage() != null && !isJsonRequest(ctx.getContentType())) {
			try {
				ctx.getRequest().getRequestDispatcher(config.getErrorPage()).forward(ctx.getRequest(), ctx.getResponse());
			}
			catch (Exception e) {
				LOG.error("Exception redirection to error page", e);
			}
		}
	}

	@Override
	public void handleValidatorException(final RequestContextHolder ctx, final Throwable exception) {

		if (exception instanceof ValidationErrorException) {
			handleValidatorError(ctx, ((ValidationErrorException) exception).getResult().getErrors());
		}
		else {
			LOG.error("Error al validar la petición {}", ctx.getRequestURI());
		}
	}

	@SuppressWarnings("serial")
	class ValidatorErrorModule extends SimpleModule {
		@Override
		public void setupModule(final SetupContext context) {
			context.setMixInAnnotations(ValidatorError.class, ValidatorErrorMixIn.class);
		}
	}

	abstract class ValidatorErrorMixIn {
		@JsonIgnore
		abstract public String getLocalIp();

		@JsonIgnore
		abstract public String getRemoteIp();

		@JsonIgnore
		abstract public String getUserName();

		@JsonIgnore
		abstract public Throwable getException();

		@JsonIgnore
		abstract public StackTraceElement[] getStackTrace();

		@JsonIgnore
		abstract public String getOriginalParameterValue();

		@JsonIgnore
		abstract public String getValidationRuleName();

		@JsonIgnore
		abstract public String getTarget();
	}

}
