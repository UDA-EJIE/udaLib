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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.NewCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Interceptor de UDA que se encarga de lo siguiente:
 * 1- Gestiona las variables relativas a idioma por defecto, idiomas de la aplicación y layout. En caso de no
 * definir alguna de las variables se producirá un error en despliegue indicando la causa del error.
 * 2- En la invocación a un controller gestiona las cookie y el parámetro para la gestión del lenguage de la
 * aplicación
 * 3- En el retorno de un controller se gestiona la variable del modelo para el layout
 *
 * @author UDA
 */
public class MvcInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private WebApplicationContext webApplicationContext;
    //** SETTERs & GETTERs **//
    private String paramName = LocaleChangeInterceptor.DEFAULT_PARAM_NAME;
    private String defaultLanguage;
    private String defaultLayout;
    private String availableLangs;
    private String portalCookie;

    /**
     * Comprobar que se han definido correctamente las variables necesarias para la aplicación:
     * - defaultLanguage : Idioma por defecto
     * - defaulLayout : Layout por defecto
     * - availableLangs: Idiomas permitidos en la aplicación
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
            throw new IllegalStateException("No se ha definido correctamente el bean 'MvcInterceptor' en el fichero mvc-config.xml del proyecto <" + war + ">. Revisar propiedad(es):" + properties.toString());
        }
    }

    /**
     * Método que se ejecuta antes del método del Controller
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Locale locale = null;

        if (portalCookie == null) {
            //Parametro cambio idioma
            String languageParam = request.getParameter(paramName);
            if (languageParam != null && availableLangs.contains(languageParam)) {
                locale = new Locale(languageParam);
            } else {
                //Cookies
                String cookieName = ((CookieLocaleResolver) RequestContextUtils.getLocaleResolver(request)).getCookieName();
                Cookie cookie = getLanguageCookie(request.getCookies(), cookieName, Arrays.asList(availableLangs.trim().split("\\s*,\\s*")));
                if (cookie != null) {
                    cookie.setSecure(true);
                    locale = new Locale(cookie.getValue());
                } else {
                    locale = new Locale(defaultLanguage);
                }
            }
        } else {
            //Idioma controlado por el portal
            Cookie[] cookies = request.getCookies();
            String cookieValue = "";
            for (Cookie cookie : cookies) {
                cookie.setSecure(true);
                if (cookie.getName().equals(portalCookie)) {
                    cookieValue = cookie.getValue();
                    break;
                }
            }
            locale = new Locale(cookieValue.substring((cookieValue.indexOf("_") + 1)));
        }

        //Modificación de la Locale y Cookie
        LocaleContextHolder.setLocale(locale);

        //Sobreescribir cookie
        RequestContextUtils.getLocaleResolver(request).setLocale(request, response, locale);

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                response.setHeader("SET-COOKIE", getHttpOnlyCookieHeader(cookie));
            }
        }

        return true;
    }

    private String getHttpOnlyCookieHeader(Cookie cookie) {

        NewCookie newCookie = new NewCookie(cookie.getName(), cookie.getValue(),
                cookie.getPath(), cookie.getDomain(), cookie.getVersion(),
                cookie.getComment(), cookie.getMaxAge(), cookie.getSecure());

        return newCookie + "; HttpOnly";
    }

    /**
     * Función que busca la cookie de idioma y determina si es idioma válido (devuelve la cookie)
     *
     * @param cookies        Conjunto de cookies de la request
     * @param cookieName     Nombre de la cookie de idioma (default 'language')
     * @param availableLangs Lista con los idiomas posibles (es, eu, en, fr)
     * @return Si es un idioma válido devuelve la cookie, si no devuelve null
     */
    private Cookie getLanguageCookie(Cookie[] cookies, String cookieName, List<String> availableLangs) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName) && availableLangs.contains(cookie.getValue())) {
                    cookie.setSecure(true);
                    return cookie;
                }
            }
        }
        return null;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
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
}
