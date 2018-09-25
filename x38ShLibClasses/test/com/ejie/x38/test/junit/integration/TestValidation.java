package com.ejie.x38.test.junit.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
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
import com.ejie.x38.serialization.UdaMappingJackson2HttpMessageConverter;
import com.ejie.x38.test.common.model.Alumno;
import com.ejie.x38.test.common.model.Coche;
import com.ejie.x38.test.junit.integration.config.X38TestingApplicationContext;
import com.ejie.x38.test.junit.integration.config.X38TestingContextLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Eurohelp S.L.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(loader = X38TestingContextLoader.class, classes = X38TestingApplicationContext.class)
public class TestValidation {

	@Resource
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private UdaFilter udaFilter;

	@Autowired
	private UdaMappingJackson2HttpMessageConverter udaMappingJackson2HttpMessageConverter;

	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders

				.webAppContextSetup(webApplicationContext)

				.addFilters(udaFilter, springSecurityFilterChain)

				.build();

		this.objectMapper = udaMappingJackson2HttpMessageConverter.getObjectMapper();
	}

	/**
	 * @param object Object
	 * @return String
	 * @throws JsonProcessingException
	 */
	private String serialize(Object object) throws JsonProcessingException {
		return this.objectMapper.writeValueAsString(object);
	}

	/**
	 * @param exceptionName    String
	 * @param exceptionMessage String
	 * @return ResultMatcher
	 */
	private static ResultMatcher errorModel(final String exceptionName, final String exceptionMessage) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				assertNotNull("Debe resolverse una excepción", result.getResolvedException());
				assertEquals("Debe coincidir el tipo de la excepción", exceptionName,
						result.getResolvedException().getClass().getName());
				assertTrue("Debe coincidir el mensaje de la excepción",
						result.getResolvedException().getMessage().indexOf(exceptionMessage) >= 0);

				assertNotNull("El modelo resultante no debe ser nulo", result.getModelAndView());

				if (result.getModelAndView() != null && result.getModelAndView().getModel() != null) {
					Map<String, Object> model = result.getModelAndView().getModel();

					assertEquals("El modelo trae propiedad 'exception_name'", exceptionName,
							model.get("exception_name"));
					assertTrue("El modelo trae propiedad 'exception_message' coincidente con el mensaje de excepción",
							model.get("exception_message").toString().indexOf(exceptionMessage) >= 0);
				}
			}
		};
	}

	/**
	 * @param exceptionName    String
	 * @param exceptionMessage Str Stringing
	 * @param responseMessage
	 * @return ResultMatcher
	 */
	private static ResultMatcher errorAjax(final String exceptionName, final String exceptionMessage,
			final String responseMessage) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				assertNotNull("Debe resolverse una excepción", result.getResolvedException());
				assertEquals("Debe coincidir el tipo de la excepción", exceptionName,
						result.getResolvedException().getClass().getName());
				assertTrue("Debe coincidir el mensaje de la excepción",
						result.getResolvedException().getMessage().indexOf(exceptionMessage) >= 0);

				assertNotNull("Debe resolverse la respuesta", result.getResponse());

				if (result.getResponse() != null) {
					try {
						assertEquals("El mensaje de la excepción será el contenido de la respuesta", responseMessage,
								result.getResponse().getContentAsString());
					} catch (UnsupportedEncodingException e) {
						fail("Error procesando el contenido de la response");
					}
				}
			}
		};
	}

	/**
	 * @param errorMessage String
	 * @return ResultMatcher
	 */
	private static ResultMatcher errorMessage(final String errorMessage) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				assertNotNull("Debe resolverse la respuesta", result.getResponse());

				if (result.getResponse() != null) {
					assertNotNull("Debe existir mensaje de error", result.getResponse().getErrorMessage());
					assertEquals("El mensaje de error debe coincidir", errorMessage,
							result.getResponse().getErrorMessage());
				}
			}
		};
	}

	@Test
	public void test() {
		try {
			mockMvc.perform(get("/validation/test")).andExpect(status().is(200))
					.andExpect(content().string("{\"respuesta\":\"ok\"}"));
		} catch (Exception e) {
			fail("Exception al realizar el test de conexión GET con el controller de prueba de validaciones [/validation/test]");
		}
	}

	/****************************************************************************
	 * INICIO - VALIDACIONES HIBERNATE
	 ****************************************************************************/
	@Test
	public void testHibernateValidationGet() {
		Locale localeEs = new Locale("es");

		Alumno alumno = new Alumno();

		String jsonReq = "";
		try {
			jsonReq = this.serialize(alumno);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a emplear en la prueba");
		}

		String jsonValidationErrors = "{\"rupErrorFields\":{\"nombre\":[\"validacion.required\"],\"direccion\":[\"validacion.required\"],\"usuario\":[\"validacion.required\"],\"apellido1\":[\"validacion.required\"]}}";

		try {

			mockMvc.perform(

					get("/validation/hibernate/get")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(jsonValidationErrors));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de validaciones de springHibernate [/validation/hibernate/get]");
		}

		try {
			mockMvc.perform(

					get("/validation/hibernate/get")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(jsonValidationErrors));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de validaciones de hibernate por AJAX [/validation/hibernate/get]");
		}
	}

	@Test
	public void testHibernateValidationPost() {
		Locale localeEs = new Locale("es");

		Alumno alumno = new Alumno();

		String jsonReq = "";
		try {
			jsonReq = this.serialize(alumno);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a emplear en la prueba");
		}

		String jsonValidationErrors = "{\"rupErrorFields\":{\"nombre\":[\"validacion.required\"],\"direccion\":[\"validacion.required\"],\"usuario\":[\"validacion.required\"],\"apellido1\":[\"validacion.required\"]}}";

		try {

			mockMvc.perform(

					post("/validation/hibernate/post")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(jsonValidationErrors));
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de validaciones de springHibernate [/validation/hibernate/post]");
		}

		try {
			mockMvc.perform(

					post("/validation/hibernate/post")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(jsonValidationErrors));
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de validaciones de hibernate por AJAX [/validation/hibernate/post]");
		}
	}

	@Test
	public void testHibernateValidationPut() {
		Locale localeEs = new Locale("es");

		Alumno alumno = new Alumno();

		String jsonReq = "";
		try {
			jsonReq = this.serialize(alumno);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a emplear en la prueba");
		}

		String jsonValidationErrors = "{\"rupErrorFields\":{\"nombre\":[\"validacion.required\"],\"direccion\":[\"validacion.required\"],\"usuario\":[\"validacion.required\"],\"apellido1\":[\"validacion.required\"]}}";

		try {

			mockMvc.perform(

					put("/validation/hibernate/put")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(jsonValidationErrors));
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de validaciones de springHibernate [/validation/hibernate/put]");
		}

		try {
			mockMvc.perform(

					put("/validation/hibernate/put")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(jsonValidationErrors));
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de validaciones de hibernate por AJAX [/validation/hibernate/put]");
		}
	}

	/****************************************************************************
	 * FIN - VALIDACIONES HIBERNATE
	 ****************************************************************************/

	/****************************************************************************
	 * INICIO - VALIDACIONES SPRING & HIBERNATE
	 ****************************************************************************/
	@Test
	public void testSpringHibernateValidationGet() {
		Locale localeEs = new Locale("es");

		Alumno alumno = new Alumno();

		String jsonReq = "";
		try {
			jsonReq = this.serialize(alumno);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a emplear en la prueba");
		}

		try {

			mockMvc.perform(

					get("/validation/springHibernate/get")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(errorModel("org.springframework.web.bind.MethodArgumentNotValidException",
							"Validation failed"));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de validaciones de springHibernate [/validation/springHibernate/get]");
		}

		try {
			mockMvc.perform(

					get("/validation/springHibernate/get")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(errorAjax("org.springframework.web.bind.MethodArgumentNotValidException",
							"Validation failed",
							"{\"rupErrorFields\":{\"nombre\":[\"validacion.required\"],\"usuario\":[\"validacion.required\"]}}"));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de validaciones de springHibernate por AJAX [/validation/springHibernate/get]");
		}
	}

	@Test
	public void testSpringHibernateValidationPost() {
		Locale localeEs = new Locale("es");

		Alumno alumno = new Alumno();

		String jsonReq = "";
		try {
			jsonReq = this.serialize(alumno);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a emplear en la prueba");
		}

		try {

			mockMvc.perform(

					post("/validation/springHibernate/post")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(errorModel("org.springframework.web.bind.MethodArgumentNotValidException",
							"Validation failed"));
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de validaciones de springHibernate [/validation/springHibernate/post]");
		}

		try {
			mockMvc.perform(

					post("/validation/springHibernate/post")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(errorAjax("org.springframework.web.bind.MethodArgumentNotValidException",
							"Validation failed",
							"{\"rupErrorFields\":{\"nombre\":[\"validacion.required\"],\"usuario\":[\"validacion.required\"]}}"));
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de validaciones de springHibernate por AJAX [/validation/springHibernate/post]");
		}
	}

	@Test
	public void testSpringHibernateValidationPut() {
		Locale localeEs = new Locale("es");

		Alumno alumno = new Alumno();

		String jsonReq = "";
		try {
			jsonReq = this.serialize(alumno);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a emplear en la prueba");
		}

		try {

			mockMvc.perform(

					put("/validation/springHibernate/put")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(errorModel("org.springframework.web.bind.MethodArgumentNotValidException",
							"Validation failed"));
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de validaciones de springHibernate [/validation/springHibernate/put]");
		}

		try {
			mockMvc.perform(

					put("/validation/springHibernate/put")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(errorAjax("org.springframework.web.bind.MethodArgumentNotValidException",
							"Validation failed",
							"{\"rupErrorFields\":{\"nombre\":[\"validacion.required\"],\"usuario\":[\"validacion.required\"]}}"));
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de validaciones de springHibernate por AJAX [/validation/springHibernate/put]");
		}
	}

	/****************************************************************************
	 * FIN - VALIDACIONES SPRING & HIBERNATE
	 ****************************************************************************/

	/****************************************************************************
	 * INICIO - VALIDACIONES PROPIAS
	 ****************************************************************************/
	@Test
	public void testCustomValidationGet() {
		Locale localeEs = new Locale("es");

		Coche coche = new Coche();

		String jsonReq = "";
		try {
			jsonReq = this.serialize(coche);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a emplear en la prueba");
		}

		try {

			mockMvc.perform(

					get("/validation/custom/get")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(
							"{\"rupErrorFields\":{\"modelo\":[\"base.rup_validate.messages.required\"]}}"));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de validaciones personalizadas [/validation/custom/get]");
		}

		try {
			mockMvc.perform(

					get("/validation/custom/get")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(
							"{\"rupErrorFields\":{\"modelo\":[\"base.rup_validate.messages.required\"]}}"));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de validaciones personalizadas por AJAX [/validation/custom/get]");
		}
	}

	@Test
	public void testCustomValidationPost() {
		Locale localeEs = new Locale("es");

		Coche coche = new Coche();

		String jsonReq = "";
		try {
			jsonReq = this.serialize(coche);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a emplear en la prueba");
		}

		try {

			mockMvc.perform(

					post("/validation/custom/post")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(
							"{\"rupErrorFields\":{\"modelo\":[\"base.rup_validate.messages.required\"]}}"));
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de validaciones personalizadas [/validation/custom/post]");
		}

		try {
			mockMvc.perform(

					post("/validation/custom/post")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(
							"{\"rupErrorFields\":{\"modelo\":[\"base.rup_validate.messages.required\"]}}"));
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de validaciones personalizadas por AJAX [/validation/custom/post]");
		}
	}

	@Test
	public void testCustomValidationPut() {
		Locale localeEs = new Locale("es");

		Coche coche = new Coche();

		String jsonReq = "";
		try {
			jsonReq = this.serialize(coche);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a emplear en la prueba");
		}

		try {

			mockMvc.perform(

					put("/validation/custom/put")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(
							"{\"rupErrorFields\":{\"modelo\":[\"base.rup_validate.messages.required\"]}}"));
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de validaciones personalizadas [/validation/custom/put]");
		}

		try {
			mockMvc.perform(

					put("/validation/custom/put")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(status().isNotAcceptable())

					.andExpect(errorMessage(
							"{\"rupErrorFields\":{\"modelo\":[\"base.rup_validate.messages.required\"]}}"));
		} catch (Exception e) {
			fail("Exception al realizar la petición PUT con el controller de prueba de validaciones personalizadas por AJAX [/validation/custom/put]");
		}
	}

	/****************************************************************************
	 * FIN - VALIDACIONES PROPIAS
	 ****************************************************************************/

}
