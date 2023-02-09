
package com.ejie.x38.hdiv.tags.form;

import org.springframework.web.servlet.tags.form.HiddenInputTag;


@SuppressWarnings("serial")
public class X38HiddenInputTag extends HiddenInputTag {
	
	public void setDynamicAttribute( String uri, String localName, Object value) throws javax.servlet.jsp.JspException {
		org.springframework.web.servlet.support.RequestContext ctx = (org.springframework.web.servlet.support.RequestContext) this.pageContext.getAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE);
		if("value".equals(localName)) {
			if (ctx != null) {
				org.springframework.web.servlet.support.RequestDataValueProcessor processor = ctx.getRequestDataValueProcessor();
				javax.servlet.ServletRequest request = this.pageContext.getRequest();
				if (processor != null && (request instanceof javax.servlet.http.HttpServletRequest)) {
					processor.processFormFieldValue((javax.servlet.http.HttpServletRequest) request, getPath(), String.valueOf(value), "hidden");
				}
			}
		}
		super.setDynamicAttribute(uri, localName, value);
	}
}
