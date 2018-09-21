package com.ejie.x38.test.common.model.validation.customvalidator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ejie.x38.test.common.model.Coche;

/**
 * @author Eurohelp S.L.
 *
 */
public class CocheValidator implements Validator {
	private static final String VALIDACION_MAXLENGTH = "base.rup_validate.messages.maxlength";
	private static final String VALIDACION_REQUIRED = "base.rup_validate.messages.required";

	private static final int ML_MODELO = 10;

	@Override()
	public boolean supports(Class<?> clazz) {
		return clazz.equals(Coche.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override()
	public void validate(Object obj, Errors errors) {
		Coche coche = (Coche) obj;
		if (coche != null) {
			validateModelo(errors, coche);
		}
	}

	/**
	 * @param errors Errors
	 * @param coche  Coche
	 */
	private void validateModelo(Errors errors, Coche coche) {
		if (StringUtils.isEmpty(coche.getModelo())) {
			errors.rejectValue("modelo", VALIDACION_REQUIRED);
		} else {
			if (coche.getModelo().length() > CocheValidator.ML_MODELO) {
				errors.rejectValue("modelo", VALIDACION_MAXLENGTH + "___" + CocheValidator.ML_MODELO);
			}
		}
	}
}
