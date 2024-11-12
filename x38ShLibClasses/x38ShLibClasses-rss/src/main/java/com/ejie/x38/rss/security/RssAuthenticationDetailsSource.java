package com.ejie.x38.rss.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/**
 * Clase de infraestructura para implementar el proceso de autenticación
 * del acceso al contenido RSS.
 * 
 * @author UDA
 *
 */
public class RssAuthenticationDetailsSource extends
    WebAuthenticationDetailsSource {

  @Override
  public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
    return new MyAuthenticationDetails(context);
  }

  @SuppressWarnings("serial")
  class MyAuthenticationDetails extends WebAuthenticationDetails {

    private final String referer;

    public MyAuthenticationDetails(HttpServletRequest request) {
      super(request);
      this.referer = request.getHeader("Referer");
    }

    public String getReferer() {
      return referer;
    }
  }

}