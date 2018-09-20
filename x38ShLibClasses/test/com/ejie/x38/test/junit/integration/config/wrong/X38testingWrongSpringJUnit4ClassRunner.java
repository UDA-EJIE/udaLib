package com.ejie.x38.test.junit.integration.config.wrong;

import static org.junit.Assert.fail;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Eurohelp S.L.
 */
public class X38testingWrongSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {

	/**
	 * @param clazz
	 * @throws InitializationError
	 */
	public X38testingWrongSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	@Override
	protected Object createTest() throws Exception {
		try {
			super.createTest();
			fail("No hubo excepción al inicializar el contexto de spring con la configuración incorrecta de UDA");
			return new Object();
		} catch (Exception e) {
			if (e.getCause().getMessage().indexOf(
					"Falta definir el bean 'requestMappingHandlerAdapter' de tipo 'RequestMappingHandlerAdapter'") >= 0) {
				System.exit(0);
			}
			if (e.getCause().getMessage().indexOf(
					"Falta definir el bean 'messageSource' de tipo 'ReloadableResourceBundleMessageSource'") >= 0) {
				System.exit(0);
			}
			if (e.getCause().getMessage().indexOf(
					"Falta definir el bean 'validationManager' de tipo 'com.ejie.x38.validation.ValidationManager'") >= 0) {
				System.exit(0);
			}
			fail("No hubo excepción al inicializar el contexto de spring con la configuración incorrecta de UDA, pero no se detectaron las tracas adecuadas del validador");
			return new Object();
		}
	}
}
