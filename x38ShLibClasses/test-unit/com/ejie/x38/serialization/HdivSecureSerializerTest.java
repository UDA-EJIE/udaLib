package com.ejie.x38.serialization;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.SecureIdentifiable;
import org.hdiv.services.TrustAssertion;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class HdivSecureSerializerTest {
	
	private ObjectMapper mapper;
	
	@Before
	public void setup() {
		mapper = new ObjectMapper();
		mapper.registerModule(new HdivSecureModule());
	}
	
	@Test
	public void testNonSecuredSerialize() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new NonSecured("name"));
		assertTrue(!serial.contains("\"nid\":"));

	}

	@Test
	public void testSerialize() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(Arrays.asList(new Secured("name")));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));

	}

	@Test
	public void testSerializeList() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(Arrays.asList(new Secured("name"), new Secured("name2")));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));
		assertTrue(serial.contains("\"nid\":\"name2_Sec\""));

	}

	@Test
	public void testTrustedSerialize() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(Arrays.asList(new SecuredTrusted("name", "code")));
		assertTrue(serial.contains("\"nid\":\"code\""));

	}

	@Test
	public void testTrustedSerializeList() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(Arrays.asList(new SecuredTrusted("name", "code"), new SecuredTrusted("name2", "code2")));

		assertTrue(serial.contains("\"nid\":\"code\""));
		assertTrue(serial.contains("\"nid\":\"code2\""));

	}

	@Test
	public void testTrustedTrustedSerialize() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new SecuredTrustedParent("name", "code"));
		assertTrue(serial.contains("\"nid\":\"code\""));

	}

	@Test
	public void testIdentifiableTrustedSerialize() throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new HdivSecureModule());
		String serial = mapper.writeValueAsString(new SecuredTrustedIdentifiableParent("name"));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));

	}
	
	@Test
	public void testUsuarioSerialize() throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new HdivSecureModule());
		String serial = mapper.writeValueAsString(new Usuario( "1", "nombreu", "ap1", null));
		assertTrue(serial.contains("\"nid\":\"1\""));
		assertTrue(serial.contains("\"apellido2\":null"));

	}
	
	public class NonSecured {

		private String name;

		public NonSecured(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public class Secured implements SecureIdentifiable<String> {

		private String name;

		public Secured(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String getId() {
			return name + "_Sec";
		}

	}

	public class SecuredTrusted implements SecureIdContainer {

		private String name;

		@TrustAssertion(idFor = SecuredTrusted.class)
		private String code;

		public SecuredTrusted(String name, String code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

	}

	public class SecuredTrustedParent implements SecureIdContainer {

		private String name;

		private SecuredTrusted securedTrusted;

		public SecuredTrustedParent(String name, String code) {
			this.name = name;
			this.securedTrusted = new SecuredTrusted(name, code);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public SecuredTrusted getSecuredTrusted() {
			return securedTrusted;
		}

		public void setSecuredTrusted(SecuredTrusted securedTrusted) {
			this.securedTrusted = securedTrusted;
		}

	}

	public class SecuredTrustedIdentifiableParent implements SecureIdContainer {

		private String name;

		private Secured secured;

		public SecuredTrustedIdentifiableParent(String name) {
			this.name = name;
			this.secured = new Secured(name);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Secured getSecured() {
			return secured;
		}

		public void setSecured(Secured secured) {
			this.secured = secured;
		}

	}
	
	public class Usuario implements java.io.Serializable, SecureIdentifiable<String> {

		private static final long serialVersionUID = 2726038020603366026L;
		private String id;
		private String nombre;
		private String apellido1;
		private String apellido2;
		
		
	    public Usuario(String id, String nombre, String apellido1, String apellido2) {	
	        this.id = id;
	        this.nombre = nombre;
	        this.apellido1 = apellido1;
	        this.apellido2 = apellido2;
	    }


		public String getId() {
			return id;
		}


		public void setId(String id) {
			this.id = id;
		}


		public String getNombre() {
			return nombre;
		}


		public void setNombre(String nombre) {
			this.nombre = nombre;
		}


		public String getApellido1() {
			return apellido1;
		}


		public void setApellido1(String apellido1) {
			this.apellido1 = apellido1;
		}


		public String getApellido2() {
			return apellido2;
		}


		public void setApellido2(String apellido2) {
			this.apellido2 = apellido2;
		}
		
	}
}
