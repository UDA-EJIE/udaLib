package com.ejie.x38.serialization;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.SecureIdentifiable;
import org.hdiv.services.TrustAssertion;
import org.junit.Before;
import org.junit.Test;

import com.ejie.x38.hdiv.config.SecureModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SecureSerializerTest {
	
	private ObjectMapper mapper;
	
	@Before
	public void setup() {
		mapper = new ObjectMapper();
		mapper.registerModule(new SecureModule());
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
		mapper.registerModule(new SecureModule());
		String serial = mapper.writeValueAsString(new SecuredTrustedIdentifiableParent("name"));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));

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
}
