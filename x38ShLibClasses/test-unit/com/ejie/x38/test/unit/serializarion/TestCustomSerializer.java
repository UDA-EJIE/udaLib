package com.ejie.x38.test.unit.serializarion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ejie.x38.serialization.CustomSerializer;
import com.ejie.x38.serialization.ThreadSafeCache;
import com.ejie.x38.test.model.Coche;
import com.ejie.x38.test.model.Empleado;
import com.ejie.x38.test.model.Marca;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author Eurohelp S.L.
 */
public class TestCustomSerializer {

	private static Coche crx5;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		Empleado eneko = new Empleado("Eneko");
		Empleado laura = new Empleado("Laura");
		Marca foo = new Marca("Foo");
		foo.setEmpleados(Arrays.asList(eneko, laura));
		TestCustomSerializer.crx5 = new Coche("CRX-5", foo);
	}

	/**
	 * Test method for
	 * {@link com.ejie.x38.serialization.CustomSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)}.
	 */
	@Test
	public final void testSerialize() {
		String modelo = "modelo";
		String marca = "marca";
		String jsonModelo = null;
		String jsonMarca = null;

		/**
		 * INICIO PRUEBA OBJETO PROPIEDAD SIMPLE
		 */
		ThreadSafeCache.clearCurrentThreadCache();
		ThreadSafeCache.addValue(modelo, modelo);

		try {
			jsonModelo = testSerializeDo(TestCustomSerializer.crx5);
		} catch (IOException e) {
			fail("IOException serializando con el CustomSerializer de un objeto con propiedad simple");
		} finally {
			assertTrue("No se ha realizado la serialización del objeto con propiedad simple",
					StringUtils.isNotEmpty(jsonModelo));
			assertEquals("La serialización del objeto con propiedad simple no es correcta", jsonModelo,
					"{\"modelo\":\"CRX-5\"}");
		}
		/**
		 * FIN PRUEBA OBJETO PROPIEDAD SIMPLE
		 */

		/**
		 * INICIO PRUEBA OBJETO PROPIEDAD COMPLEJA
		 */
		ThreadSafeCache.clearCurrentThreadCache();
		ThreadSafeCache.addValue(marca, marca);

		try {
			jsonMarca = testSerializeDo(TestCustomSerializer.crx5);
		} catch (IOException e) {
			fail("IOException serializando con el CustomSerializer de un objeto con propiedad compleja");
		} finally {
			assertTrue("No se ha realizado la serialización del objeto con propiedad compleja",
					StringUtils.isNotEmpty(jsonMarca));
			assertEquals("La serialización del objeto con propiedad simple no es correcta", jsonModelo,
					"{\"modelo\":\"CRX-5\",\"marca\":{\"nombre\":\"Foo\",\"empleados\":[{\"nombre\":\"Eneko\"},{\"nombre\":\"Laura\"}]}}");
		}

		/**
		 * FIN PRUEBA OBJETO PROPIEDAD COMPLEJA
		 */

	}

	/**
	 * @param crx5
	 * @return String
	 * @throws IOException
	 */
	private String testSerializeDo(Coche crx5) throws IOException {
		String json = "";
		Writer jsonWriter = new StringWriter();
		SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
		JsonGenerator jsonGenerator = null;

		try {
			jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
			new CustomSerializer().serialize(crx5, jsonGenerator, serializerProvider);
		} catch (Exception e) {
			fail("Exception serializando con el CustomSerializer");
		} finally {
			if (jsonGenerator != null) {
				jsonGenerator.close();
				jsonGenerator.flush();

				json = String.valueOf(jsonWriter.toString());

				jsonWriter.close();
				jsonWriter.flush();
			}
		}
		return json;
	}

}
