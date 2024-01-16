package com.ejie.x38.hdiv.filter;

import java.util.ArrayList;
import java.util.List;

import com.ejie.hdiv.filter.ValidatorHelperResult;

public class EjieValidatorHelperResult extends ValidatorHelperResult {

	/**
	 * Constant valid result.
	 */
	public static final EjieValidatorHelperResult VALID = new EjieValidatorHelperResult(true);

	private List<String> validatedParams = new ArrayList<String>();

	public EjieValidatorHelperResult(boolean valid) {
		super(valid);
	}

	public List<String> getValidatedParams() {
		return validatedParams;
	}

	public void markAsValidated(String paramName) {
		validatedParams.add(paramName);
	}

	public EjieValidatorHelperResult markAsValidated(List<String> paramNames) {
		validatedParams.addAll(paramNames);
		return this;
	}

}
