package com.ejie.x38.test.integration.config;

import javax.servlet.RequestDispatcher;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * This class is here temporarily until the TestContext framework provides
 * support for WebApplicationContext yet:
 *
 * https://jira.springsource.org/browse/SPR-5243
 *
 * <p>
 * After that this class will no longer be needed. It's provided here as an
 * example and to serve as a temporary solution.
 */
public class X38TestingWebContextLoader extends AbstractContextLoader {
	protected final MockServletContext servletContext;

	public X38TestingWebContextLoader(String warRootDir, boolean isClasspathRelative) {
		ResourceLoader resourceLoader = isClasspathRelative ? new DefaultResourceLoader()
				: new FileSystemResourceLoader();
		this.servletContext = initServletContext(warRootDir, resourceLoader);
	}

	private MockServletContext initServletContext(String warRootDir, ResourceLoader resourceLoader) {
		MockServletContext mockServletContext = new MockServletContext(warRootDir, resourceLoader) {
			// Required for DefaultServletHttpRequestHandler...
			public RequestDispatcher getNamedDispatcher(String path) {
				return (path.equals("default")) ? new MockRequestDispatcher(path) : super.getNamedDispatcher(path);
			}
		};
		mockServletContext.setInitParameter("webAppName", "x38");
		mockServletContext.setContextPath("/x38TestingWar");
		return mockServletContext;
	}

	public ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
		GenericWebApplicationContext context = new GenericWebApplicationContext();
		context.getEnvironment().setActiveProfiles(mergedConfig.getActiveProfiles());
		prepareContext(context);
		loadBeanDefinitions(context, mergedConfig);
		return context;
	}

	public ApplicationContext loadContext(String... locations) throws Exception {
		throw new UnsupportedOperationException();
	}

	protected void prepareContext(GenericWebApplicationContext context) {
		this.servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
		context.setServletContext(this.servletContext);
	}

	protected void loadBeanDefinitions(GenericWebApplicationContext context, String[] locations) {
		new XmlBeanDefinitionReader(context).loadBeanDefinitions(locations);
		AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
		context.refresh();
		context.registerShutdownHook();
	}

	protected void loadBeanDefinitions(GenericWebApplicationContext context, MergedContextConfiguration mergedConfig) {
		new AnnotatedBeanDefinitionReader(context).register(mergedConfig.getClasses());
		loadBeanDefinitions(context, mergedConfig.getLocations());
	}

	@Override
	protected String getResourceSuffix() {
		return "-context.xml";
	}
}
