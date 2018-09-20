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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.ejie.x38.test.common.model.Alumno;

/**
 * SerializationController
 * 
 * @author Eurohelp S.L.
 */
@Controller
@RequestMapping(value = "/validation")
public class ValidationController {

	private static final Logger logger = LoggerFactory.getLogger(ValidationController.class);

	@Resource
	private ReloadableResourceBundleMessageSource messageSource;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	@RequestMapping(value = "test", method = RequestMethod.GET)
	public @ResponseBody Object getTest() {
		ValidationController.logger.info("[GET][/validation/test]test");

		Map<String, String> mapa = new HashMap<String, String>();
		mapa.put("respuesta", "ok");

		return mapa;
	}

	@RequestMapping(value = "get", method = RequestMethod.GET)
	public @ResponseBody Object get(@Validated @RequestBody Alumno alumno, Locale locale) {
		ValidationController.logger.info("[GET][/validation/get]get()");
		return alumno;
	}

	@RequestMapping(value = "post", method = RequestMethod.POST)
	public @ResponseBody Object post(@Validated @RequestBody Alumno alumno, Locale locale) {
		ValidationController.logger.info("[POST][/validation/post]post()");
		return alumno;
	}

	@RequestMapping(value = "put", method = RequestMethod.PUT)
	public @ResponseBody Object put(@Validated @RequestBody Alumno alumno, Locale locale) {
		ValidationController.logger.info("[POST][/validation/put]put()");
		return alumno;
	}
}
