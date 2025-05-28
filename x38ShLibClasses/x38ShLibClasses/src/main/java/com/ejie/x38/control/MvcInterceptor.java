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
package com.ejie.x38.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * Interceptor de UDA que se encarga de lo siguiente: 1- Gestiona las variables
 * relativas a idioma por defecto, idiomas de la aplicación y layout. En caso de
 * no definir alguna de las variables se producirá un error en despliegue
 * indicando la causa del error. 2- En la invocación a un controller gestiona
 * las cookie y el parámetro para la gestión del lenguage de la aplicación 3- En
 * el retorno de un controller se gestiona la variable del modelo para el layout
 * 
 * @author UDA
 *
 */
public class MvcInterceptor implements HandlerInterceptor {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private static final Logger logger = LoggerFactory.getLogger(MvcInterceptor.class);

	@Nullable
	private String[] httpMethods;
	private boolean ignoreInvalidLocale = false;
	private String paramName = LocaleChangeInterceptor.DEFAULT_PARAM_NAME;
	private String defaultLanguage;
	private String defaultLayout;
	private String availableLangs;
	private String portalCookie;

	/**
	 * Comprobar que se han definido correctamente las variables necesarias para la aplicación: 
	 * 	- defaultLanguage: idioma por defecto.
	 * 	- defaulLayout: layout por defecto.
	 * 	- availableLangs: idiomas permitidos en la aplicación.
	 */
	@PostConstruct
	public void postConstruct() {
		List<String> properties = new ArrayList<String>();
		if (this.defaultLanguage == null) {
			properties.add("defaultLanguage");
		}
		if (this.defaultLayout == null) {
			properties.add("defaultLayout");
		}
		if (this.availableLangs == null) {
			properties.add("availableLangs");
		}
		if (!properties.isEmpty()) {
			String war = webApplicationContext.getServletContext().getContextPath().substring(1);
			throw new IllegalStateException(
					"No se ha definido correctamente el bean 'MvcInterceptor' en el fichero mvc-config.xml del proyecto <"
							+ war + ">. Revisar propiedad(es):" + properties.toString());
		}
	}

	/**
	 * Método que se ejecuta antes del método del controlador.
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// Asignar valor por defecto.
		Locale locale = new Locale(defaultLanguage);
		// Obtener cookies de la sesión.
		Cookie[] cookies = request.getCookies();
		// Indica si se usará la cookie del portal.
		boolean usesPortalCookie = false;

		if (portalCookie != null && cookies != null) {
			// Idioma controlado por el portal.
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(portalCookie)) {
					String cookieValue = cookie.getValue();
					// Comprobar si la variable cookieValue contiene algún valor, en caso negativo,
					// se mantiene el valor por defecto.
					if (cookieValue != null && !cookieValue.isEmpty()) {
						String portalCookieValue = cookieValue.substring((cookieValue.indexOf("_") + 1));
						// Verificar si el idioma obtenido está entre los soportados.
						if (availableLangs.contains(portalCookieValue)) {
							locale = new Locale(portalCookieValue);
							usesPortalCookie = true;
							setLocale(request, response, locale);
						}
					}
					break;
				}
			}
		}

		if (!usesPortalCookie) {
			String newLocale = request.getParameter(getParamName());
			if (newLocale != null) {
				// Idioma definido a partir del parámetro de la petición (por defecto es "locale").
				// Si no cumple la condición se mantendrá el idioma en uso.
				if (checkHttpMethod(request.getMethod()) && availableLangs.contains(newLocale)) {
					setLocale(request, response, parseLocaleValue(newLocale));
				}
			} else {
				// Establece el idioma por defecto.
				setLocale(request, response, locale);
			}
		}

		return true;
	}

	private boolean checkHttpMethod(String currentMethod) {
		String[] configuredMethods = getHttpMethods();
		if (ObjectUtils.isEmpty(configuredMethods)) {
			return true;
		}
		for (String configuredMethod : configuredMethods) {
			if (configuredMethod.equalsIgnoreCase(currentMethod)) {
				return true;
			}
		}
		return false;
	}

	private void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver == null) {
			throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
		}
		try {
			localeResolver.setLocale(request, response, locale);
		} catch (IllegalArgumentException ex) {
			if (isIgnoreInvalidLocale()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Ignoring invalid locale value [{}]: {}", locale, ex.getMessage());
				}
			} else {
				throw ex;
			}
		}
	}

	/**
	 * Parse the given locale value as coming from a request parameter.
	 * <p>
	 * The default implementation calls {@link StringUtils#parseLocale(String)},
	 * accepting the {@link Locale#toString} format as well as BCP 47 language tags.
	 * 
	 * @param localeValue the locale value to parse
	 * @return the corresponding {@code Locale} instance
	 */
	@Nullable
	protected Locale parseLocaleValue(String localeValue) {
		return StringUtils.parseLocale(localeValue);
	}

	// ** SETTERs & GETTERs **//

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamName() {
		return this.paramName;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public String getDefaultLayout() {
		return defaultLayout;
	}

	public void setDefaultLayout(String defaultLayout) {
		this.defaultLayout = defaultLayout;
	}

	public String getAvailableLangs() {
		return availableLangs;
	}

	public void setAvailableLangs(String availableLangs) {
		this.availableLangs = availableLangs;
	}

	public String getPortalCookie() {
		return portalCookie;
	}

	public void setPortalCookie(String portalCookie) {
		this.portalCookie = portalCookie;
	}

	/**
	 * Configure the HTTP method(s) over which the locale can be changed.
	 * 
	 * @param httpMethods the methods
	 */
	public void setHttpMethods(@Nullable String... httpMethods) {
		this.httpMethods = httpMethods;
	}

	/**
	 * Return the configured HTTP methods.
	 */
	@Nullable
	public String[] getHttpMethods() {
		return this.httpMethods;
	}

	/**
	 * Set whether to ignore an invalid value for the locale parameter.
	 */
	public void setIgnoreInvalidLocale(boolean ignoreInvalidLocale) {
		this.ignoreInvalidLocale = ignoreInvalidLocale;
	}

	/**
	 * Return whether to ignore an invalid value for the locale parameter.
	 */
	public boolean isIgnoreInvalidLocale() {
		return this.ignoreInvalidLocale;
	}
}