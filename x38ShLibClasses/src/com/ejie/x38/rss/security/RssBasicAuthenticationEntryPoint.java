package com.ejie.x38.rss.security;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import com.ejie.x38.rss.exception.RssInitializationException;

/**
 * Implementa el punto de acceso que se utiliza para autenticar al usuario que
 * trata de acceder al contenido de un feed RSS.
 * 
 * @author UDA
 *
 */
public class RssBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

	@Autowired(required = false)
	private ReloadableResourceBundleMessageSource messageSource;

	/**
	 * Key utilizada para internacionalizar el realName del contenido RSS.
	 */
	private String i18nRealmName;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		if (this.i18nRealmName != null && messageSource == null) {
			throw new RssInitializationException("No se puede resolver el valor de la propiedad i18nRealmName al no existir un bean messageSource definido");
		}

		if (this.i18nRealmName != null) {
			this.setRealmName(this.messageSource.getMessage(this.i18nRealmName, null, LocaleContextHolder.getLocale()));
		}

		super.afterPropertiesSet();
	}
	
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException{
		String content = authException.getMessage();
		
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + this.getRealmName() + "\"");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentLength(content.getBytes(Charset.forName(response.getCharacterEncoding())).length);
		response.getWriter().print(content);
		response.flushBuffer();
//		response.sendError(401, authException.getMessage());
	}
	
	

	/**
	 * Getter de la propiedad i18nRealmName.
	 * 
	 * @return Key utilizada para internacionalizar el realName del contenido
	 *         RSS.
	 */
	public String getI18nRealmName() {
		return i18nRealmName;
	}

	/**
	 * Setter de la propiedad i18nRealmName
	 * 
	 * @param i18nRealmName
	 *            Key utilizada para internacionalizar el realName del contenido
	 *            RSS.
	 */
	public void setI18nRealmName(String i18nRealmName) {
		this.i18nRealmName = i18nRealmName;
	}

}
