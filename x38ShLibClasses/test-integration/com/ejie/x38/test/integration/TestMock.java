package com.ejie.x38.test.integration;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.UdaFilter;
import com.ejie.x38.test.integration.config.mock.X38TestingMockApplicationContext;
import com.ejie.x38.test.integration.config.mock.X38TestingMockContextLoader;

/**
 * @author Eurohelp S.L.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(loader = X38TestingMockContextLoader.class, classes = X38TestingMockApplicationContext.class)
public class TestMock {

	@Resource
	private WebApplicationContext webApplicationContext;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private UdaFilter udaFilter;

	private MockMvc mockMvc;

	@Before
	public void setUp() {

		mockMvc = MockMvcBuilders

				.webAppContextSetup(webApplicationContext)

				.addFilters(udaFilter, springSecurityFilterChain)

				.build();
	}

	@Test
	public void test() {
		try {
			mockMvc.perform(

					get("/security/test"))

					.andExpect(status().isOk());
		} catch (Exception e) {
			fail("Exception al realizar el test de conexión GET con el controller de prueba de excepciones [/security/test]");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGet() {
		try {

			mockMvc.perform(

					get("/security/get")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.cookie(new Cookie("udaMockUserName", "FOO"))

							.content(""))

					.andExpect(status().isOk())

					.andExpect(content()
							.string("{\"authorities\":[{\"authority\":\"ROLE_X38-IN-FOO\"}],\"principal\":\"FOO\"}"));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de gestión de excepciones [/security/get]");
		}

		try {
			mockMvc.perform(

					get("/security/get")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.cookie(new Cookie("udaMockUserName", "FOO"))

							.content(""))

					.andExpect(status().isOk());
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de gestión de excepciones AJAX [/security/get]");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPost() {
		try {

			mockMvc.perform(

					post("/security/post")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.cookie(new Cookie("udaMockUserName", "FOO"))

							.content(""))

					.andExpect(status().isOk());
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de gestión de excepciones [/security/post]");
		}

		try {
			mockMvc.perform(

					post("/security/post")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.cookie(new Cookie("udaMockUserName", "FOO"))

							.content(""))

					.andExpect(status().isOk());
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de gestión de excepciones AJAX [/security/post]");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPut() {
		try {

			mockMvc.perform(

					put("/security/put")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.cookie(new Cookie("udaMockUserName", "FOO"))

							.content(""))

					.andExpect(status().isOk());
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de gestión de excepciones [/security/put]");
		}

		try {
			mockMvc.perform(

					put("/security/put")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.cookie(new Cookie("udaMockUserName", "FOO"))

							.content(""))

					.andExpect(status().isOk());
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de gestión de excepciones AJAX [/security/put]");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAccessDenied() {
		try {

			mockMvc.perform(

					get("/security/security")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.cookie(new Cookie("udaMockUserName", "FOO"))

							.content(""))

					.andExpect(status().isForbidden())

					.andExpect(forwardedUrl("/accessDenied"));
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de gestión de excepciones de seguridad [/security/security]");
		}

		try {
			mockMvc.perform(

					get("/security/security")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.cookie(new Cookie("udaMockUserName", "FOO"))

							.content(""))

					.andExpect(status().isForbidden())

					.andExpect(content().string("security.ajaxAccesError"));
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de gestión de excepciones de seguridad AJAX [/security/security]");
		}
	}

	@Test
	public void testNotLogged() {
		try {

			mockMvc.perform(

					get("/security/security")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.content(""))

					.andExpect(status().isMovedTemporarily())

					.andExpect(redirectedUrl(
							"/x38TestingMockWar/mockLoginPage?mockUrl=http://localhost:80/security/security&userNames=[{\"value\":\"FOO\",\"i18nCaption\":\"Foo Foo\"},{\"value\":\"DUMMY\",\"i18nCaption\":\"Dummy Dummy\"},{\"value\":\"udaAnonymousUser\",\"i18nCaption\":\"Uda Anonymous User\"}]"));
		} catch (Exception e) {
			fail("Exception al realizar la petición SIN AUTENTICARSE [/security/security]");
		}

		try {

			mockMvc.perform(

					get("/security/security")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.content(""))

					.andExpect(status().isMovedTemporarily())

					.andExpect(redirectedUrl(
							"/x38TestingMockWar/mockLoginAjaxPage?mockUrl=http://localhost:80/security/security&userNames=[{\"value\":\"FOO\",\"i18nCaption\":\"Foo Foo\"},{\"value\":\"DUMMY\",\"i18nCaption\":\"Dummy Dummy\"},{\"value\":\"udaAnonymousUser\",\"i18nCaption\":\"Uda Anonymous User\"}]"));
		} catch (Exception e) {
			fail("Exception al realizar la petición SIN AUTENTICARSE por ajax [/security/security]");
		}
	}

}
