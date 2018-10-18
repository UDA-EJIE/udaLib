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
package com.ejie.x38.test.integration.control;

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

import com.ejie.x38.json.MessageWriter;
import com.ejie.x38.test.model.Alumno;
import com.ejie.x38.test.model.Coche;
import com.ejie.x38.test.model.validation.customvalidator.CocheValidator;
import com.ejie.x38.test.model.validation.group.AlumnoEditValidation;
import com.ejie.x38.validation.ValidationManager;

/**
 * ValidationController
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
	 * INICIO - VALIDACIONES HIBERNATE
	 ****************************************************************************/
	@RequestMapping(value = "hibernate/get", method = RequestMethod.GET)
	public @ResponseBody Object hibernateGet(@RequestBody Alumno alumno, Locale locale, HttpServletResponse response) {
		ValidationController.logger.info("[GET][/validation/hibernate/get]hibernateGet()");
		Errors errors = new BeanPropertyBindingResult(alumno, "alumno");
		validationManager.validate(errors, alumno, AlumnoEditValidation.class);

		if (errors.hasErrors()) {
			Map<String, List<String>> fieldErrors = validationManager.getErrorsAsMap(errors);
			try {
				response.sendError(406, validationManager.getMessageJSON(fieldErrors).toString());
			} catch (IOException e) {
				return null;
			}
			return null;
		}

		MessageWriter messageWriter = new MessageWriter();
		messageWriter.startMessageList();
		messageWriter.addMessage("OK");
		messageWriter.endMessageList();
		return messageWriter.toString();
	}

	@RequestMapping(value = "hibernate/post", method = RequestMethod.POST)
	public @ResponseBody Object hibernatePost(@RequestBody Alumno alumno, Locale locale, HttpServletResponse response) {
		ValidationController.logger.info("[POST][/validation/hibernate/post]hibernatePost()");
		Errors errors = new BeanPropertyBindingResult(alumno, "alumno");
		validationManager.validate(errors, alumno, AlumnoEditValidation.class);

		if (errors.hasErrors()) {
			Map<String, List<String>> fieldErrors = validationManager.getErrorsAsMap(errors);
			try {
				response.sendError(406, validationManager.getMessageJSON(fieldErrors).toString());
			} catch (IOException e) {
				return null;
			}
			return null;
		}

		MessageWriter messageWriter = new MessageWriter();
		messageWriter.startMessageList();
		messageWriter.addMessage("OK");
		messageWriter.endMessageList();
		return messageWriter.toString();
	}

	@RequestMapping(value = "hibernate/put", method = RequestMethod.PUT)
	public @ResponseBody Object hibernatePut(@RequestBody Alumno alumno, Locale locale, HttpServletResponse response) {
		ValidationController.logger.info("[PUT][/validation/hibernate/put]hibernatePut()");
		Errors errors = new BeanPropertyBindingResult(alumno, "alumno");
		validationManager.validate(errors, alumno, AlumnoEditValidation.class);

		if (errors.hasErrors()) {
			Map<String, List<String>> fieldErrors = validationManager.getErrorsAsMap(errors);
			try {
				response.sendError(406, validationManager.getMessageJSON(fieldErrors).toString());
			} catch (IOException e) {
				return null;
			}
			return null;
		}

		MessageWriter messageWriter = new MessageWriter();
		messageWriter.startMessageList();
		messageWriter.addMessage("OK");
		messageWriter.endMessageList();
		return messageWriter.toString();
	}

	/****************************************************************************
	 * FIN - VALIDACIONES HIBERNATE
	 ****************************************************************************/

	/****************************************************************************
	 * INICIO - VALIDACIONES SPRING + HIBERNATE
	 ****************************************************************************/
	@RequestMapping(value = "springHibernate/get", method = RequestMethod.GET)
	public @ResponseBody Object springHibernateGet(@Validated @RequestBody Alumno alumno, Locale locale) {
		ValidationController.logger.info("[GET][/validation/springHibernate/get]springHibernateGet()");
		return alumno;
	}

	@RequestMapping(value = "springHibernate/post", method = RequestMethod.POST)
	public @ResponseBody Object springHibernatePost(@Validated @RequestBody Alumno alumno, Locale locale) {
		ValidationController.logger.info("[POST][/validation/springHibernate/post]springHibernatePost()");
		return alumno;
	}

	@RequestMapping(value = "springHibernate/put", method = RequestMethod.PUT)
	public @ResponseBody Object springHibernatePut(@Validated @RequestBody Alumno alumno, Locale locale) {
		ValidationController.logger.info("[PUT][/validation/springHibernate/put]springHibernatePut()");
		return alumno;
	}

	/****************************************************************************
	 * FIN - VALIDACIONES SPRING + HIBERNATE
	 ****************************************************************************/

	/****************************************************************************
	 * INICIO - VALIDACIONES PROPIAS
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
		ValidationController.logger.info("[GET][/validation/custom/get]customGet()");
		return customValidation(coche, response);
	}

	@RequestMapping(value = "custom/post", method = RequestMethod.POST)
	public @ResponseBody Object customPost(@RequestBody Coche coche, HttpServletResponse response) {
		ValidationController.logger.info("[POST][/validation/custom/post]customPost()");
		return customValidation(coche, response);
	}

	@RequestMapping(value = "custom/put", method = RequestMethod.PUT)
	public @ResponseBody Object customPut(@RequestBody Coche coche, HttpServletResponse response) {
		ValidationController.logger.info("[PUT][/validation/custom/put]customPut()");
		return customValidation(coche, response);
	}
	/****************************************************************************
	 * FIN - VALIDACIONES PROPIAS
	 ****************************************************************************/
}
