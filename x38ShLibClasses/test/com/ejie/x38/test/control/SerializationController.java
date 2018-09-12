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
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;

import com.ejie.x38.test.common.model.Coche;

/**
 * PatronesController
 * 
 * @author UDA
 */
@Controller
@RequestMapping(value = "/serialization")
public class SerializationController {

	private static final Logger logger = LoggerFactory.getLogger(SerializationController.class);

	@Autowired
	private Properties appConfiguration;

	@Resource
	private ReloadableResourceBundleMessageSource messageSource;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	@RequestMapping(value = "test", method = RequestMethod.GET)
	public @ResponseBody Object getTest() {
		SerializationController.logger.info("[GET][/serialization/test]test");

		Map<String, String> mapa = new HashMap<String, String>();
		mapa.put("respuesta", "ok");

		return mapa;
	}

	@RequestMapping(value = "serialize", method = RequestMethod.POST)
	public @ResponseBody Object serialize(@RequestBody String json, @RequestBody Coche coche, Locale locale) {
		SerializationController.logger.info("[POST][/serialization/serialize]serialize("+json+")");
		return coche;
	}

	// Total control - setup a model and return the view name yourself. Or
	// consider subclassing ExceptionHandlerExceptionResolver (see below).
	@ExceptionHandler(Exception.class)
	public ModelAndView handleError(HttpServletRequest req, Exception ex) throws Exception {
		SerializationController.logger.error("Request: " + req.getRequestURL() + " raised " + ex);
		throw ex;
	}
}
