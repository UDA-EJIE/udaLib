package com.ejie.x38.test.integration.config.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.ejie.x38.control.exception.MvcExceptionResolverConfig;

/**
 * @author Eurohelp S.L.
 */
@Configuration
@EnableWebMvc
@ImportResource({ "classpath:x38TestingMockWar/testMock-config.xml" })
@ComponentScan(basePackages = "com.ejie.x38.test.integration.control")
public class X38TestingMockApplicationContext extends MvcExceptionResolverConfig {

	@Bean
	public HandlerExceptionResolver handlerExceptionResolver() {
		return super.handlerExceptionResolver();
	}
}
