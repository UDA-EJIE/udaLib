package com.ejie.x38.test.integration;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.test.integration.config.wrong.X38TestingWrongApplicationContext;
import com.ejie.x38.test.integration.config.wrong.X38TestingWrongContextLoader;
import com.ejie.x38.test.integration.config.wrong.X38TestingWrongSpringJUnit4ClassRunner;

/**
 * @author Eurohelp S.L.
 */
@RunWith(X38TestingWrongSpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(loader = X38TestingWrongContextLoader.class, classes = {
		X38TestingWrongApplicationContext.class })
public class TestExceptionUdaConfig {
	@Resource
	private WebApplicationContext webApplicationContext;

	
	@Test(expected = Throwable.class)
	public void test() throws Exception {

		MockMvcBuilders

				.webAppContextSetup(webApplicationContext)

				.build();
	}
}
