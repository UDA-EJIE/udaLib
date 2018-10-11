package com.ejie.x38.test.junit.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;

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
import org.springframework.test.web.servlet.ResultMatcher;
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
public class TestException {

	@Resource
	private WebApplicationContext webApplicationContext;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private UdaFilter udaFilter;

	private MockMvc mockMvc;

	private Locale localeEs;

	private String jspError;

	@Before
	public void setUp() {

		mockMvc = MockMvcBuilders

				.webAppContextSetup(webApplicationContext)

				.addFilters(udaFilter, springSecurityFilterChain)

				.build();

		localeEs = new Locale("es");

		jspError = "/views/error.jsp";
	}

//	private static ResultMatcher redirectedUrlPattern(final String expectedUrlPattern, final String exceptionMsg) {
//		return new ResultMatcher() {
//			public void match(MvcResult result) {
//
//				String strPattern = "\\A" + expectedUrlPattern + ".*" + exceptionMsg + ".*" + "\\z";
//				Pattern pattern = Pattern.compile(strPattern);
//				if (result.getResponse() != null && StringUtils.isNotEmpty(result.getResponse().getRedirectedUrl())) {
//					assertTrue(pattern.matcher(result.getResponse().getRedirectedUrl()).find());
//				} else {
//					fail("No se ha detectado redirección que contenga: '" + strPattern + "'");
//				}
//			}
//		};
//	}

	private static ResultMatcher errorAjax(final String exceptionName, final String exceptionMessage) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				assertNotNull("Debe resolverse una excepción", result.getResolvedException());
				assertEquals("Debe coincidir el tipo de la excepción", exceptionName,
						result.getResolvedException().getClass().getName());
				assertEquals("Debe coincidir el mensaje de la excepción", exceptionMessage,
						result.getResolvedException().getMessage());

				assertNotNull("Debe resolverse la respuesta", result.getResponse());

				if (result.getResponse() != null) {
					try {
						assertEquals("El mensaje de la excepción será el contenido de la respuesta", exceptionMessage,
								result.getResponse().getContentAsString());
					} catch (UnsupportedEncodingException e) {
						fail("Error procesando el contenido de la response");
					}
				}
			}
		};
	}

	private static ResultMatcher errorModel(final String exceptionName, final String exceptionMessage) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				assertNotNull("Debe resolverse una excepción", result.getResolvedException());
				assertEquals("Debe coincidir el tipo de la excepción", exceptionName,
						result.getResolvedException().getClass().getName());
				assertEquals("Debe coincidir el mensaje de la excepción", exceptionMessage,
						result.getResolvedException().getMessage());

				assertNotNull("El modelo resultante no debe ser nulo", result.getModelAndView());

				if (result.getModelAndView() != null && result.getModelAndView().getModel() != null) {
					Map<String, Object> model = result.getModelAndView().getModel();

					assertEquals("El modelo trae propiedad 'exception_name'", exceptionName,
							model.get("exception_name"));
					assertEquals("El modelo trae propiedad 'exception_message'", exceptionMessage,
							model.get("exception_message"));
				}
			}
		};
	}

	Principal principal = new Principal() {
		@Override
		public String getName() {
			return "TEST_PRINCIPAL";
		}
	};

	@Test
	public void test() {
		try {
			mockMvc.perform(

					get("/exception/test"))

					.andExpect(status().is(200))

					.andExpect(content().string("{\"respuesta\":\"ok\"}"));
		} catch (Exception e) {
			fail("Exception al realizar el test de conexión GET con el controller de prueba de excepciones [/exception/test]");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGet() {
		try {

			mockMvc.perform(

					get("/exception/get")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(""))

					.andExpect(status().is(200))

					.andExpect(forwardedUrl(jspError))

					.andExpect(errorModel("java.lang.Exception", "Excepción lanzada en el GET"));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de gestión de excepciones [/exception/get]");
		}

		try {
			mockMvc.perform(

					get("/exception/get")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(""))

					.andExpect(status().is(406))

					.andExpect(errorAjax("java.lang.Exception", "Excepción lanzada en el GET"));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de gestión de excepciones AJAX [/exception/get]");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPost() {
		try {

			mockMvc.perform(

					post("/exception/post")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(""))

					.andExpect(status().is(200))

					.andExpect(errorModel("java.lang.Exception", "Excepción lanzada en el POST"));
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de gestión de excepciones [/exception/post]");
		}

		try {
			mockMvc.perform(

					post("/exception/post")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(""))

					.andExpect(status().is(406))

					.andExpect(errorAjax("java.lang.Exception", "Excepción lanzada en el POST"));
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de gestión de excepciones AJAX [/exception/post]");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPut() {
		try {

			mockMvc.perform(

					put("/exception/put")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(""))

					.andExpect(status().is(200))

					.andExpect(errorModel("java.lang.Exception", "Excepción lanzada en el PUT"));
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de gestión de excepciones [/exception/put]");
		}

		try {
			mockMvc.perform(

					put("/exception/put")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(""))

					.andExpect(status().is(406))

					.andExpect(errorAjax("java.lang.Exception", "Excepción lanzada en el PUT"));
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de gestión de excepciones AJAX [/exception/put]");
		}
	}

}
