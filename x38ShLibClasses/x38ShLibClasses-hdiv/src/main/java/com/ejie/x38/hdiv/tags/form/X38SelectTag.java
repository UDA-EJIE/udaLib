package com.ejie.x38.hdiv.tags.form;

import javax.servlet.jsp.JspException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.tags.form.SelectTag;

@SuppressWarnings("serial")
public class X38SelectTag extends SelectTag {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(X38SelectTag.class);

	private static final Object EMPTY = new Object();

	protected Object getItems() {
		
		Object items = super.getItems();
		if (items == null || items == EMPTY) {
			try {
				processFieldValue(getName(), null, "option");
			}catch(JspException e){
				LOGGER.debug("Cannot register select input to state. Cannot get its name.");
			}
		}
		
		return items;
	}

}
