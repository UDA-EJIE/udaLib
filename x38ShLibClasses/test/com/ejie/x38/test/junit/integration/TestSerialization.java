package com.ejie.x38.test.junit.integration;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import javax.annotation.Resource;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.IframeXHREmulationFilter;
import com.ejie.x38.UdaFilter;
import com.ejie.x38.serialization.UdaMappingJackson2HttpMessageConverter;
import com.ejie.x38.test.common.model.Coche;
import com.ejie.x38.test.common.model.Empleado;
import com.ejie.x38.test.common.model.Marca;
import com.ejie.x38.test.control.SerializationController;
import com.ejie.x38.util.DateTimeManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebAppConfiguration
@ContextConfiguration(classes = { TestConfig.class })

@RunWith(SpringJUnit4ClassRunner.class)
public class TestSerialization {

	@Resource
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Autowired
	private IframeXHREmulationFilter iframeXHREmulationFilter;

	@Autowired
	private UdaFilter udaFilter;

	@Autowired
	private SerializationController serializationController;

	@Autowired
	private UdaMappingJackson2HttpMessageConverter udaMappingJackson2HttpMessageConverter;

	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(serializationController).addFilter(udaFilter, "/*")
				.addFilter(iframeXHREmulationFilter, "/*").build();
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

		Empleado eneko = new Empleado("Eneko", null, null);
		Empleado laura = new Empleado("Laura", null, null);
		Marca foo = new Marca("Foo", null, Arrays.asList(eneko, laura));
		Coche crx5 = new Coche("CRX-5", foo);
		crx5.setCoste(BigDecimal.valueOf(9123000.0123456789));

		String strDateTime = "02/06/1995 13:45:11";
		SimpleDateFormat sdf = DateTimeManager.getTimestampFormat(localeEs);

		try {
			crx5.setFechaConstruccion(new Timestamp(sdf.parse(strDateTime).getTime()));
		} catch (ParseException e) {
			fail("ParseException inicializando el objeto para el caso de prueba");
		}

		String jsonRes = "";
		try {
			jsonRes = this.serialize(crx5);
		} catch (JsonProcessingException e1) {
			fail("Exception serializando el objeto que se va a comprobar en la prueba");
		}

		String jsonReqEs = "{\"modelo\":\"CRX-5\",\"marca\":{\"nombre\":\"Foo\",\"empleados\":[{\"nombre\":\"Eneko\"},{\"nombre\":\"Laura\"}]},\"fechaConstruccion\":\"02/06/1995 13:45:11\",\"coste\":\"9.123.000,01234568\"}";
		String jsonReqEu = "{\"modelo\":\"CRX-5\",\"marca\":{\"nombre\":\"Foo\",\"empleados\":[{\"nombre\":\"Eneko\"},{\"nombre\":\"Laura\"}]},\"fechaConstruccion\":\"1995/06/02 13:45:11\",\"coste\":\"9.123.000,01234568\"}";

		LocaleContextHolder.setLocale(localeEs);

		try {
			mockMvc.perform(post("/serialization/serialize").contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.ALL).content(jsonReqEs))

					.andExpect(status().is(200))

					.andExpect(content().string(jsonRes));
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de serialización en castellano [/serialization/serialize]");
		}

		LocaleContextHolder.setLocale(localeEu);

		try {
			mockMvc.perform(post("/serialization/serialize").contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.ALL).content(jsonReqEu))

					.andExpect(status().is(200))

					.andExpect(content().string(jsonRes));
		} catch (Exception e) {
			fail("Exception al realizar la petición POST con el controller de prueba de serialización en euskera [/serialization/serialize]");
		}
	}

}
