package com.ejie.x38.control.exception.handler;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.ModelAndView;

import com.ejie.x38.util.IframeXHREmulationUtils;

public class MvcExceptionHandlerHelper {

	public ModelAndView processException(Exception ex, 
			HttpServletRequest request, HttpServletResponse response,
			String content, int statusCode) throws IOException {

		if (this.isIframeEmulated(request)){
//			response.setContentType("text/html");
//			response.setCharacterEncoding("UTF-8");
			this.writeToResponse(response, content, statusCode);
			
			return null;
			
		} else if (this.isAjax(request)) {
			//AJAX request;

			response.setContentLength(content.getBytes(Charset.forName(response.getCharacterEncoding())).length);
			this.writeToResponse(response, content, statusCode);
			
			return null;
		} else if(ex.getClass().equals(AccessDeniedException.class)) {
			return MvcExceptionHandler.handleAccessDenied(ex, request, response);
		} else{
			//Non-AJAX request
			return MvcExceptionHandler.handle(ex, request, response);
		}
		
	}
	
	protected boolean isIframeEmulated(HttpServletRequest request){
		
		return IframeXHREmulationUtils.isIframeEmulationRequired(request);
		
	}
	
	protected boolean isAjax(HttpServletRequest request){
		
		return request.getHeaders("X-Requested-With").hasMoreElements();
		
	}

	private void writeToResponse(HttpServletResponse response, String content, int statusCode) throws IOException{
		
		response.setStatus(statusCode);
		response.getWriter().write(content);
		response.flushBuffer();
		
	}
	
}
