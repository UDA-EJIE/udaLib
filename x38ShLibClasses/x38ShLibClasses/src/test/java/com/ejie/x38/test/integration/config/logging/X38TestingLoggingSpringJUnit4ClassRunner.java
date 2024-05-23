package com.ejie.x38.test.integration.config.logging;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Eurohelp S.L.
 */
public class X38TestingLoggingSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {

	/**
	 * @param clazz
	 * @throws InitializationError
	 */
	public X38TestingLoggingSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	@Override
	protected Object createTest() throws Exception {
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("x38/x38.properties"));

		File logFolder = new File(props.getProperty("log.path")+"/logging");
		FileUtils.deleteDirectory(logFolder);

		return super.createTest();
	}
}
