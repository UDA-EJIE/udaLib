package com.ejie.x38.rss.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import com.ejie.x38.security.Credentials;
import com.ejie.x38.security.PerimetralSecurityWrapper;

/**
 * Gestiona una petición HTTP con cabecera de autorización básica. La resolución
 * de la seguridad se realiza contra XLNets.
 *
 * La implementación de esta clase está basada en la de:
 * 
 * {@link org.springframework.security.web.authentication.www.BasicAuthenticationFilter}
 *
 * @author CAC
 */
public class RssXLNetsBasicAuthenticationFilter extends GenericFilterBean {

	// ~ Instance fields
	// ================================================================================================

	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private AuthenticationEntryPoint authenticationEntryPoint;
	private AuthenticationManager authenticationManager;
	private RememberMeServices rememberMeServices = new NullRememberMeServices();
	private boolean ignoreFailure = false;
	private String credentialsCharset = "UTF-8";
	private PerimetralSecurityWrapper perimetralSecurityWrapper;


	/**
	 * @deprecated Use constructor injection
	 */
	public RssXLNetsBasicAuthenticationFilter() {
	}

	/**
	 * {@link org.springframework.security.web.authentication.www.BasicAuthenticationFilter#BasicAuthenticationFilter(AuthenticationManager)}
	 */
	public RssXLNetsBasicAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		ignoreFailure = true;
	}

	/**
	 * {@link org.springframework.security.web.authentication.www.BasicAuthenticationFilter#BasicAuthenticationFilter(AuthenticationManager, AuthenticationEntryPoint)}
	 */
	public RssXLNetsBasicAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationManager = authenticationManager;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	// ~ Methods
	// ========================================================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() {
		Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");

		if (!isIgnoreFailure()) {
			Assert.notNull(this.authenticationEntryPoint, "An AuthenticationEntryPoint is required");
		}
	}
	/**
	 * {@link org.springframework.security.web.authentication.www.BasicAuthenticationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		final boolean debug = logger.isDebugEnabled();
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;

		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Basic ")) {
			response.addHeader("WWW-Authenticate", "Basic realm=\"" + ((BasicAuthenticationEntryPoint)authenticationEntryPoint).getRealmName() + "\"");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.flushBuffer();
			return;
		}

		try {
			String[] tokens = extractAndDecodeHeader(header, request);
			assert tokens.length == 2;

			String username = tokens[0];
			String password = tokens[1];

			if (debug) {
				logger.debug("Basic Authentication Authorization header found for user '" + username + "'");
			}

			if (authenticationIsRequired(username)) {

				HttpServletRequest wrapedRequest = RssXLNetsAutenticationHelper.createXLNetsSessionUP(username, password, request, response);
				String userConnectedUserName = this.perimetralSecurityWrapper.getUserConnectedUserName(wrapedRequest);
				if (debug) {
					logger.debug("RssXLNetsBasicAuthenticationFilter - Connected user name: " + userConnectedUserName);
				}
				
				Credentials credentials = this.perimetralSecurityWrapper.getCredentials();
				credentials.loadCredentialsData(this.perimetralSecurityWrapper, wrapedRequest);

				UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, credentials);
				authRequest.setDetails(authenticationDetailsSource.buildDetails(wrapedRequest));
				Authentication authResult = authenticationManager.authenticate(authRequest);

				if (debug) {
					logger.debug("Authentication success: " + authResult);
				}

				SecurityContextHolder.getContext().setAuthentication(authResult);

				rememberMeServices.loginSuccess(wrapedRequest, response, authResult);

				onSuccessfulAuthentication(wrapedRequest, response, authResult);

				chain.doFilter(wrapedRequest, response);
			}

		} catch (AuthenticationException failed) {
			SecurityContextHolder.clearContext();

			if (debug) {
				logger.debug("Authentication request for failed: " + failed);
			}

			rememberMeServices.loginFail(request, response);

			onUnsuccessfulAuthentication(request, response, failed);

			if (ignoreFailure) {
				chain.doFilter(request, response);
			} else {
				authenticationEntryPoint.commence(request, response, failed);
			}

			return;
		}

	}

	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws IOException {

		byte[] base64Token = header.substring(6).getBytes("UTF-8");
		byte[] decoded;
		try {
			decoded = Base64.decode(base64Token);
		} catch (IllegalArgumentException e) {
			throw new BadCredentialsException("Failed to decode basic authentication token");
		}

		String token = new String(decoded, getCredentialsCharset(request));

		int delim = token.indexOf(":");

		if (delim == -1) {
			throw new BadCredentialsException("Invalid basic authentication token");
		}
		return new String[] { token.substring(0, delim), token.substring(delim + 1) };
	}

	private boolean authenticationIsRequired(String username) {
		// Only reauthenticate if username doesn't match SecurityContextHolder
		// and user isn't authenticated
		// (see SEC-53)
		Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

		if (existingAuth == null || !existingAuth.isAuthenticated()) {
			return true;
		}

		// Limit username comparison to providers which use usernames (ie
		// UsernamePasswordAuthenticationToken)
		// (see SEC-348)

		if (existingAuth instanceof UsernamePasswordAuthenticationToken && !existingAuth.getName().equals(username)) {
			return true;
		}

		// Handle unusual condition where an AnonymousAuthenticationToken is
		// already present
		// This shouldn't happen very often, as BasicProcessingFitler is meant
		// to be earlier in the filter
		// chain than AnonymousAuthenticationFilter. Nevertheless, presence of
		// both an AnonymousAuthenticationToken
		// together with a BASIC authentication request header should indicate
		// reauthentication using the
		// BASIC protocol is desirable. This behaviour is also consistent with
		// that provided by form and digest,
		// both of which force re-authentication if the respective header is
		// detected (and in doing so replace
		// any existing AnonymousAuthenticationToken). See SEC-610.
		if (existingAuth instanceof AnonymousAuthenticationToken) {
			return true;
		}

		return false;
	}

	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
	}

	protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
	}

	protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
		return authenticationEntryPoint;
	}

	/**
	 * @deprecated Use constructor injection
	 */
	@Deprecated
	public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	protected AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	/**
	 * @deprecated Use constructor injection
	 */
	@Deprecated
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	protected boolean isIgnoreFailure() {
		return ignoreFailure;
	}

	/**
	 *
	 * @deprecated Use the constructor which takes a single
	 *             AuthenticationManager parameter
	 */
	@Deprecated
	public void setIgnoreFailure(boolean ignoreFailure) {
		this.ignoreFailure = ignoreFailure;
	}

	public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
		Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
		this.authenticationDetailsSource = authenticationDetailsSource;
	}

	public void setRememberMeServices(RememberMeServices rememberMeServices) {
		Assert.notNull(rememberMeServices, "rememberMeServices cannot be null");
		this.rememberMeServices = rememberMeServices;
	}

	public void setCredentialsCharset(String credentialsCharset) {
		Assert.hasText(credentialsCharset, "credentialsCharset cannot be null or empty");
		this.credentialsCharset = credentialsCharset;
	}

	protected String getCredentialsCharset(HttpServletRequest httpRequest) {
		return credentialsCharset;
	}

	public PerimetralSecurityWrapper getPerimetralSecurityWrapper() {
		return perimetralSecurityWrapper;
	}

	public void setPerimetralSecurityWrapper(PerimetralSecurityWrapper perimetralSecurityWrapper) {
		this.perimetralSecurityWrapper = perimetralSecurityWrapper;
	}

}
