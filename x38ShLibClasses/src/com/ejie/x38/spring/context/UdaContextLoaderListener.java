/*
* Copyright 2023 E.J.I.E., S.A.
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
package com.ejie.x38.spring.context;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

/**
 * This class mirrors ContextLoaderListener from Spring 4 with BeanFactoryLocator mechanism enabled.
 * 
 * @since 6.0.0
 */
public class UdaContextLoaderListener extends ContextLoaderListener {

	/**
	 * Optional servlet context parameter (i.e., "{@code locatorFactorySelector}")
	 * used only when obtaining a parent context using the default implementation of
	 * {@link #loadParentContext(ServletContext servletContext)}. Specifies the
	 * 'selector' used in the
	 * {@link ContextSingletonBeanFactoryLocator#getInstance(String selector)}
	 * method call, which is used to obtain the BeanFactoryLocator instance from
	 * which the parent context is obtained.
	 * <p>
	 * The default is {@code classpath*:beanRefContext.xml}, matching the default
	 * applied for the {@link ContextSingletonBeanFactoryLocator#getInstance()}
	 * method. Supplying the "parentContextKey" parameter is sufficient in this
	 * case.
	 */
	public static final String LOCATOR_FACTORY_SELECTOR_PARAM = "locatorFactorySelector";

	/**
	 * Optional servlet context parameter (i.e., "{@code parentContextKey}") used
	 * only when obtaining a parent context using the default implementation of
	 * {@link #loadParentContext(ServletContext servletContext)}. Specifies the
	 * 'factoryKey' used in the
	 * {@link BeanFactoryLocator#useBeanFactory(String factoryKey)} method call,
	 * obtaining the parent application context from the BeanFactoryLocator
	 * instance.
	 * <p>
	 * Supplying this "parentContextKey" parameter is sufficient when relying on the
	 * default {@code classpath*:beanRefContext.xml} selector for candidate factory
	 * references.
	 */
	public static final String LOCATOR_FACTORY_KEY_PARAM = "parentContextKey";

	/**
	 * Holds BeanFactoryReference when loading parent factory via
	 * ContextSingletonBeanFactoryLocator.
	 */
	private BeanFactoryReference parentContextRef;

	/**
	 * Template method with default implementation (which may be overridden by a
	 * subclass), to load or obtain an ApplicationContext instance which will be
	 * used as the parent context of the root WebApplicationContext. If the return
	 * value from the method is null, no parent context is set.
	 * <p>
	 * The main reason to load a parent context here is to allow multiple root web
	 * application contexts to all be children of a shared EAR context, or
	 * alternately to also share the same parent context that is visible to EJBs.
	 * For pure web applications, there is usually no need to worry about having a
	 * parent context to the root web application context.
	 * <p>
	 * The default implementation uses
	 * {@link org.springframework.context.access.ContextSingletonBeanFactoryLocator},
	 * configured via {@link #LOCATOR_FACTORY_SELECTOR_PARAM} and
	 * {@link #LOCATOR_FACTORY_KEY_PARAM}, to load a parent context which will be
	 * shared by all other users of ContextsingletonBeanFactoryLocator which also
	 * use the same configuration parameters.
	 * 
	 * @param servletContext current servlet context
	 * @return the parent application context, or {@code null} if none
	 * @see org.springframework.context.access.ContextSingletonBeanFactoryLocator
	 */
	@Override
	protected ApplicationContext loadParentContext(ServletContext servletContext) {
		ApplicationContext parentContext = null;
		String locatorFactorySelector = servletContext.getInitParameter(LOCATOR_FACTORY_SELECTOR_PARAM);
		String parentContextKey = servletContext.getInitParameter(LOCATOR_FACTORY_KEY_PARAM);

		if (parentContextKey != null) {
			// locatorFactorySelector may be null, indicating the default "classpath*:beanRefContext.xml"
			BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(locatorFactorySelector);
			Log logger = LogFactory.getLog(ContextLoader.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Getting parent context definition: using parent context key of '" + parentContextKey
						+ "' with BeanFactoryLocator");
			}
			this.parentContextRef = locator.useBeanFactory(parentContextKey);
			parentContext = (ApplicationContext) this.parentContextRef.getFactory();
		}

		return parentContext;
	}

}
