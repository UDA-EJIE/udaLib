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
package com.ejie.x38.test.control;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ejie.x38.test.common.model.Alumno;
import com.ejie.x38.test.common.model.Coche;
import com.ejie.x38.test.common.model.validation.customvalidator.CocheValidator;
import com.ejie.x38.validation.ValidationManager;

/**
 * SerializationController
 * 
 * @author Eurohelp S.L.
 */
@Controller
@RequestMapping(value = "/validation")
public class ValidationController {

	private static final Logger logger = LoggerFactory.getLogger(ValidationController.class);

	@Autowired
	private ValidationManager validationManager;

	@Resource
	private ReloadableResourceBundleMessageSource messageSource;

	@RequestMapping(value = "test", method = RequestMethod.GET)
	public @ResponseBody Object getTest() {
		ValidationController.logger.info("[GET][/validation/test]test");

		Map<String, String> mapa = new HashMap<String, String>();
		mapa.put("respuesta", "ok");

		return mapa;
	}

	/****************************************************************************
	 * /** INICIO - VALIDACIONES HIBERNATE /
	 ****************************************************************************/
	@RequestMapping(value = "hibernate/get", method = RequestMethod.GET)
	public @ResponseBody Object hibernateGet(@Validated @RequestBody Alumno alumno, Locale locale) {
		ValidationController.logger.info("[GET][/validation/hibernate/get]hibernateGet()");
		return alumno;
	}

	@RequestMapping(value = "hibernate/post", method = RequestMethod.POST)
	public @ResponseBody Object hibernatePost(@Validated @RequestBody Alumno alumno, Locale locale) {
		ValidationController.logger.info("[POST][/validation/hibernate/post]hibernatePost()");
		return alumno;
	}

	@RequestMapping(value = "hibernate/put", method = RequestMethod.PUT)
	public @ResponseBody Object hibernatePut(@Validated @RequestBody Alumno alumno, Locale locale) {
		ValidationController.logger.info("[PUT][/validation/hibernate/put]hibernatePut()");
		return alumno;
	}

	/****************************************************************************
	 * /** FIN - VALIDACIONES HIBERNATE /
	 ****************************************************************************/

	/****************************************************************************
	 * /** INICIO - VALIDACIONES PROPIAS /
	 ****************************************************************************/
	private Object customValidation(Coche coche, HttpServletResponse response) {
		Errors errors = new BeanPropertyBindingResult(coche, "coche");
		CocheValidator cocheValidator = new CocheValidator();
		cocheValidator.validate(coche, errors);

		if (errors.hasErrors()) {
			try {
				Map<String, List<String>> fieldErrors = this.validationManager.getErrorsAsMap(errors);
				response.sendError(406, this.validationManager.getMessageJSON(fieldErrors).toString());
			} catch (IOException e) {
				throw new ValidationException("Error procesando validación", e);
			}
		}

		return coche;
	}

	@RequestMapping(value = "custom/get", method = RequestMethod.GET)
	public @ResponseBody Object customGet(@RequestBody Coche coche, HttpServletResponse response) {
		ValidationController.logger.info("[GET][/validation/custom/get]hibernateGet()");
		return customValidation(coche, response);
	}

	@RequestMapping(value = "custom/post", method = RequestMethod.POST)
	public @ResponseBody Object customPost(@RequestBody Coche coche, HttpServletResponse response) {
		ValidationController.logger.info("[POST][/validation/custom/post]hibernatePost()");
		return customValidation(coche, response);
	}

	@RequestMapping(value = "custom/put", method = RequestMethod.PUT)
	public @ResponseBody Object customPut(@RequestBody Coche coche, HttpServletResponse response) {
		ValidationController.logger.info("[PUT][/validation/custom/put]hibernatePut()");
		return customValidation(coche, response);
	}
	/****************************************************************************
	 * /** FIN - VALIDACIONES PROPIAS /
	 ****************************************************************************/
}
