package com.ejie.x38.test.integration;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.UdaFilter;
import com.ejie.x38.test.integration.config.portal.X38TestingPortalApplicationContext;
import com.ejie.x38.test.integration.config.portal.X38TestingPortalContextLoader;

/**
 * @author Eurohelp S.L.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(loader = X38TestingPortalContextLoader.class, classes = X38TestingPortalApplicationContext.class)
public class TestI18nPortal {

	private static final String PORTAL_COOKIE = "r01euskadiCookie";

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
	 * @param exceptionName    String
	 * @param exceptionMessage String
	 * @return ResultMatcher
	 */
	private static ResultMatcher debugRM() {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				System.out.println(result);
			}
		};
	}

	@Test
	public void test() {
		try {
			mockMvc.perform(get("/i18n/test")).andExpect(status().is(200))
					.andExpect(debugRM());
//					.andExpect(content().string("{\"respuesta\":\"ok\"}"));
		} catch (Exception e) {
			fail("Exception al realizar el test de conexión GET con el controller de prueba de serialización [/i18n/test]");
		}
	}

	@Test
	public void portalCookieChange() {

		try {
			final Cookie r61_es = new Cookie(PORTAL_COOKIE, "r61_es");
			r61_es.setSecure(true);

			mockMvc.perform(get("/i18n/test")

					.cookie(r61_es))

					.andExpect(cookie().exists(COOKIE_LANGUAGE))

					.andExpect(cookie().value(COOKIE_LANGUAGE, "es"));

		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de serialización en castellano [/i18n/serialize]");
		}

		try {
			final Cookie r61_eu = new Cookie(PORTAL_COOKIE, "r61_eu");
			r61_eu.setSecure(true);

			mockMvc.perform(get("/i18n/test")

					.cookie(r61_eu))

					.andExpect(cookie().exists(COOKIE_LANGUAGE))

					.andExpect(cookie().value(COOKIE_LANGUAGE, "eu"));

		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de serialización en euskera [/i18n/serialize]");
		}
	}

}
