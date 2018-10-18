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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.ejie.x38.test.model.Coche;

/**
 * SerializationController
 * 
 * @author Eurohelp S.L.
 */
@Controller
@RequestMapping(value = "/serialization")
public class SerializationController {

	private static final Logger logger = LoggerFactory.getLogger(SerializationController.class);

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
	public @ResponseBody Object serialize(@RequestBody Coche coche, Locale locale) {
		SerializationController.logger.info("[POST][/serialization/serialize]serialize()");
		LocaleContextHolder.setLocale(locale);
		return coche;
	}
}
