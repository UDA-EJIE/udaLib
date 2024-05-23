/*
* Copyright 2012 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.test.integration.control.old;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.ejie.x38.control.bind.annotation.RequestJsonBody;
import com.ejie.x38.test.model.Alumno;
import com.ejie.x38.test.model.Comarca;
import com.ejie.x38.test.model.Departamento;
import com.ejie.x38.test.model.UploadBean;

/**
 * PatronesController
 * 
 * @author UDA
 */
@Controller
@RequestMapping(value = "/form")
public class FormController {

	private static final Logger logger = LoggerFactory.getLogger(FormController.class);

	@Autowired
//	private Properties appConfiguration;

	@Resource
	private ReloadableResourceBundleMessageSource messageSource;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	@RequestMapping(value = "test", method = RequestMethod.GET)
	public @ResponseBody Object getTest() {

		Map<String, String> mapa = new HashMap<String, String>();

		mapa.put("respuesta", "ok");

		return mapa;
	}

	/**
	 * MAINT (Usuarios) [form.jsp]
	 */
	// Form http submit
	@RequestMapping(value = "simple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//		@RequestMapping(value = "/createCloudCredential", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)

	public @ResponseBody Object getFormHttp(@RequestBody Alumno alumno) {

//			MessageWriter messageWriter = new MessageWriter();
//			
//			messageWriter.startMessageList();
//			messageWriter.addMessage("El formulario se ha enviado correctamente.");
//			messageWriter.addMessage("Esta es la representación JSON del objeto recibido:");
//			messageWriter.startSubLevel();
//			messageWriter.addMessage(new JSONObject(alumno).toString());
//			messageWriter.endSubLevel();
//			messageWriter.endMessageList();

		return alumno;
	}

	@ModelAttribute(value = "alumno")
	public Alumno addAlumno(@RequestParam(value = "alumno.apellido1", required = false) String apellido1,
			@RequestParam(value = "alumno.apellido2", required = false) String apellido2,
			@RequestParam(value = "alumno.nombre", required = false) String nombre,

			HttpServletRequest request) {
		Alumno alumnoBind = new Alumno();

		alumnoBind.setNombre(nombre);
		alumnoBind.setApellido1(apellido1);
		alumnoBind.setApellido2(apellido2);

		return alumnoBind;
	}

	// Form ajax submit
	@RequestMapping(value = "multientidad", method = RequestMethod.POST)
	public @ResponseBody Object getFormmMultientidades(@RequestJsonBody(param = "alumno") Alumno alumno,
			@RequestJsonBody(param = "departamento") Departamento departamento) {

		Map<String, Object> mapRetorno = new HashMap<String, Object>();

		mapRetorno.put("alumno", alumno);
		mapRetorno.put("departamento", departamento);

		return mapRetorno;
	}

	// Form ajax submit
	@RequestMapping(value = "multientidadesMismoTipo", method = RequestMethod.POST)
	public @ResponseBody Object getFormmMultientidadesMismoTipo(@RequestJsonBody(param = "comarca1") Comarca comarca1,
			@RequestJsonBody(param = "comarca2") Comarca comarca2,
			@RequestJsonBody(param = "comarca3") Comarca comarca3) {

		Map<String, Object> mapRetorno = new HashMap<String, Object>();

		mapRetorno.put("comarca1", comarca1);
		mapRetorno.put("comarca2", comarca2);
		mapRetorno.put("comarca3", comarca3);

		return mapRetorno;

	}

	@RequestMapping(value = "subidaArchivos", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Object addFormSimple(@ModelAttribute UploadBean uploadBean,
			@RequestParam(value = "fotoPadre", required = false) MultipartFile fotoPadre,
			@RequestParam(value = "fotoMadre", required = false) MultipartFile fotoMadre, HttpServletResponse response,
			Principal principal) throws IOException {

		FormController.logger.info("[POST][/form/subidaArchivos]addFormSimple");

//			if(fotoPadre!=null && !fotoPadre.isEmpty()){
//				uploadService.saveToDisk(fotoPadre, appConfiguration.getProperty("fileUpload.path"));
//			}
//			if(fotoMadre!=null && !fotoMadre.isEmpty()){
//				uploadService.saveToDisk(fotoMadre, appConfiguration.getProperty("fileUpload.path"));
//			}
		throw new MaxUploadSizeExceededException(22222);

//			if (fotoPadre != null && fotoPadre.getName().equals("fotoPadre") && fotoPadre.getOriginalFilename().equals("file1.txt")){
//				
//				return TestMessages.REQUEST_OK;
//				
//			}else{
//				return TestMessages.REQUEST_FAIL;
//			}

	}

}
