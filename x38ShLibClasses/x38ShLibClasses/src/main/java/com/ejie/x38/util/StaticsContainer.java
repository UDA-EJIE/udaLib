/*
* Copyright 2011 E.J.I.E., S.A.
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
package com.ejie.x38.util;

/**
 * 
 * @author UDA
 *
 */
public class StaticsContainer {

	public static String webAppName;
	public static String webId;
	public static String staticsUrl;
	public static String modelPackageName;
	public static String loginUrl;
	public static boolean aplicInPortal;
	public static String weblogicInstance;
	public static String layout;
	public static String language;
	public static boolean cookiePathRoot;
	public static boolean cookieSecure;
	public static boolean inheritableLocalContext;
	public static boolean xhrRedirectOnError;
	public static String xhrUnauthorizedPage;
	public static boolean detailedError;

	public static String getWebAppName() {
		return webAppName;
	}

	public static void setWebAppName(String webAppName) {
		StaticsContainer.webAppName = webAppName;
	}

	public static String getWebId() {
		return webId;
	}

	public static void setWebId(String webId) {
		StaticsContainer.webId = webId;
	}

	public static String getStaticsUrl() {
		return staticsUrl;
	}

	public static void setStaticsUrl(String staticsUrl) {
		StaticsContainer.staticsUrl = staticsUrl;
	}

	public static String getModelPackageName() {
		return modelPackageName;
	}

	public static void setModelPackageName(String modelPackageName) {
		StaticsContainer.modelPackageName = modelPackageName;
	}

	public static String getLoginUrl() {
		return loginUrl;
	}

	public static void setLoginUrl(String loginUrl) {
		StaticsContainer.loginUrl = loginUrl;
	}

	public static boolean isAplicInPortal() {
		return aplicInPortal;
	}

	public static void setAplicInPortal(boolean aplicInPortal) {
		StaticsContainer.aplicInPortal = aplicInPortal;
	}

	public static String getWeblogicInstance() {
		return weblogicInstance;
	}

	public static void setWeblogicInstance(String weblogicInstance) {
		StaticsContainer.weblogicInstance = weblogicInstance;
	}

	public static String getLayout() {
		return layout;
	}

	public static void setLayout(String layout) {
		StaticsContainer.layout = layout;
	}

	public static String getLanguage() {
		return language;
	}

	public static void setLanguage(String language) {
		StaticsContainer.language = language;
	}

	public static boolean isCookiePathRoot() {
		return cookiePathRoot;
	}

	public static void setCookiePathRoot(boolean cookiePathRoot) {
		StaticsContainer.cookiePathRoot = cookiePathRoot;
	}

	public static boolean isCookieSecure() {
		return cookieSecure;
	}

	public static void setCookieSecure(boolean cookieSecure) {
		StaticsContainer.cookieSecure = cookieSecure;
	}

	public static boolean isInheritableLocalContext() {
		return inheritableLocalContext;
	}

	public static void setInheritableLocalContext(boolean inheritableLocalContext) {
		StaticsContainer.inheritableLocalContext = inheritableLocalContext;
	}

	public static boolean isXhrRedirectOnError() {
		return xhrRedirectOnError;
	}

	public static void setXhrRedirectOnError(boolean xhrRedirectOnError) {
		StaticsContainer.xhrRedirectOnError = xhrRedirectOnError;
	}

	public static String getXhrUnauthorizedPage() {
		return xhrUnauthorizedPage;
	}

	public static void setXhrUnauthorizedPage(String xhrUnauthorizedPage) {
		StaticsContainer.xhrUnauthorizedPage = xhrUnauthorizedPage;
	}

	public static boolean isDetailedError() {
		return detailedError;
	}

	public static void setDetailedError(boolean detailedError) {
		StaticsContainer.detailedError = detailedError;
	}

}