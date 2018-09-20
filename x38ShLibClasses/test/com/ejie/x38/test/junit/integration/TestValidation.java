package com.ejie.x38.test.junit.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	 * @param object
	 * @return
	 * @throws JsonProcessingException
	 */
	private String serialize(Object object) throws JsonProcessingException {
		return this.objectMapper.writeValueAsString(object);
	}

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

	@Test
	public void test() {
		try {
			mockMvc.perform(get("/validation/test")).andExpect(status().is(200))
					.andExpect(content().string("{\"respuesta\":\"ok\"}"));
		} catch (Exception e) {
			fail("Exception al realizar el test de conexión GET con el controller de prueba de validaciones [/validation/test]");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGet() {
		Locale localeEs = new Locale("es");
		Locale localeEu = new Locale("eu");

		Alumno alumno = new Alumno();

		String jsonReq = "";
		try {
			jsonReq = this.serialize(alumno);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a emplear en la prueba");
		}

		try {

			mockMvc.perform(

					get("/validation/get")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEs)

							.content(jsonReq))

					.andExpect(errorModel("org.springframework.web.bind.MethodArgumentNotValidException",
							"Validation failed"));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de validaciones en castellano [/validation/get]");
		}

		try {
			mockMvc.perform(

					get("/validation/get")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.locale(localeEu)

							.content(jsonReq))

					.andExpect(errorModel("org.springframework.web.bind.MethodArgumentNotValidException",
							"Validation failed"));
		} catch (Exception e) {
			fail("Exception al realizar la petición GET con el controller de prueba de validaciones en euskera [/validation/get]");
		}
	}

}
