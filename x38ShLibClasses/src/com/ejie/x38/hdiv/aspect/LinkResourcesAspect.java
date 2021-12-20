package com.ejie.x38.hdiv.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hdiv.services.LinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ejie.x38.hdiv.processor.ResponseLinkProcesor;

@Aspect
@Component
public class LinkResourcesAspect extends ResponseLinkProcesor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LinkResourcesAspect.class);

	@Autowired
	private LinkProvider linkProvider;

	@Around("@annotation(com.ejie.x38.hdiv.annotation.UDALink)")
	public Object processLinks(final ProceedingJoinPoint joinPoint) throws Throwable {

		Object result = joinPoint.proceed();
		try {
			checkResponseToLinks(result, joinPoint.getTarget().getClass(), linkProvider);
		}
		catch (Throwable e) {
			LOGGER.error("Error processing links with exception:", e);
		}
		return result;

	}

	@Override
	protected void onSecureIdentifiableFound(Object object) {
	}

	@Override
	protected void onSecureIdContainerFound(Object object) {
	}

}
