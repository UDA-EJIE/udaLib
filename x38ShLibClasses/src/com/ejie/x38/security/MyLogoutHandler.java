package com.ejie.x38.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

import com.ejie.x38.security.PerimetralSecurityWrapper;

public class MyLogoutHandler implements LogoutHandler {
	private boolean invalidateHttpSession;
	private boolean invalidateXlnetSession;
	private PerimetralSecurityWrapper perimetralSecurityWrapper;
	static Logger logger = Logger.getLogger(MyLogoutHandler.class);

	public PerimetralSecurityWrapper getPerimetralSecurityWrapper() {
		return perimetralSecurityWrapper;
	}

	public void setPerimetralSecurityWrapper(
			PerimetralSecurityWrapper perimetralSecurityWrapper) {
		this.perimetralSecurityWrapper = perimetralSecurityWrapper;
	}

	@Override
	public void logout(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) {

		//Destroy XLNET session
		if(invalidateXlnetSession){
			Assert.notNull(request, "HttpServletRequest required");			
			getPerimetralSecurityWrapper().logout(request);
			logger.info("XLNET " +getPerimetralSecurityWrapper().getUserConnectedUidSession(request)+ " Session destroyed!");
		}

		//Invalidate HTTP session
		if (invalidateHttpSession) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				logger.info("Session " +session.getId()+ " invalidated!");
				session.invalidate();				
			}
		}

		//Clear Spring Security Context
		SecurityContextHolder.clearContext();
		logger.log(Level.INFO, "SecurityContextHolder cleared!");
	}

	public boolean isInvalidateHttpSession() {
		return invalidateHttpSession;
	}

	public void setInvalidateHttpSession(boolean invalidateHttpSession) {
		this.invalidateHttpSession = invalidateHttpSession;
	}

	public boolean isInvalidateXlnetSession() {
		return invalidateXlnetSession;
	}

	public void setInvalidateXlnetSession(boolean invalidateXlnetSession) {
		this.invalidateXlnetSession = invalidateXlnetSession;
	}
}