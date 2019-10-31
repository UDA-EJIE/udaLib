package com.ejie.x38.test.unit.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import com.ejie.x38.util.StaticsContainer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.ejie.x38.json.JSONObject;
import com.ejie.x38.test.model.Alumno;
import com.ejie.x38.validation.ValidationManager;

public class TestValidationManager {

	private static ValidationManager validationManager;
	private static Errors errors;

	@Before
	public void setUpBefore() throws Exception {
		StaticsContainer.webAppName = "x38";
		validationManager = new ValidationManager();
	}

	@Test(expected = Test.None.class /* no exception expected */)
	public final void testInit() {
		validationManager.init();
		Alumno alumno = new Alumno();
		errors = new BeanPropertyBindingResult(alumno, "alumno");
		validationManager.validate(errors, alumno);
	}

	@Test
	public final void testValidate() {
		validationManager.init();
		Alumno alumno = new Alumno();
		errors = new BeanPropertyBindingResult(alumno, "alumno");
		validationManager.validate(errors, alumno);

		assertTrue("No se han resuelto errores en la validación", errors.hasErrors());

		if (errors.hasErrors()) {
			assertEquals("Deben resolverse dos errores en el objeto Alumno", 2, errors.getAllErrors().size());

			if (errors.getAllErrors().size() == 2) {
				FieldError errorNombre = errors.getFieldError("nombre");
				FieldError errorUsuario = errors.getFieldError("usuario");

				assertEquals("Debe haber error de requerimiento para el campo nombre", "validacion.required",
						errorNombre.getDefaultMessage());
				assertEquals("Debe haber error de requerimiento para el campo usuario", "validacion.required",
						errorUsuario.getDefaultMessage());
			}
		}
	}

	@Test
	public final void testGetRupFeedbackMsg() {
		validationManager.init();
		Alumno alumno = new Alumno();
		errors = new BeanPropertyBindingResult(alumno, "alumno");
		Map<String, Object> expected = new HashMap<String, Object>();
		String required = "required";
		String label = "comun.validation.required";
		expected.put("style", required);
		expected.put("label", label);
		Map<String, Object> rupFeedbackMsg = validationManager.getRupFeedbackMsg(label, required);
		assertEquals("Debe devolver un mapa con label y style", expected, rupFeedbackMsg);
	}

	@Test
	public final void testGetMessageJSONObject() {
		validationManager.init();
		Alumno alumno = new Alumno();
		errors = new BeanPropertyBindingResult(alumno, "alumno");
		validationManager.validate(errors, alumno);

		assertTrue("No se han resuelto errores en la validación", errors.hasErrors());

		if (errors.hasErrors()) {
			assertEquals("Deben resolverse dos errores en el objeto Alumno", 2, errors.getAllErrors().size());

			JSONObject json = validationManager.getMessageJSON(errors);
			assertNotNull("El json devuelto no debe ser nulo", json);
			assertTrue("El json devuelto contendrá los errores dentro de 'rupErrorFields'", json.has("rupErrorFields"));
			assertEquals("El json devuelto contendrá 2 errores", 2,
					((BeanPropertyBindingResult) json.get("rupErrorFields")).getErrorCount());
		}

	}

	@Test
	public final void testGetMessageJSONObjectObject() {
		validationManager.init();
		Alumno alumno = new Alumno();
		errors = new BeanPropertyBindingResult(alumno, "alumno");
		validationManager.validate(errors, alumno);

		assertTrue("No se han resuelto errores en la validación", errors.hasErrors());

		if (errors.hasErrors()) {
			assertEquals("Deben resolverse dos errores en el objeto Alumno", 2, errors.getAllErrors().size());

			JSONObject json = validationManager.getMessageJSON(errors, "Alumno no válido");
			assertNotNull("El json devuelto no debe ser nulo", json);
			assertTrue("El json devuelto contendrá los errores dentro de 'rupErrorFields'", json.has("rupErrorFields"));
			assertTrue("El json devuelto contendrá las especificaciones del feedback 'rupFeedback'",
					json.has("rupFeedback"));

			assertEquals("El feedback devuelto debe tener el formato correcto", "{\"message\":\"Alumno no válido\"}",
					json.get("rupFeedback").toString());
		}
	}

	@Test
	public final void testGetMessageJSONObjectObjectString() {
		validationManager.init();
		Alumno alumno = new Alumno();
		errors = new BeanPropertyBindingResult(alumno, "alumno");
		validationManager.validate(errors, alumno);

		assertTrue("No se han resuelto errores en la validación", errors.hasErrors());

		if (errors.hasErrors()) {
			assertEquals("Deben resolverse dos errores en el objeto Alumno", 2, errors.getAllErrors().size());

			JSONObject json = validationManager.getMessageJSON(errors, "Alumno no válido", "veryImportant");
			assertNotNull("El json devuelto no debe ser nulo", json);
			assertTrue("El json devuelto contendrá los errores dentro de 'rupErrorFields'", json.has("rupErrorFields"));
			assertTrue("El json devuelto contendrá las especificaciones del feedback 'rupFeedback'",
					json.has("rupFeedback"));

			assertEquals("El feedback devuelto debe tener el formato correcto",
					"{\"message\":\"Alumno no válido\",\"style\":\"veryImportant\"}",
					json.get("rupFeedback").toString());
		}
	}
}
