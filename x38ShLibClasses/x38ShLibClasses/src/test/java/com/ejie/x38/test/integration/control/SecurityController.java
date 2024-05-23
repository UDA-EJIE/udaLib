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
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ExceptionController
 * 
 * @author Eurohelp S.L.
 */
@Controller
@RequestMapping(value = "/security")
public class SecurityController {

	private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

	@Resource
	private ReloadableResourceBundleMessageSource messageSource;

	@RequestMapping(value = "test", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getTest() {
		SecurityController.logger.info("[GET][/security/test]test");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("principal", auth.getPrincipal());
		mapa.put("authorities", auth.getAuthorities());

		return mapa;
	}

	@RequestMapping(value = "get", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> get() throws Exception {
		SecurityController.logger.info("[GET][/security/get]get");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("principal", auth.getPrincipal());
		mapa.put("authorities", auth.getAuthorities());

		return mapa;
	}

	@RequestMapping(value = "post", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> post() throws Exception {
		SecurityController.logger.info("[POST][/security/post]post()");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("principal", auth.getPrincipal());
		mapa.put("authorities", auth.getAuthorities());

		return mapa;
	}

	@RequestMapping(value = "put", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> put() throws Exception {
		SecurityController.logger.info("[PUT][/security/put]put()");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("principal", auth.getPrincipal());
		mapa.put("authorities", auth.getAuthorities());

		return mapa;
	}

	@Secured("ROLE_FOO")
	@RequestMapping(value = "security", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> security() throws Exception {
		SecurityController.logger.info("[GET][/security/security]security");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("principal", auth.getPrincipal());
		mapa.put("authorities", auth.getAuthorities());

		return mapa;
	}
}
