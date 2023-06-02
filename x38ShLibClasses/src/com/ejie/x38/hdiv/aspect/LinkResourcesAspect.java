package com.ejie.x38.hdiv.aspect;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ejie.x38.hdiv.processor.ResponseLinkProcesor;

@Aspect
@Component
public class LinkResourcesAspect extends ResponseLinkProcesor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LinkResourcesAspect.class);

	@Around("@annotation(com.ejie.x38.hdiv.annotation.UDALink)")
	public Object processLinks(final ProceedingJoinPoint joinPoint) throws Throwable {

		try {
			fillSecurityContext();
		}catch(Exception e) {
			LOGGER.debug("Cannot load security context from session");
		}
		
		Object result = joinPoint.proceed();
		try {
			checkResponseToLinks(result, joinPoint.getTarget().getClass());
		}
		catch (Throwable e) {
			LOGGER.error("Error processing links with exception:", e);
		}
		return result;
	}
	
	//Loads SecurityContext from session when it is not loaded into SecurityContextHolder. 
	//After forward or redirect the SecurityContextHolder.getAuthentication is null
	private void fillSecurityContext() {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//No authentication loaded into ThreatLocals SecurityContext
		if(auth == null) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			SecurityContext sc = (SecurityContext) request.getSession().getAttribute(SPRING_SECURITY_CONTEXT_KEY);
			//User authentication found into session
			if(sc.getAuthentication() != null) {
				SecurityContextHolder.getContext().setAuthentication(sc.getAuthentication());
			}
		}
	}
}
