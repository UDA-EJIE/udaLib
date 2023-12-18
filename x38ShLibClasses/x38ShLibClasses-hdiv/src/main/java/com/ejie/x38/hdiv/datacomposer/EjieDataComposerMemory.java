package com.ejie.x38.hdiv.datacomposer;

import com.ejie.hdiv.context.RequestContextHolder;
import com.ejie.hdiv.dataComposer.DataComposerMemory;
import com.ejie.hdiv.state.IParameter;
import com.ejie.hdiv.state.IState;

public class EjieDataComposerMemory extends DataComposerMemory {
	
	public EjieDataComposerMemory(RequestContextHolder requestContext) {
		super(requestContext);
	}
	
	public String resetAndCompose(String parameterName, String value, boolean editable) {
		IState state = getStates().peek();

		IParameter parameter = state.getParameter(parameterName);
		try {
			parameter.getValues().clear();
		} catch (Exception e) {
			// No action needed.
		}
		
		return super.compose(parameterName, value, editable);
	}	
}
