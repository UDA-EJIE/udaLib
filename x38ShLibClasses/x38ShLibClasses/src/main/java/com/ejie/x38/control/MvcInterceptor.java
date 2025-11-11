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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
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

	private static final Logger logger = LoggerFactory.getLogger(MvcInterceptor.class);

	private WebApplicationContext webApplicationContext;

	@Nullable
	private String[] httpMethods;
	private boolean ignoreInvalidLocale = false;
	private String paramName = LocaleChangeInterceptor.DEFAULT_PARAM_NAME;
	private String defaultLanguage;
	private String defaultLayout;
	private String availableLangs;
	private String portalCookie;
	private String xlnetsCookie;

	// Constructor con inyección de dependencias.
	public MvcInterceptor(WebApplicationContext webApplicationContext) {
		this.webApplicationContext = webApplicationContext;
	}

	/**
	 * Comprobar que se han definido correctamente las variables necesarias para la aplicación: 
	 * 	- defaultLanguage: idioma por defecto.
	 * 	- defaulLayout: layout por defecto.
	 * 	- availableLangs: idiomas permitidos en la aplicación.
	 */
	@PostConstruct
	public void postConstruct() {
		List<String> properties = new ArrayList<>();
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
			// Validar que ServletContext no sea null.
			ServletContext servletContext = webApplicationContext.getServletContext();
			String war = (servletContext != null) ? servletContext.getContextPath().substring(1) : "unknown";

			throw new IllegalStateException(
					"No se ha definido correctamente el bean 'MvcInterceptor' en el fichero mvc-config.xml del proyecto <"
							+ war + ">. Revisar propiedad(es):" + properties.toString());
		}
	}

	/**
	 * Método que se ejecuta antes del método del controlador.
	 * <p>
	 * Gestiona el establecimiento del locale (idioma) de la aplicación basándose en:
	 * <ol>
	 *   <li>Cookie del portal (si está configurada, tiene prioridad máxima)</li>
	 *   <li>Parámetro de la petición HTTP (elección explícita del usuario)</li>
	 *   <li>Idioma actual en sesión (si fue establecido previamente por el usuario)</li>
	 *   <li>Cookie n38Idioma de XLNetS (si está configurada, solo como valor inicial)</li>
	 *   <li>Idioma por defecto</li>
	 * </ol>
	 * 
	 * @param request la petición HTTP actual
	 * @param response la respuesta HTTP actual
	 * @param handler el objeto handler seleccionado para ejecutar
	 * @return {@code true} para continuar con la cadena de ejecución, {@code false} para detenerla
	 * @throws Exception si ocurre algún error durante el procesamiento
	 * @throws IllegalStateException si no se encuentra un LocaleResolver configurado
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver == null) {
			throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
		}

		Locale currentLocale = localeResolver.resolveLocale(request);
		Locale localeToSet = resolveTargetLocale(request, currentLocale);

		if (!Objects.equals(localeToSet, currentLocale)) {
			logger.debug("Changing locale from {} to {}", currentLocale, localeToSet);
			setLocale(request, response, localeToSet);
		}

		return true;
	}

	/**
	 * Resuelve el idioma objetivo basándose en múltiples fuentes con el siguiente orden de prioridades.
	 * <p>
	 * <strong>Orden de prioridades:</strong>
	 * <ol>
	 *   <li><strong>Cookie del portal</strong> - Prevalece sobre todo (si está configurada mediante {@link #setPortalCookie(String)})</li>
	 *   <li><strong>Parámetro de petición</strong> - Elección explícita del usuario, persiste en sesión</li>
	 *   <li><strong>Idioma en sesión</strong> - Preferencia del usuario establecida previamente</li>
	 *   <li><strong>Cookie n38Idioma de XLNetS</strong> - Solo se usa como valor inicial si no hay preferencia del usuario 
	 *       (si está configurada mediante {@link #setXlnetsCookie(String)})</li>
	 *   <li><strong>Idioma por defecto</strong> - Último recurso</li>
	 * </ol>
	 * <p>
	 * <strong>Comportamiento importante:</strong> Una vez que el usuario establece un idioma mediante el parámetro,
	 * este se mantiene en sesión y tiene prioridad sobre la cookie n38Idioma en navegaciones subsecuentes.
	 * La cookie n38Idioma solo se utiliza como valor inicial cuando el usuario aún no ha establecido
	 * una preferencia explícita.
	 * 
	 * @param request la petición HTTP actual
	 * @param currentLocale el locale actualmente establecido en la sesión
	 * @return el locale que debe establecerse, nunca {@code null}
	 */
	private Locale resolveTargetLocale(HttpServletRequest request, Locale currentLocale) {
		// Prioridad 1: cookie del portal (SIEMPRE prevalece, si está configurada).
		Locale portalLocale = extractCookieLocale(request, portalCookie, this::parsePortalCookie);
		if (portalLocale != null) {
			logger.debug("Using portal cookie locale: {}", portalLocale);
			return portalLocale;
		}

		// Prioridad 2: parámetro de petición (elección explícita del usuario).
		// Cuando el usuario cambia el idioma mediante parámetro, este se persiste en sesión
		// y tendrá prioridad sobre n38Idioma en futuras navegaciones.
		Locale parameterLocale = extractParameterLocale(request);
		if (parameterLocale != null) {
			logger.debug("Using user parameter locale: {}", parameterLocale);
			return parameterLocale;
		}

		// Prioridad 3: idioma actual en sesión (si fue establecido previamente).
		// Si el usuario ya tiene un idioma en sesión (establecido por parámetro anteriormente),
		// este prevalece sobre la cookie n38Idioma.
		if (!currentLocale.getLanguage().isEmpty()) {
			logger.debug("Using current session locale: {}", currentLocale);
			return currentLocale;
		}

		// Prioridad 4: cookie n38Idioma de XLNetS (solo como valor inicial, si está configurada).
		// Solo se usa si el usuario no tiene ninguna preferencia establecida en sesión.
		Locale xlnetsLocale = extractCookieLocale(request, xlnetsCookie, this::parseXLNetSCookie);
		if (xlnetsLocale != null) {
			logger.debug("Using XLNetS n38Idioma cookie locale as initial value: {}", xlnetsLocale);
			return xlnetsLocale;
		}

		// Prioridad 5: idioma por defecto.
		Locale defaultLocale = new Locale(defaultLanguage);
		logger.debug("Using default locale: {}", defaultLocale);
		return defaultLocale;
	}

	/**
	 * Extrae el idioma desde una cookie específica utilizando un procesador personalizado.
	 * <p>
	 * Método genérico que busca una cookie por nombre y aplica la lógica de procesamiento
	 * correspondiente para extraer el locale.
	 * 
	 * @param request la petición HTTP que puede contener cookies
	 * @param cookieName el nombre de la cookie a buscar, puede ser {@code null}
	 * @param cookieProcessor función que procesa el valor de la cookie encontrada
	 * @return el locale extraído de la cookie, o {@code null} si:
	 *         <ul>
	 *           <li>El nombre de la cookie es {@code null}</li>
	 *           <li>No hay cookies en la petición</li>
	 *           <li>No se encuentra la cookie específica</li>
	 *           <li>El procesador devuelve {@code null}</li>
	 *         </ul>
	 */
	private Locale extractCookieLocale(HttpServletRequest request, String cookieName, 
									   java.util.function.Function<Cookie, Locale> cookieProcessor) {
		if (cookieName == null) {
			return null;
		}

		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		return Arrays.stream(cookies)
				.filter(cookie -> cookieName.equals(cookie.getName()))
				.findFirst()
				.map(cookieProcessor)
				.orElse(null);
	}

	/**
	 * Procesa el valor de la cookie del portal para extraer el código de idioma.
	 * <p>
	 * Soporta dos formatos de cookie:
	 * <ul>
	 *   <li><strong>Formato con guión bajo:</strong> {@code "somevalue_eu"} → extrae {@code "eu"}</li>
	 *   <li><strong>Formato directo:</strong> {@code "eu"} → usa {@code "eu"} directamente</li>
	 * </ul>
	 * <p>
	 * El idioma extraído debe estar presente en la lista de idiomas disponibles
	 * configurada en {@link #availableLangs} para ser considerado válido.
	 * 
	 * @param cookie la cookie del portal a procesar, no debe ser {@code null}
	 * @return el locale correspondiente al idioma extraído, o {@code null} si:
	 *         <ul>
	 *           <li>El valor de la cookie está vacío o es {@code null}</li>
	 *           <li>El idioma extraído no está en la lista de idiomas disponibles</li>
	 *         </ul>
	 */
	private Locale parsePortalCookie(Cookie cookie) {
		String cookieValue = cookie.getValue();
		logger.debug("Parsing portal cookie value: {}", cookieValue);

		if (cookieValue == null || cookieValue.isEmpty()) {
			return null;
		}

		// Verificar si el formato tiene guión bajo.
		int underscoreIndex = cookieValue.indexOf("_");
		String language;

		if (underscoreIndex != -1 && underscoreIndex < cookieValue.length() - 1) {
			// Formato con guión bajo. Por ejemplo: "somevalue_eu".
			language = cookieValue.substring(underscoreIndex + 1);
			logger.debug("Extracted language from underscore format: {}", language);
		} else {
			// Formato directo. Por ejemplo: "eu".
			language = cookieValue;
			logger.debug("Using direct language format: {}", language);
		}

		boolean isValidLanguage = availableLangs.contains(language);
		logger.debug("Is language '{}' valid? {} (available: {})", language, isValidLanguage, availableLangs);

		return isValidLanguage ? new Locale(language) : null;
	}

	/**
	 * Extrae el idioma desde el parámetro de la petición HTTP.
	 * <p>
	 * Verifica que:
	 * <ul>
	 *   <li>El parámetro esté presente en la petición</li>
	 *   <li>El método HTTP sea válido (según configuración)</li>
	 *   <li>El idioma solicitado esté en la lista de idiomas disponibles</li>
	 *   <li>El valor del parámetro se pueda procesar correctamente</li>
	 * </ul>
	 * 
	 * @param request la petición HTTP que puede contener el parámetro de idioma
	 * @return el locale correspondiente al parámetro, o {@code null} si:
	 *         <ul>
	 *           <li>No hay parámetro de idioma en la petición</li>
	 *           <li>El método HTTP no está permitido</li>
	 *           <li>El idioma no está en la lista de disponibles</li>
	 *           <li>El procesamiento del valor falla</li>
	 *         </ul>
	 * @see #getParamName()
	 * @see #checkHttpMethod(String)
	 * @see #parseLocaleValue(String)
	 */
	private Locale extractParameterLocale(HttpServletRequest request) {
		String localeParam = request.getParameter(getParamName());
		if (localeParam == null)
			return null;

		boolean isValidRequest = checkHttpMethod(request.getMethod()) && availableLangs.contains(localeParam);
		if (!isValidRequest)
			return null;

		Locale parsed = parseLocaleValue(localeParam);
		if (parsed == null) {
			logger.warn("parseLocaleValue returned null for: {}", localeParam);
		}
		return parsed;
	}

	/**
	 * Procesa el valor de la cookie n38Idioma de XLNetS para extraer y mapear el código de idioma.
	 * <p>
	 * Realiza el mapeo de valores específicos de XLNetS a códigos de idioma estándar:
	 * <ul>
	 *   <li><strong>Eusk</strong> → {@code "eu"} (euskera)</li>
	 *   <li><strong>Cast</strong> → {@code "es"} (castellano)</li>
	 * </ul>
	 * <p>
	 * El idioma mapeado se valida contra la lista de idiomas disponibles configurada
	 * en {@link #availableLangs}.
	 * 
	 * @param cookie la cookie n38Idioma a procesar, no debe ser {@code null}
	 * @return el locale correspondiente al idioma mapeado, o {@code null} si:
	 *         <ul>
	 *           <li>El valor de la cookie está vacío o es {@code null}</li>
	 *           <li>El valor no coincide con "Eusk" ni "Cast"</li>
	 *           <li>El idioma mapeado no está en la lista de idiomas disponibles</li>
	 *         </ul>
	 */
	private Locale parseXLNetSCookie(Cookie cookie) {
		String cookieValue = cookie.getValue();
		logger.debug("Processing XLNetS n38Idioma cookie value: {}", cookieValue);

		if (cookieValue == null || cookieValue.isEmpty()) {
			logger.debug("XLNetS cookie value is null or empty");
			return null;
		}

		// Mapear valores de XLNetS a códigos de idioma estándar directamente
		String language = null;
		String trimmedValue = cookieValue.trim();
		
		switch (trimmedValue) {
			case "Eusk":
				language = "eu";
				break;
			case "Cast":
				language = "es";
				break;
			default:
				logger.debug("Unknown XLNetS language value: {}", cookieValue);
				return null;
		}

		// Validar que el idioma mapeado esté disponible
		boolean isValidLanguage = availableLangs.contains(language);
		logger.debug("Mapped XLNetS '{}' to '{}', is valid? {} (available: {})", 
					 cookieValue, language, isValidLanguage, availableLangs);

		return isValidLanguage ? new Locale(language) : null;
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

	private void setLocale(HttpServletRequest request, HttpServletResponse response, @NonNull Locale locale) {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver == null) {
			throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
		}

		// Verificar estado antes del cambio.
		Locale beforeLocale = localeResolver.resolveLocale(request);
		logger.debug("Setting locale - Before: {}, Target: {}", beforeLocale, locale);

		try {
			localeResolver.setLocale(request, response, locale);

			// Verificar estado después del cambio.
			Locale afterLocale = localeResolver.resolveLocale(request);
			logger.debug("Setting locale - After: {}", afterLocale);

			if (!locale.equals(afterLocale)) {
				logger.warn("Locale may not have been set correctly. Expected: {}, Actual: {}", locale, afterLocale);
			}

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
	 * Procesa el valor del locale proveniente de un parámetro de petición.
	 * <p>
	 * La implementación por defecto utiliza {@link StringUtils#parseLocale(String)},
	 * aceptando el formato {@link Locale#toString} así como etiquetas de idioma BCP 47.
	 * 
	 * @param localeValue el valor del locale a procesar
	 * @return la instancia {@code Locale} correspondiente
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
	 * Establece el nombre de la cookie de XLNetS para detección automática de idioma.
	 * <p>
	 * Si se configura (típicamente como "n38Idioma"), el interceptor intentará detectar
	 * el idioma desde esta cookie del sistema de seguridad XLNetS. La cookie solo se
	 * utiliza como valor inicial cuando el usuario no ha establecido una preferencia
	 * explícita mediante parámetros.
	 * <p>
	 * Si se establece a {@code null}, la detección desde XLNetS se desactiva.
	 * 
	 * @param xlnetsCookie el nombre de la cookie de XLNetS, o {@code null} para desactivar
	 */
	public void setXlnetsCookie(String xlnetsCookie) {
		this.xlnetsCookie = xlnetsCookie;
	}

	/**
	 * Obtiene el nombre de la cookie de XLNetS configurada.
	 * 
	 * @return el nombre de la cookie de XLNetS, o {@code null} si no está configurada
	 */
	public String getXlnetsCookie() {
		return xlnetsCookie;
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
