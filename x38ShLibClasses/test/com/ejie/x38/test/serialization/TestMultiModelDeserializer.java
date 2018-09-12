package com.ejie.x38.test.serialization;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.test.common.model.Provincia;
import com.ejie.x38.test.common.model.Usuario;

public class TestMultiModelDeserializer {

	private static HashMap<String, Object> expected = null;
	private static String json = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Usuario user = new Usuario("1", "Javier");
		Provincia prov = new Provincia(BigDecimal.valueOf(42), "Bizkaia", "Bizkaia", null);
		expected = new HashMap<String, Object>();
		expected.put("usuario", user);
		expected.put("provincia", prov);
		json = "{usuario:{codigo:\"1\", nombre:\"Javier\"},provincia:{codigo:\"42\", nombre:\"Bizkaia\"},"
				+ "rupEntityMapping:{usario:\"com.ejie.x38.test.common.model.Usuario\",provincia:\"com.ejie.x38.test.common.model.Provincia\"}}";
	}

	@Test
	public final void testDeserializer() throws IOException {
		HashMap<String, Object> resDeserializer = deserialize();

	}

	private HashMap<String, Object> deserialize() {
		HashMap<String, Object> ret = null;
		return ret;
	}
}
