/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.JspException;

/**
 * Data-binding aware JSP tag for rendering a hidden HTML '{@code input}' field
 * containing the databound value.
 *
 * <p>Example (binding to 'name' property of form backing object):
 * <pre class="code>
 * &lt;form:hidden path=&quot;name&quot;/&gt;
 * </pre>
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 2.0
 */
@SuppressWarnings("serial")
public class HiddenInputTag extends AbstractHtmlElementTag {

	/**
	 * The name of the '{@code disabled}' attribute.
	 */
	public static final String DISABLED_ATTRIBUTE = "disabled";

	private boolean disabled;


	/**
	 * Set the value of the '{@code disabled}' attribute.
	 * May be a runtime expression.
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Get the value of the '{@code disabled}' attribute.
	 */
	public boolean isDisabled() {
		return this.disabled;
	}


	/**
	 * Flags "type" as an illegal dynamic attribute.
	 */
	@Override
	protected boolean isValidDynamicAttribute(String localName, Object value) {
		return !"type".equals(localName);
	}

	/**
	 * Writes the HTML '{@code input}' tag to the supplied {@link TagWriter} including the
	 * databound value.
	 * @see #writeDefaultAttributes(TagWriter)
	 * @see #getBoundValue()
	 */
	@Override
	protected int writeTagContent(TagWriter tagWriter) throws JspException {
		tagWriter.startTag("input");
		writeDefaultAttributes(tagWriter);
		tagWriter.writeAttribute("type", "hidden");
		if (isDisabled()) {
			tagWriter.writeAttribute(DISABLED_ATTRIBUTE, "disabled");
		}
		String value = getDisplayString(getBoundValue(), getPropertyEditor());
		tagWriter.writeAttribute("value", processFieldValue(getName(), value, "hidden"));
		tagWriter.endTag();
		return SKIP_BODY;
	}

	
	public void setDynamicAttribute( String uri, String localName, Object value) throws javax.servlet.jsp.JspException {
			System.out.println("***************XAS************setDynamicAttribute");
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
