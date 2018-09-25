package com.ejie.x38.test.junit.integration;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.UdaFilter;
import com.ejie.x38.test.junit.integration.config.X38TestingApplicationContext;
import com.ejie.x38.test.junit.integration.config.X38TestingContextLoader;

/**
 * @author Eurohelp S.L.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(loader = X38TestingContextLoader.class, classes = X38TestingApplicationContext.class)
public class TestI18n {

	private static final String COOKIE_LANGUAGE = "language";

	@Resource
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private UdaFilter udaFilter;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders

				.webAppContextSetup(webApplicationContext)

				.addFilters(udaFilter, springSecurityFilterChain)

				.build();
	}

	/**
	 * @param validationMsg String
	 * @param expected      String
	 * @return ResultMatcher
	 */
	private static ResultHandler localeChange(final MockMvc mockMvc, final Locale locale) {
		return new ResultHandler() {

			@Override
			public void handle(MvcResult arg0) throws Exception {

				mockMvc.perform(

						post("/i18n/localeChange")

								.contentType(MediaType.APPLICATION_JSON)

								.accept(MediaType.ALL)

								.cookie(arg0.getResponse().getCookies())

								.content(""))

						.andExpect(status().is(200))

						.andExpect(cookie().exists(COOKIE_LANGUAGE))

						.andExpect(cookie().value(COOKIE_LANGUAGE, locale.getLanguage()));

			}
		};
	};

	@Test
	public void test() {
		try {
			mockMvc.perform(get("/i18n/test")).andExpect(status().is(200))
					.andExpect(content().string("{\"respuesta\":\"ok\"}"));
		} catch (Exception e) {
			fail("Exception al realizar el test de conexión GET con el controller de prueba de serialización [/i18n/test]");
		}
	}

	@Test
	public void localeChange() {
		Locale localeEs = new Locale("es");
		Locale localeEu = new Locale("eu");

		try {
			mockMvc.perform(get("/i18n/test?locale=es")

					.locale(localeEs))

					.andExpect(cookie().exists(COOKIE_LANGUAGE))

					.andExpect(cookie().value(COOKIE_LANGUAGE, "es"))

					.andDo(localeChange(mockMvc, localeEs));

		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de serialización en castellano [/i18n/serialize]");
		}

		try {
			mockMvc.perform(get("/i18n/test?locale=eu")

					.locale(localeEu))

					.andExpect(cookie().exists(COOKIE_LANGUAGE))

					.andExpect(cookie().value(COOKIE_LANGUAGE, "eu"))

					.andDo(localeChange(mockMvc, localeEu));

		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de serialización en euskera [/i18n/serialize]");
		}
	}

}
