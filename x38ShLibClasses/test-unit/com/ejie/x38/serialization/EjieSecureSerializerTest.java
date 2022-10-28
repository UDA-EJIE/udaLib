package com.ejie.x38.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.SecureIdentifiable;
import org.hdiv.services.TrustAssertion;
import org.junit.Before;
import org.junit.Test;

import com.ejie.x38.dto.TableRequestDto;
import com.ejie.x38.hdiv.protection.IdProtectionDataManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EjieSecureSerializerTest {
	
	private ObjectMapper mapper;
	
	private IdProtectionDataManager idProtectionDataManager;
	
	@Before
	public void setup() {
		
		idProtectionDataManager = new InMemoryIdProtectionDataManager();
		
		EjieSecureModule.setIdProtectionDataManager(idProtectionDataManager);
		mapper = new ObjectMapper();
		mapper.registerModule(new EjieSecureModule(idProtectionDataManager));
	}
	
	
	
	@Test
	public void testNonSecuredSerialize() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new NonSecured("name"));
		assertTrue(!serial.contains("\"nid\":"));

	}

	@Test
	public void testSerializeArray() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(Arrays.asList(new Secured("name")));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));

	}
	
	@Test
	public void testSerialize() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new Secured("name"));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));

	}
	
	@Test
	public void testDeserialize() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new Secured("name"));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));
		//Secured secured = mapper.readValue("{\"name\":\"name\",\"id\":\"cSa-n8bn-6fP-Xn0btWbdtEbSw-T8bnJncz0nJn0btWbdn02nXE$Jncz0n5-:$:-wtan_Jnc\"}", Secured.class);
		Secured secured = mapper.readValue(serial, Secured.class);
		assertEquals("name_Sec", secured.getId());
		assertEquals("name", secured.getName());
	}
	
	@Test(expected = JsonMappingException.class)
	public void testDeserializeUnSecuredSecured() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new Secured("name"));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));
		//Modified Id in the serialized string
		mapper.readValue("{\"name\":\"name\",\"id\":\"cSa-n8bn-6fP-Xn0btWbdtEbSw-T8bnJncz0nJn0btWbdn02nXE$Jncz0n5-:$:-wtan_Jtc\"}", Secured.class);
	}
	
	@Test(expected = JsonMappingException.class)
	public void testDeserializeUnSecuredClass() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new Secured("name"));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));
		mapper.readValue("{\"name\":\"name\",\"id\":\"cSr-n8bn-6fP-Xn0btWbdtEbSw-T8bnJncz0nJn0btWbdn02nXE$Jncz0n5-:$:-wtan_Jnc\"}", Secured.class);
	}
	
	@Test(expected = JsonMappingException.class)
	public void testDeserializeUnSecured() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new Secured("name"));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));
		mapper.readValue("{\"name\":\"name\",\"id\":\"name_Sec\"}", Secured.class);
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
	public void testDeserializeTrusted() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new SecuredTrusted("name", "code"));
		assertTrue(serial.contains("\"nid\":\"code\""));
		SecuredTrusted securedTrusted = mapper.readValue("{\"name\":\"name\",\"code\":\"cSa-n8bn-6fP-Xn0btWbdtEbSw-T8bnJncz0nJn0btWbdn02nXE$Jncz0n520zXEn5-:$:-cS5n\"}", SecuredTrusted.class);
		assertEquals("code", securedTrusted.getCode());
		assertEquals("name", securedTrusted.getName());
	}
	
	@Test(expected = JsonMappingException.class)
	public void testDeserializeUnTrusted() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new SecuredTrusted("name", "code"));
		assertTrue(serial.contains("\"nid\":\"code\""));
		//Modified Id in the serialized string
		mapper.readValue("{\"name\":\"name\",\"code\":\"cSa-n8bn-6fP-Xn0btWbdtEbSw-T8bnJncz0nJn0btWbdn02nXE$Jncz0n520zXEn5-:$:-cS5y\"}", SecuredTrusted.class);
	}
	
	@Test(expected = JsonMappingException.class)
	public void testDeserializeUnTrustedClass() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new SecuredTrusted("name", "code"));
		assertTrue(serial.contains("\"nid\":\"code\""));
		//Modified Id in the serialized string
		mapper.readValue("{\"name\":\"name\",\"code\":\"cfa-n8bn-6fP-Xn0btWbdtEbSw-T8bnJncz0nJn0btWbdn02nXE$Jncz0n520zXEn5-:$:-cS5n\"}", SecuredTrusted.class);
	}
	
	@Test(expected = JsonMappingException.class)
	public void testDeserializeUnSecuredSecureTrusted() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new SecuredTrusted("name", "code"));
		assertTrue(serial.contains("\"nid\":\"code\""));
		//Modified Id in the serialized string
		mapper.readValue("{\"name\":\"name\",\"code\":\"code\"}", SecuredTrusted.class);
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

		String serial = mapper.writeValueAsString(new SecuredTrustedIdentifiableParent("name"));
		assertTrue(serial.contains("\"nid\":\"name_Sec\""));

	}
	
	@Test
	public void testUsuarioSerialize() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new Usuario( "1", "nombreu", "ap1", null));
		assertTrue(serial.contains("\"nid\":\"1\""));
		assertTrue(serial.contains("\"apellido2\":null"));

	}
	
	@Test
	public void testMultiPKSerialize() throws JsonProcessingException, IOException {

		String serial = mapper.writeValueAsString(new MultiPk( new BigDecimal(1), new BigDecimal(2), "nombre", "ap1", "ap2"));
		assertTrue(serial.contains("\"nid\":\"1@@@@2\""));
		assertTrue(serial.contains("\"id\":\"1@@@@2\""));
		ObjectNode node = (ObjectNode)mapper.readTree(serial);
		node.remove("nid");
		node.remove("id");
		mapper.readValue(mapper.writeValueAsString(node), MultiPk.class);
	}
	
	@Test
	public void testTableRequestDTO() throws JsonProcessingException, IOException {
		idProtectionDataManager.storeSecureId(SecuredTrusted.class, "code");
		idProtectionDataManager.storeSecureId(SecuredTrusted.class, "c6d");
		String selialized = "{\"multiselection\":{\"selectedAll\":false,\"selectedIds\":[\"cSa-n8bn-6fP-Xn0btWbdtEbSw-T8bnJncz0nJn0btWbdn02nXE$Jncz0n520zXEn5-:$:-cS5n\",\"cSa-n8bn-6fP-Xn0btWbdtEbSw-T8bnJncz0nJn0btWbdn02nXE$Jncz0n520zXEn5-:$:-cL5\"]},\"rows\":10,\"page\":1,\"sidx\":\"nombre\",\"sord\":\"asc\",\"core\":{\"pkToken\":\"~\",\"pkNames\":[\"id\"]}}";
		TableRequestDto tableRequestDto = mapper.readValue(selialized, TableRequestDto.class);
		assertTrue(tableRequestDto.getMultiselection().getSelectedIds().size()==2);
		assertTrue(tableRequestDto.getMultiselection().getSelectedIds().contains("code"));
		assertTrue(tableRequestDto.getMultiselection().getSelectedIds().contains("c6d"));
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

	public static class Secured implements SecureIdentifiable<String> {

		private String name;

		public Secured() {
		}
		
		public Secured(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public void setId(String id) {
			
		}

		@Override
		public String getId() {
			return name + "_Sec";
		}

	}

	public static class SecuredTrusted implements SecureIdContainer {

		private String name;

		@TrustAssertion(idFor = SecuredTrusted.class)
		private String code;

		public SecuredTrusted() {
		}
		
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
	
	public static class InMemoryIdProtectionDataManager implements IdProtectionDataManager{
		
		private static final Map<Class<?>, HashSet<String>> ID_CONTAINER = new HashMap<Class<?>, HashSet<String>>();
		
		public void storeSecureId(Class<?> clazz, String nId) {
			
			HashSet<String> secureIds = ID_CONTAINER.get(clazz);
			if(secureIds == null) {
				secureIds = new HashSet<String>();
				ID_CONTAINER.put(clazz, secureIds);
			}
			secureIds.add(nId);
		}
		
		public boolean isAllowedSecureId(Class<?> clazz, String nId) {
			
			HashSet<String> secureIds = ID_CONTAINER.get(clazz);
			return (secureIds != null && secureIds.contains(nId) && isAllowedToAction(clazz, nId));
		}
		
		public boolean isAllowedToAction(Class<?> clazz, String nId) {
			return true;
		}

		@Override
		public boolean isAllowedAction(HttpServletRequest request) {
			return true;
		}

		@Override
		public void allowAction(String url) {
		}

		@Override
		public void allowId(String url, Class<?> clazz, String nId) {
		}

		@Override
		public void remapAction(String url, String toRemapURL) {
			// TODO Auto-generated method stub
			
		}	

	}
}
