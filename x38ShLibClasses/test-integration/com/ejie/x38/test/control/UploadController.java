package com.ejie.x38.test.control;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ejie.x38.json.MessageWriter;
import com.ejie.x38.test.bean.UploadBean;

@Controller
@RequestMapping(value = "/upload")
public class UploadController {

	@RequestMapping(value="test", method = RequestMethod.GET)
	public @ResponseBody Object getTest(){
		
		Map<String, String> mapa = new HashMap<String, String>();
		
		mapa.put("respuesta", "ok");
		
		return mapa;
	}
	
	@RequestMapping(method = RequestMethod.GET)
    public @ResponseBody String main() {
        return "help";
    }
	
	@RequestMapping(value="form/subidaArchivos", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Object addFormSimple(
			@Validated @RequestBody UploadBean usuario,
			@RequestParam(value="fotoPadre", required=false) MultipartFile fotoPadre,
			@RequestParam(value="fotoMadre", required=false) MultipartFile fotoMadre,
			HttpServletResponse response) throws IOException {
		
//		if(fotoPadre!=null && !fotoPadre.isEmpty()){
//			uploadService.saveToDisk(fotoPadre, appConfiguration.getProperty("fileUpload.path"));
//		}
//		if(fotoMadre!=null && !fotoMadre.isEmpty()){
//			uploadService.saveToDisk(fotoMadre, appConfiguration.getProperty("fileUpload.path"));
//		}
		
		MessageWriter messageWriter = new MessageWriter();
		
		messageWriter.startMessageList();
		messageWriter.addMessage("Las entidades se han enviado correctamente");
		messageWriter.endMessageList();
		
		ServletOutputStream servletOutputStream = response.getOutputStream();
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		servletOutputStream.print(messageWriter.toString());
		response.flushBuffer();
		
		
//		throw new MaxUploadSizeExceededException(333333);
		return null;
	}
	
}
