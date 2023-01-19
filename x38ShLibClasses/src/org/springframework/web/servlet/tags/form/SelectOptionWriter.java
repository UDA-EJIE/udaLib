package org.springframework.web.servlet.tags.form;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

public class SelectOptionWriter extends OptionWriter{

	private static RequestDataValueProcessor dataValueProcessor;
	
	private String name;
	
	public static void setRequestDataValueProcessor(RequestDataValueProcessor processor) {
		dataValueProcessor = processor;
	}
	
	public SelectOptionWriter(String name, Object optionSource, BindStatus bindStatus, String valueProperty, String labelProperty, boolean htmlEscape) {
		super(optionSource, bindStatus, valueProperty, labelProperty, htmlEscape);
		this.name = name; 
	}
	
	@Override
	protected String processOptionValue(String resolvedValue) {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			if (dataValueProcessor != null && request instanceof HttpServletRequest) {
				resolvedValue = dataValueProcessor.processFormFieldValue((HttpServletRequest) request, name, resolvedValue, "option");
			}
			return resolvedValue;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
