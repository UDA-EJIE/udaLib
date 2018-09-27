package com.ejie.x38.test.junit.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.FileInputStream;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.UdaFilter;
import com.ejie.x38.test.junit.integration.config.logging.X38TestingLoggingSpringJUnit4ClassRunner;
import com.ejie.x38.test.junit.integration.config.pib.X38TestingPibApplicationContext;
import com.ejie.x38.test.junit.integration.config.pib.X38TestingPibContextLoader;
import com.sun.mail.iap.ByteArray;

/**
 * @author Eurohelp S.L.
 */
@RunWith(X38TestingLoggingSpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(loader = X38TestingPibContextLoader.class, classes = X38TestingPibApplicationContext.class)
public class TestLogging {

	@Resource
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private UdaFilter udaFilter;

	@Autowired
	private PropertiesFactoryBean appConfiguration;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders

				.webAppContextSetup(webApplicationContext)

				.addFilters(udaFilter, springSecurityFilterChain)

				.build();
	}

	@Test
	public void test() {
		try {
			String logPath = appConfiguration.getObject().getProperty("log.path");

			String fileAuditoriaAccesos = "auditoriaAccesos_x38_WLTEST.log";
			FileInputStream auditoriaAccesos = null;
			try {
				mockMvc.perform(get("/exception/test"));

				auditoriaAccesos = new FileInputStream(logPath + "/logging/" + fileAuditoriaAccesos);
				ByteArray bAuditoriaAccesos = new ByteArray(auditoriaAccesos.available());
				auditoriaAccesos.read(bAuditoriaAccesos.getBytes());
				String strAuditoriaAccesos = new String(bAuditoriaAccesos.getBytes());

				assertTrue("Debe encontrarse trazas de seguridad de uda registradas en el acceso invocado",
						strAuditoriaAccesos.indexOf("com.ejie.x38.security") >= 0);
			} catch (Exception e) {
				fail("Exception al comprobar el archivo de log [" + fileAuditoriaAccesos + "]");
			} finally {
				if (auditoriaAccesos != null) {
					auditoriaAccesos.close();
				}
			}

			String fileAuditoriaBBDD = "auditoriaBBDD_x38_WLTEST.log";
			FileInputStream auditoriaBBDD = null;
			try {
				auditoriaBBDD = new FileInputStream(logPath + "/logging/" + fileAuditoriaBBDD);
				ByteArray bAuditoriaBBDD = new ByteArray(auditoriaBBDD.available());
				auditoriaBBDD.read(bAuditoriaBBDD.getBytes());
			} catch (Exception e) {
				fail("Exception al comprobar el archivo de log [" + fileAuditoriaBBDD + "]");
			} finally {
				if (auditoriaBBDD != null) {
					auditoriaBBDD.close();
				}
			}

			String fileIncidencias = "incidencias_x38_WLTEST.log";
			FileInputStream incidencias = null;
			try {
				mockMvc.perform(get("/exception/get"));

				incidencias = new FileInputStream(logPath + "/logging/" + fileIncidencias);
				ByteArray bIncidencias = new ByteArray(incidencias.available());
				incidencias.read(bIncidencias.getBytes());
				String strIncidencias = new String(bIncidencias.getBytes());

				assertTrue("Debe encontrarse traza de error que genera el método invocado",
						strIncidencias.indexOf("Excepción lanzada en el GET") >= 0);
			} catch (Exception e) {
				fail("Exception al comprobar el archivo de log [" + fileIncidencias + "]");
			} finally {
				if (incidencias != null) {
					incidencias.close();
				}
			}

			String fileSalidaEstandar = "salidaEstandar_x38_WLTEST.log";
			FileInputStream salidaEstandar = null;
			try {
				salidaEstandar = new FileInputStream(logPath + "/logging/" + fileSalidaEstandar);
				assertNotNull("El archivo [" + fileSalidaEstandar + "] debe existir", salidaEstandar);
				ByteArray bSalidaEstandar = new ByteArray(salidaEstandar.available());
				salidaEstandar.read(bSalidaEstandar.getBytes());
				String strSalidaEstandar = new String(bSalidaEstandar.getBytes());

				assertTrue("Debe encontrarse trazas del LogbackConfigurer",
						strSalidaEstandar.indexOf("com.ejie.x38.log.LogbackConfigurer") >= 0);

				assertTrue("Debe encontrarse traza de fin de configuración de logback",
						strSalidaEstandar.indexOf("Ends the initialization of system logs") >= 0);

			} catch (Exception e) {
				fail("Exception al comprobar el archivo de log [" + fileSalidaEstandar + "]");
			} finally {
				if (salidaEstandar != null) {
					salidaEstandar.close();
				}
			}

			String fileUdaTrazas = "udaTrazas_x38_WLTEST.log";
			FileInputStream udaTrazas = null;
			try {
				udaTrazas = new FileInputStream(logPath + "/logging/" + fileUdaTrazas);
				ByteArray bUdaTrazas = new ByteArray(udaTrazas.available());
				udaTrazas.read(bUdaTrazas.getBytes());
				String strSalidaEstandar = new String(bUdaTrazas.getBytes());

				assertTrue("Debe encontrarse trazas uda", strSalidaEstandar.indexOf("com.ejie.x38") >= 0);
			} catch (Exception e) {
				fail("Exception al comprobar el archivo de log [" + fileUdaTrazas + "]");
			} finally {
				if (udaTrazas != null) {
					udaTrazas.close();
				}
			}

			String fileAppTrazas = "x38Trazas_x38_WLTEST.log";
			FileInputStream appTrazas = null;
			try {
				appTrazas = new FileInputStream(logPath + "/logging/" + fileAppTrazas);
				ByteArray bAppTrazas = new ByteArray(appTrazas.available());
				appTrazas.read(bAppTrazas.getBytes());
				String strSalidaEstandar = new String(bAppTrazas.getBytes());

				assertTrue("Deben encontrarse trazas de la app de test",
						strSalidaEstandar.indexOf("com.ejie.x38") >= 0);
			} catch (Exception e) {
				fail("Exception al comprobar el archivo de log [" + fileAppTrazas + "]");
			} finally {
				if (appTrazas != null) {
					appTrazas.close();
				}
			}

			mockMvc.perform(get("/i18n/test"));
		} catch (Exception e) {
			fail("Exception al comprobar los archivos de trazas");
		}
	}

}
