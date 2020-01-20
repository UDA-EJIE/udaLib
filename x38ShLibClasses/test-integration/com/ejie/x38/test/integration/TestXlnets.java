package com.ejie.x38.test.integration;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.http.Cookie;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;

import com.ejie.x38.UdaFilter;
import com.ejie.x38.test.integration.config.X38TestingApplicationContext;
import com.ejie.x38.test.integration.config.X38TestingContextLoader;

/**
 * @author Eurohelp S.L.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(loader = X38TestingContextLoader.class, classes = X38TestingApplicationContext.class)
public class TestXlnets {

	@Resource
	private WebApplicationContext webApplicationContext;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private UdaFilter udaFilter;

	private MockMvc mockMvc;

//	private Document tokenXlnetsLdap;
	private Document tokenXlnetsPassword;
	private Document tokenXlnetsBarcos;

	/**
	 * @param rutaToken String
	 * @param msgError  String
	 * @return Document
	 */
	private Document getXlnetsToken(String rutaToken, String msgError) {
		Document token = null;
		try {
			InputStream is = TestXlnets.class.getResourceAsStream(rutaToken);
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				token = builder.parse(is);
			} finally {
				is.close();
			}
		} catch (Exception e) {
			fail(msgError);
		}

		return token;
	}

	@Before
	public void setUp() {
		String n38Lib = "c:/usr/javase6/xlnets/";

		System.setProperty("N38IPServidor", "0.0.1");
		System.setProperty("N38ENTORNO", n38Lib + "/config/n38c/wl11_7001");
		System.setProperty("file.encoding", "iso8859-1");

//		tokenXlnetsLdap = getXlnetsToken("/x38TestingWar/token-xlnets-ldap.xml",
//				"Exception al inicializar los tests de XLNETS de LDAP recuperando el archivo xml con el token");
		tokenXlnetsPassword = getXlnetsToken("/x38TestingWar/token-xlnets-password.xml",
				"Exception al inicializar los tests de XLNETS de usuario/contraseña recuperando el archivo xml con el token");
		tokenXlnetsBarcos = getXlnetsToken("/x38TestingWar/token-xlnets-barcos.xml",
				"Exception al inicializar los tests de XLNETS de juego de barcos recuperando el archivo xml con el token");

		mockMvc = MockMvcBuilders

				.webAppContextSetup(webApplicationContext)

				.addFilters(udaFilter, springSecurityFilterChain)

				.build();
	}

	/**
	 * @param token    Document
	 * @param expect   String
	 * @param msgError String
	 */
	private void testGetDo(Document token, String expect, String msgError) {
		try {

			final Cookie n38DominioUid = new Cookie("n38DominioUid", "D0_Euskalsarea01");
			n38DominioUid.setSecure(true);

			final Cookie n38UidSesion = new Cookie("n38UidSesion", "1519022548012");
			n38UidSesion.setSecure(true);

			mockMvc.perform(

					get("/security/get")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.cookie(n38DominioUid)

							.cookie(n38UidSesion)

							.requestAttr("N38SesionXML", token)

							.content(""))

					.andExpect(status().isOk())

					.andExpect(content().string(expect));
		} catch (Exception e) {
			fail(msgError);
		}
	}

	/**
	 * @param token    Document
	 * @param expect   String
	 * @param msgError String
	 */
	private void testAjaxGetDo(Document token, String expect, String msgError) {
		try {
			final Cookie n38DominioUid = new Cookie("n38DominioUid", "D0_Euskalsarea01");
			n38DominioUid.setSecure(true);
			final Cookie n38UidSesion = new Cookie("n38UidSesion", "1519022548012");
			n38UidSesion.setSecure(true);

			mockMvc.perform(

					get("/security/get")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.cookie(n38DominioUid)

							.cookie(n38UidSesion)

							.requestAttr("N38SesionXML", token)

							.content(""))

					.andExpect(status().isOk())

					.andExpect(content().string(expect));
		} catch (Exception e) {
			fail(msgError);
		}
	}

	/**
	 * @param token    Document
	 * @param expect   String
	 * @param msgError String
	 */
	private void testPostDo(Document token, String expect, String msgError) {
		try {
			final Cookie n38DominioUid = new Cookie("n38DominioUid", "D0_Euskalsarea01");
			n38DominioUid.setSecure(true);
			final Cookie n38UidSesion = new Cookie("n38UidSesion", "1519022548012");
			n38UidSesion.setSecure(true);

			mockMvc.perform(

					post("/security/post")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.cookie(n38DominioUid)

							.cookie(n38UidSesion)

							.requestAttr("N38SesionXML", token)

							.content(""))

					.andExpect(status().isOk())

					.andExpect(content().string(expect));
		} catch (Exception e) {
			fail(msgError);
		}
	}

	/**
	 * @param token    Document
	 * @param expect   String
	 * @param msgError String
	 */
	private void testAjaxPostDo(Document token, String expect, String msgError) {
		try {
			final Cookie n38DominioUid = new Cookie("n38DominioUid", "D0_Euskalsarea01");
			n38DominioUid.setSecure(true);
			final Cookie n38UidSesion = new Cookie("n38UidSesion", "1519022548012");
			n38UidSesion.setSecure(true);

			mockMvc.perform(

					post("/security/post")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.cookie(n38DominioUid)

							.cookie(n38UidSesion)

							.requestAttr("N38SesionXML", token)

							.content(""))

					.andExpect(status().isOk())

					.andExpect(content().string(expect));
		} catch (Exception e) {
			fail(msgError);
		}
	}

	/**
	 * @param token    Document
	 * @param expect   String
	 * @param msgError String
	 */
	private void testPutDo(Document token, String expect, String msgError) {
		try {
			final Cookie n38DominioUid = new Cookie("n38DominioUid", "D0_Euskalsarea01");
			n38DominioUid.setSecure(true);
			final Cookie n38UidSesion = new Cookie("n38UidSesion", "1519022548012");
			n38UidSesion.setSecure(true);

			mockMvc.perform(

					put("/security/put")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.cookie(n38DominioUid)

							.cookie(n38UidSesion)

							.requestAttr("N38SesionXML", token)

							.content(""))

					.andExpect(status().isOk())

					.andExpect(content().string(expect));
		} catch (Exception e) {
			fail(msgError);
		}
	}

	/**
	 * @param token    Document
	 * @param expect   String
	 * @param msgError String
	 */
	private void testAjaxPutDo(Document token, String expect, String msgError) {
		try {

			final Cookie n38DominioUid = new Cookie("n38DominioUid", "D0_Euskalsarea01");
			n38DominioUid.setSecure(true);
			final Cookie n38UidSesion = new Cookie("n38UidSesion", "1519022548012");
			n38UidSesion.setSecure(true);

			mockMvc.perform(

					put("/security/put")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.cookie(n38DominioUid)

							.cookie(n38UidSesion)

							.requestAttr("N38SesionXML", token)

							.content(""))

					.andExpect(status().isOk())

					.andExpect(content().string(expect));
		} catch (Exception e) {
			fail(msgError);
		}
	}

	/**
	 * @param token    Document
	 * @param expect   String
	 * @param msgError String
	 */
	private void testAccessDeniedDo(Document token, String expect, String msgError) {
		try {
			final Cookie n38DominioUid = new Cookie("n38DominioUid", "D0_Euskalsarea01");
			n38DominioUid.setSecure(true);
			final Cookie n38UidSesion = new Cookie("n38UidSesion", "1519022548012");
			n38UidSesion.setSecure(true);

			mockMvc.perform(

					get("/security/security")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.cookie(n38DominioUid)

							.cookie(n38UidSesion)

							.requestAttr("N38SesionXML", token)

							.content(""))

					.andExpect(status().isForbidden())

					.andExpect(forwardedUrl("/accessDenied"));
		} catch (Exception e) {
			fail(msgError);
		}
	}

	/**
	 * @param token    Document
	 * @param expect   String
	 * @param msgError String
	 */
	private void testAjaxAccessDeniedDo(Document token, String expect, String msgError) {
		try {
			final Cookie n38DominioUid = new Cookie("n38DominioUid", "D0_Euskalsarea01");
			n38DominioUid.setSecure(true);
			final Cookie n38UidSesion = new Cookie("n38UidSesion", "1519022548012");
			n38UidSesion.setSecure(true);

			mockMvc.perform(

					get("/security/security")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.cookie(n38DominioUid)

							.cookie(n38UidSesion)

							.requestAttr("N38SesionXML", token)

							.content(""))

					.andExpect(status().isForbidden())

					.andExpect(content().string("security.ajaxAccesError"));
		} catch (Exception e) {
			fail(msgError);
		}
	}

	@Test
	public void test() {
		try {
			mockMvc.perform(

					get("/security/test"))

					.andExpect(status().isOk());
		} catch (Exception e) {
			fail("Exception al realizar el test de conexión GET con el controller de prueba de excepciones [/security/test]");
		}
	}

	@Test
	public void testGet() {
//		testGetDo(tokenXlnetsLdap,
//				"{\"authorities\":[{\"authority\":\"ROLE_X38-IN-FOO\"}],\"principal\":\"FOO\"}",
//				"Exception al realizar la petición GET para LDAP con el controller de prueba de gestión de excepciones [/security/get]");
		testGetDo(tokenXlnetsPassword, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"X21A-01\"}",
				"Exception al realizar la petición GET para usuario/contraseña con el controller de prueba de gestión de excepciones [/security/get]");
		testGetDo(tokenXlnetsBarcos, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"Z0000001\"}",
				"Exception al realizar la petición GET para juego de barcos con el controller de prueba de gestión de excepciones [/security/get]");

//		testAjaxGetDo(tokenXlnetsLdap,
//				"{\"authorities\":[{\"authority\":\"ROLE_X38-IN-FOO\"}],\"principal\":\"FOO\"}",
//				"Exception al realizar la petición GET para LDAP con el controller de prueba de gestión de excepciones [/security/get]");
		testAjaxGetDo(tokenXlnetsPassword, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"X21A-01\"}",
				"Exception al realizar la petición GET para usuario/contraseña con el controller de prueba de gestión de excepciones [/security/get]");
		testAjaxGetDo(tokenXlnetsBarcos, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"Z0000001\"}",
				"Exception al realizar la petición GET para juego de barcos con el controller de prueba de gestión de excepciones [/security/get]");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPost() {
//		testPostDo(tokenXlnetsLdap,
//				"{\"authorities\":[{\"authority\":\"ROLE_X38-IN-FOO\"}],\"principal\":\"FOO\"}",
//				"Exception al realizar la petición POST para LDAP con el controller de prueba de gestión de excepciones [/security/post]");
		testPostDo(tokenXlnetsPassword, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"X21A-01\"}",
				"Exception al realizar la petición POST para usuario/contraseña con el controller de prueba de gestión de excepciones [/security/post]");
		testPostDo(tokenXlnetsBarcos, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"Z0000001\"}",
				"Exception al realizar la petición POST para juego de barcos con el controller de prueba de gestión de excepciones [/security/post]");

//		testAjaxPostDo(tokenXlnetsLdap,
//				"{\"authorities\":[{\"authority\":\"ROLE_X38-IN-FOO\"}],\"principal\":\"FOO\"}",
//				"Exception al realizar la petición POST para LDAP con el controller de prueba de gestión de excepciones [/security/post]");
		testAjaxPostDo(tokenXlnetsPassword,
				"{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"X21A-01\"}",
				"Exception al realizar la petición POST para usuario/contraseña con el controller de prueba de gestión de excepciones [/security/post]");
		testAjaxPostDo(tokenXlnetsBarcos, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"Z0000001\"}",
				"Exception al realizar la petición POST para juego de barcos con el controller de prueba de gestión de excepciones [/security/post]");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testPut() {
//		testPutDo(tokenXlnetsLdap,
//				"{\"authorities\":[{\"authority\":\"ROLE_X38-IN-FOO\"}],\"principal\":\"FOO\"}",
//				"Exception al realizar la petición PUT para LDAP con el controller de prueba de gestión de excepciones [/security/put]");
		testPutDo(tokenXlnetsPassword, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"X21A-01\"}",
				"Exception al realizar la petición PUT para usuario/contraseña con el controller de prueba de gestión de excepciones [/security/put]");
		testPutDo(tokenXlnetsBarcos, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"Z0000001\"}",
				"Exception al realizar la petición PUT para juego de barcos con el controller de prueba de gestión de excepciones [/security/put]");

//		testAjaxPutDo(tokenXlnetsLdap,
//				"{\"authorities\":[{\"authority\":\"ROLE_X38-IN-FOO\"}],\"principal\":\"FOO\"}",
//				"Exception al realizar la petición PUT para LDAP con el controller de prueba de gestión de excepciones [/security/put]");
		testAjaxPutDo(tokenXlnetsPassword, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"X21A-01\"}",
				"Exception al realizar la petición PUT para usuario/contraseña con el controller de prueba de gestión de excepciones [/security/put]");
		testAjaxPutDo(tokenXlnetsBarcos, "{\"authorities\":[{\"authority\":\"ROLE_UDA\"}],\"principal\":\"Z0000001\"}",
				"Exception al realizar la petición PUT para juego de barcos con el controller de prueba de gestión de excepciones [/security/put]");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAccessDenied() {
//		testSecurityDo(tokenXlnetsLdap, null,
//				"Exception al realizar la petición SIN CREDENCIALES [/security/security]");
		testAccessDeniedDo(tokenXlnetsPassword, null,
				"Exception al realizar la petición SIN CREDENCIALES [/security/security]");
		testAccessDeniedDo(tokenXlnetsBarcos, null,
				"Exception al realizar la petición SIN CREDENCIALES [/security/security]");

//		testAjaxSecurityDo(tokenXlnetsLdap, null,
//				"Exception al realizar la petición SIN CREDENCIALES por ajax [/security/security]");
		testAjaxAccessDeniedDo(tokenXlnetsPassword, null,
				"Exception al realizar la petición SIN CREDENCIALES por ajax [/security/security]");
		testAjaxAccessDeniedDo(tokenXlnetsBarcos, null,
				"Exception al realizar la petición SIN CREDENCIALES por ajax [/security/security]");
	}

	@Test
	public void testNotLogged() {
		try {

			mockMvc.perform(

					get("/security/security")

							.contentType(MediaType.APPLICATION_JSON)

							.accept(MediaType.ALL)

							.content(""))

					.andExpect(status().isMovedTemporarily())

					.andExpect(redirectedUrl(
							"http://xlnets.servicios.jakina.ejiedes.net/n38a/N38LoginInicioServlet?N38API=http://localhost/security/security"));
		} catch (Exception e) {
			fail("Exception al realizar la petición SIN AUTENTICARSE [/security/security]");
		}

		try {

			mockMvc.perform(

					get("/security/security")

							.contentType(MediaType.APPLICATION_JSON)

							.header("X-Requested-With", "XMLHttpRequest")

							.accept(MediaType.ALL)

							.content(""))

					.andExpect(status().isMovedTemporarily())

					.andExpect(redirectedUrl(
							"http://xlnets.servicios.jakina.ejiedes.net/n38a/N38LoginInicioServlet?N38API=http://localhost/security/security"));
		} catch (Exception e) {
			fail("Exception al realizar la petición SIN AUTENTICARSE por ajax [/security/security]");
		}
	}

}
