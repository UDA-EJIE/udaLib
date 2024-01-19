package com.ejie.x38.validation;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ejie.x38.util.DateTimeManager;

@Controller
@RequestMapping (value = "/validate")
public class ValidationController {
	
	@Autowired
	private ValidationManager validationManager;
	
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Object validate(@RequestParam(value="bean",required=false) String bean, 
			@RequestParam(value="property",required=false) String property, 
			@RequestParam(value="value",required=false) String value, 
			HttpServletRequest request, HttpServletResponse response){
		

		Locale locale = LocaleContextHolder.getLocale();
		
		try {
			String result = validationManager.validateProperty(bean, property, value, locale);
			
			response.setContentType("text/javascript;charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Expires", DateTimeManager.getHttpExpiredDate());
			
			if (result == null){
	            response.setStatus(HttpServletResponse.SC_OK);		
			}else if (result!=null && !result.equals("error!")){
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				response.getWriter().write(result);
			}else{
				throw new RuntimeException("error!");
			}
		} catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
				response.getWriter().write("Error in the validate. The structure or morphology of the data is incorrect, review the data sent.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
		
	}

}
