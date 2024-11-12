package com.ejie.x38.rss.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wrapper de la request HTTP utilizado para añadir información de autorización
 * del usaurio al contenido RSS.
 * 
 * @author UDA
 *
 */
public class RssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	/**
	 * Lista de cookies añadidas a la request HTTP
	 */
	public List<Cookie> extraCookies;

	/**
	 * Crea una nueva request HTTP a partir de la indicada por parámetro. Se le
	 * añadirán las cookies existentes en la propiedad extraCookies.
	 * 
	 * @param paramHttpServletRequest
	 */
	public RssHttpServletRequestWrapper(HttpServletRequest paramHttpServletRequest) {
		super(paramHttpServletRequest);
		extraCookies = new ArrayList<Cookie>();
		Cookie[] cookies = paramHttpServletRequest.getCookies();

		if (cookies != null && cookies.length > 0) {
			List<Cookie> asList = Arrays.asList();
			extraCookies.addAll(asList);
		}
	}

	/**
	 * Añade una cookie a la propiedad que contiene la lista extra de cookies.
	 * 
	 * @param cookie
	 *            Cookie extra a añadir a la propiedad extraCookies.
	 */
	public void addCookie(Cookie cookie) {
		extraCookies.add(cookie);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cookie[] getCookies() {
		Cookie[] cookieArray = new Cookie[extraCookies.size()];
		return this.extraCookies.toArray(cookieArray);
	}

}