package com.ejie.x38.hdiv.tag;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

public class HiddenInputTag extends org.springframework.web.servlet.tags.form.HiddenInputTag {

	private static final long serialVersionUID = 5630289178101940264L;

	private static final String VALUE_FIELD = "value";
	
	@Override
	public void setDynamicAttribute(final String uri, final String localName, final Object value) throws JspException {
		RequestContext ctx = (RequestContext) this.pageContext.getAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE);
		if(VALUE_FIELD.equals(localName)) {
			if (ctx != null) {
				RequestDataValueProcessor processor = ctx.getRequestDataValueProcessor();
				ServletRequest request = this.pageContext.getRequest();
				if (processor != null && (request instanceof HttpServletRequest)) {
					processor.processFormFieldValue((HttpServletRequest) request, getPath(), String.valueOf(value), "hidden");
				}
			}
		}
		
		super.setDynamicAttribute(uri, localName, value);
	}
	
}
