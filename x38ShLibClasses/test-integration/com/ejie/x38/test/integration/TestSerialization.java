package com.ejie.x38.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.UdaFilter;
import com.ejie.x38.serialization.ThreadSafeCache;
import com.ejie.x38.serialization.UdaMappingJackson2HttpMessageConverter;
import com.ejie.x38.test.integration.config.X38TestingApplicationContext;
import com.ejie.x38.test.integration.config.X38TestingContextLoader;
import com.ejie.x38.test.model.Coche;
import com.ejie.x38.test.model.Empleado;
import com.ejie.x38.test.model.Marca;
import com.ejie.x38.test.model.NoraPais;
import com.ejie.x38.util.DateTimeManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Eurohelp S.L.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(loader = X38TestingContextLoader.class, classes = X38TestingApplicationContext.class)
public class TestSerialization {

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
	 * @param validationMsg String
	 * @param expected      String
	 * @return ResultMatcher
	 */
	private static ResultMatcher contentMatch(final String validationMsg, final String expected) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				assertNotNull("Debe resolverse la respuesta", result.getResponse());

				if (result.getResponse() != null) {
					try {
						assertEquals(validationMsg, expected, result.getResponse().getContentAsString());
					} catch (UnsupportedEncodingException e) {
						fail("Error procesando el contenido de la response");
					}
				}
			}
		};
	}

	/**
	 * @param validationMsg String
	 * @param expected      String
	 * @return ResultMatcher
	 */
	private static ResultHandler postSerialize(final MockMvc mockMvc, final Locale localeEs, final String jsonResEs,
			final String jsonReqEs) {
		return new ResultHandler() {

			@Override
			public void handle(MvcResult arg0) throws Exception {
				customSerializerParams();

				mockMvc.perform(

						post("/serialization/serialize")

								.contentType(MediaType.APPLICATION_JSON)

								.accept(MediaType.ALL)

								.locale(localeEs)
								
								.cookie(arg0.getResponse().getCookies())

								.content(jsonReqEs))

						.andExpect(status().is(200))

						.andExpect(contentMatch("La respuesta no es correcta", jsonResEs));

			}
		};
	};

	/**
	 * @param object
	 * @return
	 * @throws JsonProcessingException
	 */
	private String serialize(Object object) throws JsonProcessingException {
		return this.objectMapper.writeValueAsString(object);
	}

	@Test
	public void test() {
		try {
			mockMvc.perform(get("/serialization/test")).andExpect(status().is(200))
					.andExpect(content().string("{\"respuesta\":\"ok\"}"));
		} catch (Exception e) {
			fail("Exception al realizar el test de conexión GET con el controller de prueba de serialización [/serialization/test]");
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void serialize() {
		Locale localeEs = new Locale("es");
		Locale localeEu = new Locale("eu");

		Empleado eneko = new Empleado("Eneko");
		Empleado laura = new Empleado("Laura");
		Marca foo = new Marca("Foo");
		foo.setEmpleados(Arrays.asList(eneko, laura));
		foo.setPais(new NoraPais("108", "España"));
		Coche crx5 = new Coche("CRX-5", foo);
		crx5.setCoste(BigDecimal.valueOf(9123000.0123456789));
		crx5.setPrecio(BigDecimal.valueOf(9123230.3456789));

		String strFechaConstruccion = "02/06/1995 13:45:11";
		String strTiempoConstruccion = "12:30:22";
		String strFecNacEneko = "01/01/1981";
		String strFecNacLaura = "12/12/1992";
		SimpleDateFormat sdtf = DateTimeManager.getTimestampFormat(localeEs);
		SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat sdf = DateTimeManager.getDateTimeFormat(localeEs);

		try {
			crx5.setFechaConstruccion(new Timestamp(sdtf.parse(strFechaConstruccion).getTime()));
			crx5.setTiempoConstruccion(new Timestamp(stf.parse(strTiempoConstruccion).getTime()));
			eneko.setFechaNacimiento(new Date(sdf.parse(strFecNacEneko).getTime()));
			laura.setFechaNacimiento(new Date(sdf.parse(strFecNacLaura).getTime()));
		} catch (ParseException e) {
			fail("ParseException inicializando el objeto para el caso de prueba");
		}

		String jsonResEs = "";
		String jsonResEu = "";
		try {
			LocaleContextHolder.setLocale(localeEs);
			String modelo = crx5.getModelo();
			crx5.setModelo(modelo + "_ES");
			customSerializerParams();
			jsonResEs = this.serialize(crx5);
			LocaleContextHolder.setLocale(localeEu);
			crx5.setModelo(modelo + "_EU");
			customSerializerParams();
			jsonResEu = this.serialize(crx5);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a comprobar en la prueba");
		}

		String jsonReqEs = "{\"modelo\":\"CRX-5_ES\",\"tiempoConstruccion\":\"12:30:22\",\"precio\":\"9.123.230,3456789\",\"marca\":{\"nombre\":\"Foo\",\"pais\":{\"dsO\":\"España\"},\"empleados\":[{\"nombre\":\"Eneko\",\"fechaNacimiento\":\"01/01/1981\"},{\"nombre\":\"Laura\",\"fechaNacimiento\":\"12/12/1992\"}]},\"fechaConstruccion\":\"02/06/1995 13:45:11\",\"coste\":\"9.123.000,01234568\"}";
		String jsonReqEu = "{\"modelo\":\"CRX-5_EU\",\"tiempoConstruccion\":\"12:30:22\",\"precio\":\"9.123.230,3456789\",\"marca\":{\"nombre\":\"Foo\",\"pais\":{\"dsO\":\"España\"},\"empleados\":[{\"nombre\":\"Eneko\",\"fechaNacimiento\":\"1981/01/01\"},{\"nombre\":\"Laura\",\"fechaNacimiento\":\"1992/12/12\"}]},\"fechaConstruccion\":\"1995/06/02 13:45:11\",\"coste\":\"9.123.000,01234568\"}";

		try {
			mockMvc.perform(get("/serialization/test?locale=es"))

					.andDo(postSerialize(mockMvc, localeEs, jsonResEs, jsonReqEs));

		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de serialización en castellano [/serialization/serialize]");
		}

		try {
			mockMvc.perform(get("/serialization/test?locale=eu"))

					.andDo(postSerialize(mockMvc, localeEu, jsonResEu, jsonReqEu));

		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de serialización en euskera [/serialization/serialize]");
		}
	}

	/**
	 * ThreadSafeCache para el CustomSerializer de marca.pais
	 */
	private static void customSerializerParams() {
		ThreadSafeCache.clearCurrentThreadCache();
		ThreadSafeCache.addValue("dsO", "dsO");
	}

}
